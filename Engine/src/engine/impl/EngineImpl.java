package engine.impl;

import engine.api.Engine;
import engine.entity.cell.*;
import engine.entity.dto.CellDto;
import engine.entity.dto.RowDto;
import engine.entity.range.Range;
import engine.entity.sheet.Row;
import engine.entity.sheet.api.Sheet;
import engine.entity.sheet.SheetDimension;
import engine.entity.dto.SheetDto;
import engine.entity.sheet.impl.SheetImpl;
import engine.entity.sheet.SheetManager;
import engine.exception.file.FileNotExistException;
import engine.exception.file.InvalidFileTypeException;
import engine.entity.cell.CellConnectionsGraph;
import engine.exception.range.ColumnIsNotPartOfRangeException;
import engine.file.SheetFilesManager;
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
    private final SheetFilesManager sheetFilesManager = new SheetFilesManager();
    private boolean isDataLoaded = false;

    private SheetDto createSheetDto(Sheet sheet) {
        Map<CellPositionInSheet, CellDto> position2cell;
        position2cell = new HashMap<>();

        for (Map.Entry<CellPositionInSheet, Cell> entry: sheet.getPosition2cell().entrySet()) {
            CellDto cellDto = getCellDto(entry.getValue());
            position2cell.put(entry.getKey(), cellDto);
        }

        return new SheetDto(position2cell);
    }

    @Override
    public EffectiveValue getEffectiveValueForDisplay(EffectiveValue originalEffectiveValue) {
        String effectiveValueStr;
        EffectiveValue effectiveValueForDisplay = null;

        if (originalEffectiveValue != null) {
            effectiveValueStr = originalEffectiveValue.getValue().toString();
            if (effectiveValueStr.matches("-?\\d+(\\.\\d+)?")) {
                DecimalFormat formatter = new DecimalFormat("#,###.##");
                effectiveValueForDisplay = new EffectiveValue(CellType.NUMERIC, formatter.format(new BigDecimal(effectiveValueStr)));
            } else if (effectiveValueStr.equalsIgnoreCase("true") || effectiveValueStr.equalsIgnoreCase("false")) {
                effectiveValueForDisplay = new EffectiveValue(CellType.BOOLEAN, effectiveValueStr.toUpperCase());
            } else {
                effectiveValueForDisplay = new EffectiveValue(CellType.STRING, effectiveValueStr);
            }
        }

        return effectiveValueForDisplay;
    }

    @Override
    public boolean isDataLoaded() {
        return isDataLoaded;
    }

    @Override
    public int getCurrentSheetVersion(String sheetName) {
        return sheetFilesManager.getSheetManager(sheetName).getCurrentVersion();
    }

    @Override
    public CellDto findCellInSheet(String sheetName, int row, int column, int sheetVersion) {
        SheetDto sheetDto = getSheet(sheetName, sheetVersion);
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);

        return sheetDto.getCell(cellPosition);
    }

    @Override
    public SheetDto getSheet(String sheetName, int sheetVersion) {
        Sheet sheet = sheetFilesManager.getSheetManager(sheetName).getSheetByVersion(sheetVersion);

        return createSheetDto(sheet);
    }

    @Override
    public int getLastCellVersion(String sheetName, int row, int column) {
        Sheet sheet = sheetFilesManager.getSheetManager(sheetName)
                .getSheetByVersion(sheetFilesManager.getSheetManager(sheetName).getCurrentVersion());
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        Cell cell = sheet.getCell(cellPosition);

        if (cell == null) {
            return 0;
        }

        return cell.getLastUpdatedInVersion();
    }

    @Override
    public Set<CellPositionInSheet> getInfluencedBySet(String sheetName, int row, int column, int sheetVersion) {
        return findCellInSheet(sheetName, row, column, sheetVersion).getInfluencedBy();
    }

    @Override
    public Set<CellPositionInSheet> getInfluencesSet(String sheetName, int row, int column, int sheetVersion) {
        return findCellInSheet(sheetName, row, column, sheetVersion).getInfluences();
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

    @Override //THE FIRST UPDATE
    public CellDto updateSheetCell(String sheetName, int row, int column, String newOriginalValue) {
        Sheet clonedSheet = sheetFilesManager.getSheetManager(sheetName)
                .getSheetByVersion(sheetFilesManager.getSheetManager(sheetName).getCurrentVersion()).clone();
        Set<CellPositionInSheet> visitedCellPositions = new HashSet<>();
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        int cellsUpdatedCounter = 1;

        setCellInfo(clonedSheet, cellPosition, newOriginalValue);
        updateInfluencedByCell(clonedSheet, cellPosition, visitedCellPositions);
        cellsUpdatedCounter += visitedCellPositions.size();
        clonedSheet.setUpdatedCellsCount(cellsUpdatedCounter);
        sheetFilesManager.getSheetManager(sheetName).addNewSheet(clonedSheet);

        return findCellInSheet(sheetName, row, column, getCurrentSheetVersion(sheetName));
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
    public String loadFile(String filePath) throws Exception {
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

        return addNewSheetManagerFromJaxbSheet(jaxbSheet);
    }

    @Override
    public String loadFile(InputStream fileInputStream) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        STLSheet jaxbSheet = (STLSheet) jaxbUnmarshaller.unmarshal(fileInputStream);

        return addNewSheetManagerFromJaxbSheet(jaxbSheet);
    }

    @Override
    public int getFilesAmount() {
        return sheetFilesManager.getSheetManagersCount();
    }

    private String addNewSheetManagerFromJaxbSheet(STLSheet jaxbSheet) {
        List<STLRange> ranges = jaxbSheet.getSTLRanges().getSTLRange();

        // Creating sheet manager
        SheetManager sheetManager = getNewSheetManager(jaxbSheet);
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
        sheetFilesManager.addSheetManager(jaxbSheet.getName(), sheetManager);
        isDataLoaded = true;
        return jaxbSheet.getName();
    }

    private SheetManager getNewSheetManager(STLSheet jaxbSheet) {
        int numOfRows = jaxbSheet.getSTLLayout().getRows();
        int numOfColumns = jaxbSheet.getSTLLayout().getColumns();
        int rowHeight = jaxbSheet.getSTLLayout().getSTLSize().getRowsHeightUnits();
        int columnWidth = jaxbSheet.getSTLLayout().getSTLSize().getColumnWidthUnits();

        SheetDimension sheetDimension = new SheetDimension(numOfRows, numOfColumns, rowHeight, columnWidth);

        return new SheetManager(sheetDimension);
    }

    @Override
    public CellPositionInSheet getCellPositionInSheet(String sheetName, int row, int column) {
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        sheetFilesManager.getSheetManager(sheetName).validatePositionInSheetBounds(cellPosition);

        return cellPosition;
    }

    @Override
    public CellPositionInSheet getCellPositionInSheet(String sheetName, String position) {
        CellPositionInSheet cellPosition = PositionFactory.createPosition(position);

        return getCellPositionInSheet(sheetName, cellPosition.getRow(), cellPosition.getColumn());
    }

    @Override
    public int getNumOfSheetRows(String sheetName) {
        return sheetFilesManager.getSheetManager(sheetName).getSheetDimension().getNumOfRows();
    }

    @Override
    public int getNumOfSheetColumns(String sheetName) {
        return sheetFilesManager.getSheetManager(sheetName).getSheetDimension().getNumOfColumns();
    }

    @Override
    public int getSheetRowHeight(String sheetName) {
        return sheetFilesManager.getSheetManager(sheetName).getSheetDimension().getRowHeight();
    }

    @Override
    public int getSheetColumnWidth(String sheetName) {
        return sheetFilesManager.getSheetManager(sheetName).getSheetDimension().getColumnWidth();
    }

    @Override
    public Range getRangeByName(String sheetName, String rangeName) {
        return sheetFilesManager.getSheetManager(sheetName).getRangesManager().getRangeByName(rangeName);
    }

    @Override
    public List<String> getRangeNames(String sheetName) {
        List<String> rangeNames = new ArrayList<>();
        sheetFilesManager.getSheetManager(sheetName).getRangesManager().getName2Range()
                .forEach((name, range) -> rangeNames.add(name));

        return rangeNames;
    }

    public Range createRange(String sheetName, String rangeName, CellPositionInSheet fromPosition, CellPositionInSheet toPosition) {
        return sheetFilesManager.getSheetManager(sheetName).createRange(rangeName, fromPosition, toPosition);
    }

    public void deleteRange(String sheetName, String rangeName) {
        sheetFilesManager.getSheetManager(sheetName).getRangesManager().deleteRange(rangeName);
    }

    private void validateColumnsInRange(Range range, Set<String> columns) {
        columns.forEach((colSortedBy) -> {
            List<String> cellsInColumn = range.getIncludedColumns().stream()
                    .filter((includedCol) -> includedCol.equals(colSortedBy))
                    .toList();
            if (cellsInColumn.isEmpty()) {
                throw new ColumnIsNotPartOfRangeException(colSortedBy, range);
            }
        });
    }

    @Override
    public LinkedList<RowDto> getSortedRowsSheet(String sheetName, Range rangeToSort, Set<String> columnsSortedBy) {
        validateColumnsInRange(rangeToSort, columnsSortedBy);
        Sheet inWorkSheet = sheetFilesManager.getSheetManager(sheetName)
                .getSheetByVersion(getCurrentSheetVersion(sheetName)).clone();

        // Extract rows from the sheet based on the given range
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

        return getRowDto(sortedNumericRows);
    }

    private LinkedList<RowDto> getRowDto(List<Row> rows) {
        LinkedList<RowDto> rowsDto = new LinkedList<>();

        for (Row row : rows) {
            RowDto rowDto = new RowDto(row.getRowNumber(), new HashMap<>());
            for (Map.Entry<String, Cell> column2cell : row.getCells().entrySet()) {
                CellDto cellDto = getCellDto(column2cell.getValue());
                rowDto.getCells().put(column2cell.getKey(), cellDto);
            }
            rowsDto.add(rowDto);
        }

        return rowsDto;
    }

    private CellDto getCellDto(Cell cell) {
        CellDto cellDto;
        if (cell == null) {
            EffectiveValue effectiveValue = new EffectiveValue(CellType.UNKNOWN, "");
            cellDto = new CellDto("", effectiveValue, effectiveValue, new LinkedHashSet<>(), new LinkedHashSet<>());
        } else {
            EffectiveValue effectiveValue = cell.getEffectiveValue();
            cellDto = new CellDto(cell.getOriginalValue(), effectiveValue, getEffectiveValueForDisplay(effectiveValue), cell.getInfluencedBy(), cell.getInfluences());
        }

        return cellDto;
    }

    private List<Row> extractRowsInRange(Sheet sheet, Range range) {
        List<Row> rows = new LinkedList<>();
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

    @Override
    public Map<String, Set<EffectiveValue>> getUniqueColumnValuesByRange(String sheetName, Range range, Set<String> columns) {
        validateColumnsInRange(range, columns);
        Map<String, Set<EffectiveValue>> column2uniqueEffectiveValues = new HashMap<>();
        columns.forEach((column) -> column2uniqueEffectiveValues.put(column, new LinkedHashSet<>()));

        range.getIncludedPositions().forEach((cellPosition) -> {
            Set<EffectiveValue> uniqueColumnValues = column2uniqueEffectiveValues.get(CellPositionInSheet.parseColumn(cellPosition.getColumn()));
            if (uniqueColumnValues != null) {
                EffectiveValue originalEffectiveValue = sheetFilesManager.getSheetManager(sheetName)
                        .getSheetByVersion(getCurrentSheetVersion(sheetName)).getCellEffectiveValue(cellPosition);
                EffectiveValue effectiveValueForDisplay = getEffectiveValueForDisplay(originalEffectiveValue);
                uniqueColumnValues.add(effectiveValueForDisplay);
            }
        });

        return column2uniqueEffectiveValues;
    }

    @Override
    public LinkedList<RowDto> getFilteredRowsSheet(String sheetName, Range rangeToFilter, Map<String, Set<String>> column2effectiveValuesFilteredBy) {
        List<Row> rowsToFilter = extractRowsInRange(sheetFilesManager.getSheetManager(sheetName)
                .getSheetByVersion(getCurrentSheetVersion(sheetName)), rangeToFilter);

        // Filter out rows by the columns
        List<Row> filteredRows = rowsToFilter.stream()
            .filter((row) -> {
                // for each column filter
                for (Map.Entry<String, Set<String>> entry : column2effectiveValuesFilteredBy.entrySet()) {
                    Cell cellInColumn = row.getCells().get(entry.getKey());
                    // if an empty cell and empty cell value (which is null) is one of the values to filter
                    if (cellInColumn == null || cellInColumn.getEffectiveValue() == null) {
                        if (!entry.getValue().contains(null)) {
                            return false;
                        }
                    }
                    else {
                        EffectiveValue originalEffectiveValue = cellInColumn.getEffectiveValue();
                        EffectiveValue effectiveValueForDisplay = getEffectiveValueForDisplay(originalEffectiveValue);
                        if (!entry.getValue().contains(effectiveValueForDisplay.getValue().toString())) {
                            return false;
                        }
                    }
                }
                return true;
            }).collect(Collectors.toCollection(LinkedList::new));

        return getRowDto(filteredRows);
    }



    @Override
    public SheetDto getSheetAfterDynamicAnalysisOfCell(String sheetName, CellPositionInSheet cellPosition, double cellOriginalValue) {
        SheetDto dynamicAnalysedSheetDto;
        Sheet inWorkSheet = sheetFilesManager.getSheetManager(sheetName)
                .getSheetByVersion(getCurrentSheetVersion(sheetName)).clone();
        Set<CellPositionInSheet> visitedCellPositions = new HashSet<>();

        String originalValueStr = String.valueOf(cellOriginalValue);
        setCellInfo(inWorkSheet, cellPosition, originalValueStr);
        updateInfluencedByCell(inWorkSheet, cellPosition, visitedCellPositions);

        dynamicAnalysedSheetDto = createSheetDto(inWorkSheet);
        return dynamicAnalysedSheetDto;
    }
}