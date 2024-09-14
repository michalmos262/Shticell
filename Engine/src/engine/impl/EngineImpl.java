package engine.impl;

import engine.api.Engine;
import engine.entity.cell.*;
import engine.entity.dto.CellDto;
import engine.entity.range.Range;
import engine.entity.sheet.Row;
import engine.entity.sheet.api.Sheet;
import engine.entity.sheet.SheetDimension;
import engine.entity.dto.SheetDto;
import engine.entity.sheet.impl.SheetImpl;
import engine.entity.sheet.SheetManager;
import engine.exception.file.FileAlreadyExistsException;
import engine.exception.file.FileNotExistException;
import engine.exception.file.InvalidFileTypeException;
import engine.entity.cell.CellConnectionsGraph;
import engine.jaxb.schema.generated.STLCell;
import engine.jaxb.schema.generated.STLCells;
import engine.jaxb.schema.generated.STLRange;
import engine.jaxb.schema.generated.STLSheet;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static engine.expression.impl.ExpressionEvaluator.evaluateArgument;

public class EngineImpl implements Engine {
    private SheetManager sheetManager;
    private boolean isDataLoaded = false;

    private SheetDto createSheetDto(Sheet sheet) {
        Map<CellPositionInSheet, CellDto> position2cell;
        position2cell = new HashMap<>();
        for (Map.Entry<CellPositionInSheet, Cell> entry: sheet.getPosition2cell().entrySet()) {
            Cell cell = entry.getValue();
            CellDto cellDto;
            if (cell == null) {
                EffectiveValue effectiveValue = new EffectiveValue(CellType.UNKNOWN, "");
                cellDto = new CellDto("", effectiveValue, effectiveValue, new LinkedHashSet<>(), new LinkedHashSet<>());
            }
            else {
                cellDto = new CellDto(cell.getOriginalValue(), cell.getEffectiveValue(), getEffectiveValueForDisplay(cell), cell.getInfluencedBy(), cell.getInfluences());
            }
            position2cell.put(entry.getKey(), cellDto);
        }

        return new SheetDto(position2cell);
    }

    public EffectiveValue getEffectiveValueForDisplay(Cell cell) {
        EffectiveValue effectiveValue;

        // if cell is not created yet
        if (cell.getEffectiveValue() == null) {
            effectiveValue = new EffectiveValue(CellType.STRING, "");
        } else {
            String effectiveValueStr = cell.getEffectiveValue().getValue().toString();
            if (effectiveValueStr.matches("-?\\d+(\\.\\d+)?")) {
                DecimalFormat formatter = new DecimalFormat("#,###.##");
                effectiveValue = new EffectiveValue(CellType.NUMERIC, formatter.format(new BigDecimal(effectiveValueStr)));
            } else if (effectiveValueStr.equalsIgnoreCase("true") || effectiveValueStr.equalsIgnoreCase("false")) {
                effectiveValue = new EffectiveValue(CellType.BOOLEAN, effectiveValueStr.toUpperCase());
            } else {
                effectiveValue = new EffectiveValue(CellType.STRING, effectiveValueStr);
            }
        }

        return effectiveValue;
    }

    @Override
    public boolean isDataLoaded() {
        return isDataLoaded;
    }

    @Override
    public String getSheetName() {
        return sheetManager.getName();
    }

    @Override
    public int getCurrentSheetVersion() {
        return sheetManager.getCurrentVersion();
    }

    @Override
    public CellDto findCellInSheet(int row, int column, int sheetVersion) {
        SheetDto sheetDto = getSheet(sheetVersion);
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);

        return sheetDto.getCell(cellPosition);
    }

    @Override
    public SheetDto getSheet(int sheetVersion) {
        Sheet sheet = sheetManager.getSheetByVersion(sheetVersion);

        return createSheetDto(sheet);
    }

    @Override
    public int getLastCellVersion(int row, int column) {
        Sheet sheet = sheetManager.getSheetByVersion(sheetManager.getCurrentVersion());
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        Cell cell = sheet.getCell(cellPosition);

        if (cell == null) {
            return 0;
        }

        return cell.getLastUpdatedInVersion();
    }

    @Override
    public Set<CellPositionInSheet> getInfluencedBySet(int row, int column, int sheetVersion) {
        return findCellInSheet(row, column, sheetVersion).getInfluencedBy();
    }

    @Override
    public Set<CellPositionInSheet> getInfluencesSet(int row, int column, int sheetVersion) {
        return findCellInSheet(row, column, sheetVersion).getInfluences();
    }

    public EffectiveValue handleEffectiveValue(Sheet sheet, CellPositionInSheet cellPosition, String originalValue) {
        EffectiveValue effectiveValue;
        Set<CellPositionInSheet> influencingCellPositions = new LinkedHashSet<>();
        Set<String> usingRangeNames = new LinkedHashSet<>();
        effectiveValue = evaluateArgument(sheet, originalValue, influencingCellPositions, usingRangeNames);

        for (CellPositionInSheet influencingPosition : influencingCellPositions) {
            sheet.addCellConnection(influencingPosition, cellPosition);
        }
        for (String rangeName: usingRangeNames) {
            sheet.useRange(rangeName);
            sheet.getPosition2cell().get(cellPosition).addRangeNameUsed(rangeName);
        }

        return effectiveValue;
    }

    //RECURSIVE UPDATE
    private void updateInfluencedByCell(Sheet sheet, CellPositionInSheet InfluencerCellPosition, Set<CellPositionInSheet> visited) {
        Cell cell = sheet.getCell(InfluencerCellPosition);
        List<CellPositionInSheet> influencedCellPositions = new LinkedList<>(cell.getInfluences());

        for (CellPositionInSheet influencedByCellPosition : influencedCellPositions) {
            Cell influencedByCell = sheet.getCell(influencedByCellPosition);
            String originalValue = influencedByCell.getOriginalValue();
            EffectiveValue effectiveValue = handleEffectiveValue(sheet, influencedByCellPosition, originalValue);
            sheet.updateCell(influencedByCellPosition, originalValue, effectiveValue);
            visited.add(influencedByCellPosition);
            updateInfluencedByCell(sheet, influencedByCellPosition, visited);
        }
    }

    @Override
    //THE FIRST UPDATE
    public CellDto updateSheetCell(int row, int column, String newOriginalValue) {
        Sheet clonedSheet = sheetManager.getSheetByVersion(sheetManager.getCurrentVersion()).clone();
        Set<CellPositionInSheet> visitedCellPositions = new HashSet<>();
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        int cellsUpdatedCounter = 1;

        setCellInfo(clonedSheet, cellPosition, newOriginalValue);
        updateInfluencedByCell(clonedSheet, cellPosition, visitedCellPositions);
        cellsUpdatedCounter += visitedCellPositions.size();
        clonedSheet.setUpdatedCellsCount(cellsUpdatedCounter);
        sheetManager.addNewSheet(clonedSheet);

        return findCellInSheet(row, column, getCurrentSheetVersion());
    }

    private void setCellInfo(Sheet sheet, CellPositionInSheet cellPosition, String originalValue) {
        try {
            Cell cellInUpdate = sheet.getCell(cellPosition);
            EffectiveValue effectiveValue;

            if (cellInUpdate == null) { // need to create new cell
                sheet.createNewCell(cellPosition, originalValue);
            } else {
                // had to move the set to linked list for keeping on order
                Set<CellPositionInSheet> influencedByCellPositions = new LinkedHashSet<>(cellInUpdate.getInfluencedBy());
                for (CellPositionInSheet influencingCellPosition: influencedByCellPositions) {
                    sheet.removeCellConnection(influencingCellPosition, cellPosition);
                }
                for (String rangeName: cellInUpdate.getRangeNamesUsed()) {
                    cellInUpdate.removeRangeNameUsed(rangeName);
                    sheet.unUseRange(rangeName);
                }
            }
            effectiveValue = handleEffectiveValue(sheet, cellPosition, originalValue);
            sheet.updateCell(cellPosition, originalValue, effectiveValue);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error occurred on update position " + cellPosition + ": " + e.getMessage());
        }
    }

    private int createCellsFromFile(Sheet sheet, STLCells jaxbCells, SheetManager sheetManager) {
        int cellsUpdatedCounter = 0;

        // Create a graph of REF connections
        CellConnectionsGraph refConnectionsGraph = new CellConnectionsGraph(jaxbCells, sheetManager.getRangesManager());

        // Sort the graph topologically
        List<CellPositionInSheet> topologicalSortedGraph = refConnectionsGraph.sortTopologically();

        // Move jaxb cells list to map
        Map<CellPositionInSheet, STLCell> jaxbCellsMap = new HashMap<>();
        for (STLCell jaxbCell: jaxbCells.getSTLCell()) {
            CellPositionInSheet cellPosition = PositionFactory.createPosition(jaxbCell.getRow(), jaxbCell.getColumn());
            jaxbCellsMap.put(cellPosition, jaxbCell);
        }

        // Create the real cells by the topological sort
        for (CellPositionInSheet cellPositionInSheet : topologicalSortedGraph) {
            String originalValue = jaxbCellsMap.get(cellPositionInSheet).getSTLOriginalValue();
            setCellInfo(sheet, cellPositionInSheet, originalValue);
            cellsUpdatedCounter++;
        }

        return cellsUpdatedCounter;
    }

    @Override
    public void loadFile(String filePath) throws Exception {
        File file = new File(filePath);

        if (!(file.exists() && file.isFile())) {
            throw new FileNotExistException(filePath);
        }
        else if (!file.getName().endsWith("." + SUPPORTED_FILE_TYPE)) {
            throw new InvalidFileTypeException(filePath, SUPPORTED_FILE_TYPE.toUpperCase());
        }

        JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        STLSheet jaxbSheet = (STLSheet) jaxbUnmarshaller.unmarshal(file);
        List<STLRange> ranges = jaxbSheet.getSTLRanges().getSTLRange();

        // Creating sheet manager
        SheetManager sheetManager = getSheetManager(jaxbSheet);
        // Creating ranges
        for (STLRange range: ranges) {
            CellPositionInSheet fromPosition = PositionFactory.createPosition(range.getSTLBoundaries().getFrom());
            CellPositionInSheet toPosition = PositionFactory.createPosition(range.getSTLBoundaries().getTo());
            sheetManager.createRange(range.getName(), fromPosition, toPosition);
        }
        // Creating a sheet entity
        Sheet sheet = new SheetImpl(sheetManager);
        int cellsUpdatedCounter = createCellsFromFile(sheet, jaxbSheet.getSTLCells(), sheetManager);
        sheet.setUpdatedCellsCount(cellsUpdatedCounter);
        sheetManager.addNewSheet(sheet);
        this.sheetManager = sheetManager;
        isDataLoaded = true;
    }

    private static SheetManager getSheetManager(STLSheet jaxbSheet) {
        int numOfRows = jaxbSheet.getSTLLayout().getRows();
        int numOfColumns = jaxbSheet.getSTLLayout().getColumns();
        int rowHeight = jaxbSheet.getSTLLayout().getSTLSize().getRowsHeightUnits();
        int columnWidth = jaxbSheet.getSTLLayout().getSTLSize().getColumnWidthUnits();

        SheetDimension sheetDimension = new SheetDimension(numOfRows, numOfColumns, rowHeight, columnWidth);

        return new SheetManager(jaxbSheet.getName(), sheetDimension);
    }

    @Override
    public void writeSystemToFile(String fileName) throws IOException {
        String fullFileName = fileName + "." + SYSTEM_FILE_TYPE;
        File file = new File(fullFileName);

        if (file.isFile() && file.exists()) {
            throw new FileAlreadyExistsException(file.getAbsolutePath());
        }

        ObjectOutputStream out =
                new ObjectOutputStream(
                        new FileOutputStream(fullFileName));
        out.writeObject(this.sheetManager);
        out.flush();
    }

    @Override
    public void readSystemFromFile(String fileName) {
        try {
            ObjectInputStream in =
                new ObjectInputStream(
                        new FileInputStream(fileName));
            this.sheetManager = (SheetManager) in.readObject();
            isDataLoaded = true;
        } catch (Exception e) {
            throw new InvalidFileTypeException(fileName, SYSTEM_FILE_TYPE);
        }
    }

    @Override
    public Map<Integer, Integer> getVersion2updatedCellsCount() {
        Map<Integer, Integer> version2updatedCellsCount = new HashMap<>();

        sheetManager.getVersion2sheet().forEach((version, sheet) ->
                version2updatedCellsCount.put(version, sheet.getUpdatedCellsCount()));

        return version2updatedCellsCount;
    }

    @Override
    public Map<Integer, SheetDto> getVersion2sheet() {
        Map<Integer, SheetDto> version2sheet = new HashMap<>();

        sheetManager.getVersion2sheet().forEach((version, sheet) ->
                version2sheet.put(version, createSheetDto(sheet)));

        return version2sheet;
    }

    @Override
    public void validateSheetVersionExists(int version) {
        Map<Integer, Integer> version2updatedCellsCount = getVersion2updatedCellsCount();

        if (!version2updatedCellsCount.containsKey(version)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public CellPositionInSheet getCellPositionInSheet(int row, int column) {
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        sheetManager.validatePositionInSheetBounds(cellPosition);

        return cellPosition;
    }

    @Override
    public CellPositionInSheet getCellPositionInSheet(String position) {
        CellPositionInSheet cellPosition = PositionFactory.createPosition(position);

        return getCellPositionInSheet(cellPosition.getRow(), cellPosition.getColumn());
    }

    @Override
    public int getNumOfSheetRows() {
        return sheetManager.getSheetDimension().getNumOfRows();
    }

    @Override
    public int getNumOfSheetColumns() {
        return sheetManager.getSheetDimension().getNumOfColumns();
    }

    @Override
    public int getSheetRowHeight() {
        return sheetManager.getSheetDimension().getRowHeight();
    }

    @Override
    public int getSheetColumnWidth() {
        return sheetManager.getSheetDimension().getColumnWidth();
    }

    @Override
    public SheetDimension getSheetDimension() {
        return sheetManager.getSheetDimension();
    }

    @Override
    public Range getRangeByName(String rangeName) {
        return sheetManager.getRangesManager().getRangeByName(rangeName);
    }

    @Override
    public List<String> getRangeNames() {
        List<String> rangeNames = new ArrayList<>();
        sheetManager.getRangesManager().getName2Range().forEach((name, range) -> rangeNames.add(name));
        return rangeNames;
    }

    public void createRange(String name, CellPositionInSheet fromPosition, CellPositionInSheet toPosition) {
        sheetManager.createRange(name, fromPosition, toPosition);
    }

    public void deleteRange(String name) {
        sheetManager.getRangesManager().deleteRange(name);
    }

    @Override
    public SheetDto getSortedRowsSheet(Range rangeToSort, LinkedHashSet<String> columnsSortedBy) {
        Sheet inWorkSheet = sheetManager.getSheetByVersion(getCurrentSheetVersion()).clone();

        //TODO: check the columnsSortedBy are in the range

        // Extract rows from the map based on the given range
        List<Row> rows = extractRowsInRange(inWorkSheet, rangeToSort);

        // Filter out rows that have non-numeric values
        List<Row> sortedNumericRows = rows.stream()
                .filter(Row::hasNumericValues)
                .sorted((row1, row2) -> {
                    // Sort numeric rows by the given columns
                    for (String column : columnsSortedBy) {
                        int comparison = row1.compareTo(row2, column);
                        if (comparison != 0) {
                            return comparison;
                        }
                    }
                    return 0;
                }).collect(Collectors.toList());

        // Update the map with sorted rows
        updateSheetWithSortedRows(inWorkSheet, rangeToSort, sortedNumericRows);

        return createSheetDto(inWorkSheet);
    }

    private List<Row> extractRowsInRange(Sheet sheet, Range range) {
        List<Row> rows = new ArrayList<>();
        for (int rowNum = range.getFromPosition().getRow(); rowNum <= range.getToPosition().getRow(); rowNum++) {
            Row row = new Row(rowNum);
            for (int colNumber = range.getFromPosition().getColumn(); colNumber <= range.getToPosition().getColumn(); colNumber++) {
                CellPositionInSheet position = PositionFactory.createPosition(rowNum, colNumber);
                Cell cell = sheet.getCell(position);
                row.addCell(CellPositionInSheet.parseColumn(colNumber), cell);
            }
            rows.add(row);
        }
        return rows;
    }

    private void updateSheetWithSortedRows(Sheet sheet, Range rangeToSort, List<Row> sortedRows) {
        List<Row> rows = extractRowsInRange(sheet, rangeToSort);
        List<Row> numericRowsToUpdate = rows.stream().filter(Row::hasNumericValues).toList();
        for (int i = 0; i < numericRowsToUpdate.size(); i++) {
            Row sortedRow = sortedRows.get(i);
            Row rowToUpdateInSheet = numericRowsToUpdate.get(i);
            for (Map.Entry<String, Cell> sortedCellEntry : sortedRow.getCells().entrySet()) {
                CellPositionInSheet cellPosition = PositionFactory.createPosition(rowToUpdateInSheet.getRowNumber(), sortedCellEntry.getKey());
                sheet.getPosition2cell().put(cellPosition, sortedCellEntry.getValue());
            }
        }
    }
}