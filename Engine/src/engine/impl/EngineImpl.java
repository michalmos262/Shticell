package engine.impl;

import dto.cell.CellDto;
import dto.cell.CellPositionDto;
import dto.cell.CellTypeDto;
import dto.cell.EffectiveValueDto;
import dto.sheet.*;
import engine.api.Engine;
import engine.entity.cell.*;
import engine.entity.range.Range;
import engine.entity.sheet.Row;
import engine.entity.sheet.api.Sheet;
import engine.entity.sheet.SheetDimension;
import engine.entity.sheet.impl.SheetImpl;
import engine.entity.sheet.SheetManager;
import engine.entity.cell.CellConnectionsGraph;
import engine.exception.range.ColumnIsNotPartOfRangeException;
import engine.file.SheetFilesManager;
import engine.jaxb.schema.generated.STLCell;
import engine.jaxb.schema.generated.STLCells;
import engine.jaxb.schema.generated.STLRange;
import engine.jaxb.schema.generated.STLSheet;
import engine.user.permission.UserPermission;
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

    private SheetDto createSheetDto(Sheet sheet, String owner) {
        Map<CellPositionDto, CellDto> position2cell;
        position2cell = new HashMap<>();

        for (Map.Entry<CellPositionInSheet, Cell> entry: sheet.getPosition2cell().entrySet()) {
            CellDto cellDto = getCellDto(entry.getValue(), owner);
            position2cell.put(new CellPositionDto(entry.getKey().getRow(), entry.getKey().getColumn()), cellDto);
        }

        return new SheetDto(position2cell, sheet.getVersion());
    }

    @Override
    public EffectiveValueDto getEffectiveValueDtoForDisplay(EffectiveValueDto originalEffectiveValue) {
        String effectiveValueStr;
        EffectiveValueDto effectiveValueForDisplay = null;

        if (originalEffectiveValue != null) {
            effectiveValueStr = originalEffectiveValue.getValue().toString();
            if (effectiveValueStr.matches("-?\\d+(\\.\\d+)?")) {
                DecimalFormat formatter = new DecimalFormat("#,###.##");
                effectiveValueForDisplay = new EffectiveValueDto(CellTypeDto.NUMERIC, formatter.format(new BigDecimal(effectiveValueStr)));
            } else if (effectiveValueStr.equalsIgnoreCase("true") || effectiveValueStr.equalsIgnoreCase("false")) {
                effectiveValueForDisplay = new EffectiveValueDto(CellTypeDto.BOOLEAN, effectiveValueStr.toUpperCase());
            } else {
                effectiveValueForDisplay = new EffectiveValueDto(CellTypeDto.STRING, effectiveValueStr);
            }
        }

        return effectiveValueForDisplay;
    }

    private EffectiveValue getEffectiveValueForDisplay(EffectiveValue originalEffectiveValue) {
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
    public int getCurrentSheetVersion(String sheetName) {
        return sheetFilesManager.getSheetManager(sheetName).getCurrentVersion();
    }

    @Override
    public CellDto findCellInSheet(String sheetName, int row, int column, int sheetVersion) {
        SheetDto sheetDto = getSheet(sheetName, sheetVersion);
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);

        return sheetDto.getCell(new CellPositionDto(cellPosition.getRow(), cellPosition.getColumn()));
    }

    @Override
    public SheetDto getSheet(String sheetName, int sheetVersion) {
        Sheet sheet = sheetFilesManager.getSheetManager(sheetName).getSheetByVersion(sheetVersion);

        return createSheetDto(sheet, sheetFilesManager.getSheetManager(sheetName).getOwnerName());
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
    public Set<CellPositionDto> getInfluencedBySet(String sheetName, int row, int column, int sheetVersion) {
        return findCellInSheet(sheetName, row, column, sheetVersion).getInfluencedBy();
    }

    @Override
    public Set<CellPositionDto> getInfluencesSet(String sheetName, int row, int column, int sheetVersion) {
        return findCellInSheet(sheetName, row, column, sheetVersion).getInfluences();
    }

    private EffectiveValue handleEffectiveValue(Sheet sheet, CellPositionInSheet cellPosition, String originalValue, String updatedByName) {
        EffectiveValue effectiveValue;
        Set<CellPositionInSheet> influencingCellPositions = new LinkedHashSet<>();
        Set<String> usingRangeNames = new LinkedHashSet<>();
        effectiveValue = evaluateArgument(sheet, originalValue, influencingCellPositions, usingRangeNames);

        for (CellPositionInSheet influencingPosition : influencingCellPositions) {
            sheet.addCellConnection(influencingPosition, cellPosition, updatedByName);
        }
        for (String rangeName: usingRangeNames) {
            sheet.useRange(rangeName);
            sheet.getPosition2cell().get(cellPosition).addRangeNameUsed(rangeName);
        }

        return effectiveValue;
    }

    //RECURSIVE UPDATE
    private void updateInfluencedByCell(Sheet sheet, CellPositionInSheet InfluencerCellPosition, Set<CellPositionInSheet> visited, String updatedByName) {
        Cell cell = sheet.getCell(InfluencerCellPosition);
        List<CellPositionInSheet> influencedCellPositions = new LinkedList<>(cell.getInfluences());

        for (CellPositionInSheet influencedByCellPosition : influencedCellPositions) {
            Cell influencedByCell = sheet.getCell(influencedByCellPosition);
            String originalValue = influencedByCell.getOriginalValue();
            EffectiveValue effectiveValue = handleEffectiveValue(sheet, influencedByCellPosition, originalValue, updatedByName);
            sheet.updateCell(influencedByCellPosition, originalValue, effectiveValue);
            visited.add(influencedByCellPosition);
            updateInfluencedByCell(sheet, influencedByCellPosition, visited, updatedByName);
        }
    }

    @Override //THE FIRST UPDATE
    public CellDto updateSheetCell(String sheetName, int row, int column,
                                   String newOriginalValue, String updatedByName) {
        Sheet clonedSheet = sheetFilesManager.getSheetManager(sheetName)
                .getSheetByVersion(getCurrentSheetVersion(sheetName)).clone();
        Set<CellPositionInSheet> visitedCellPositions = new HashSet<>();
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        int cellsUpdatedCounter = 1;

        setCellInfo(clonedSheet, cellPosition, newOriginalValue, updatedByName);
        updateInfluencedByCell(clonedSheet, cellPosition, visitedCellPositions, updatedByName);
        cellsUpdatedCounter += visitedCellPositions.size();
        clonedSheet.setUpdatedCellsCount(cellsUpdatedCounter);
        sheetFilesManager.getSheetManager(sheetName).addNewSheet(clonedSheet);

        return findCellInSheet(sheetName, row, column, getCurrentSheetVersion(sheetName));
    }

    private void setCellInfo(Sheet sheet, CellPositionInSheet cellPosition, String originalValue, String updatedByName) {
        try {
            Cell cellInUpdate = sheet.getCell(cellPosition);
            EffectiveValue effectiveValue;

            if (cellInUpdate == null) { // need to create new cell
                sheet.createNewCell(cellPosition, originalValue, updatedByName);
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
                cellInUpdate.setUpdatedByName(updatedByName);
            }
            effectiveValue = handleEffectiveValue(sheet, cellPosition, originalValue, updatedByName);
            sheet.updateCell(cellPosition, originalValue, effectiveValue);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error occurred on update position " + cellPosition + ": " + e.getMessage());
        }
    }

    private int createCellsFromFile(Sheet sheet, STLCells jaxbCells, SheetManager sheetManager, String updatedByName) {
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
            setCellInfo(sheet, cellPositionInSheet, originalValue, updatedByName);
            cellsUpdatedCounter++;
        }

        return cellsUpdatedCounter;
    }

    @Override
    public FileMetadata loadFile(InputStream fileInputStream, String owner) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        STLSheet jaxbSheet = (STLSheet) jaxbUnmarshaller.unmarshal(fileInputStream);

        return addNewSheetManagerFromJaxbSheet(jaxbSheet, owner);
    }

    @Override
    public List<FileMetadata> getSheetFilesMetadata() {
        return sheetFilesManager.getFileMetadataList();
    }

    private FileMetadata addNewSheetManagerFromJaxbSheet(STLSheet jaxbSheet, String owner) {
        List<STLRange> ranges = jaxbSheet.getSTLRanges().getSTLRange();

        // Creating sheet manager
        SheetManager sheetManager = getNewSheetManager(jaxbSheet, owner);
        // Creating ranges
        for (STLRange range: ranges) {
            CellPositionInSheet fromPosition = PositionFactory.createPosition(range.getSTLBoundaries().getFrom());
            CellPositionInSheet toPosition = PositionFactory.createPosition(range.getSTLBoundaries().getTo());
            sheetManager.createRange(range.getName(), fromPosition, toPosition);
        }
        // Creating a sheet entity
        Sheet sheet = new SheetImpl(sheetManager);
        int cellsUpdatedCounter = createCellsFromFile(sheet, jaxbSheet.getSTLCells(), sheetManager, owner);
        sheet.setUpdatedCellsCount(cellsUpdatedCounter);
        sheetManager.addNewSheet(sheet);
        sheetFilesManager.addSheetManager(jaxbSheet.getName(), sheetManager);
        SheetDimension sheetDimension = sheetManager.getSheetDimension();
        String sheetSize = sheetDimension.getNumOfRows() + "X" + sheetDimension.getNumOfColumns();

        FileMetadata fileMetadata = new FileMetadata(jaxbSheet.getName(), owner, sheetSize,
                UserPermission.NONE.toString());
        sheetFilesManager.addFileMetadata(fileMetadata);

        return fileMetadata;
    }

    private SheetManager getNewSheetManager(STLSheet jaxbSheet, String ownerName) {
        int numOfRows = jaxbSheet.getSTLLayout().getRows();
        int numOfColumns = jaxbSheet.getSTLLayout().getColumns();
        int rowHeight = jaxbSheet.getSTLLayout().getSTLSize().getRowsHeightUnits();
        int columnWidth = jaxbSheet.getSTLLayout().getSTLSize().getColumnWidthUnits();

        SheetDimension sheetDimension = new SheetDimension(numOfRows, numOfColumns, rowHeight, columnWidth);

        return new SheetManager(sheetDimension, ownerName);
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
    public SheetDimensionDto getSheetDimension(String sheetName) {
        SheetDimension sheetDimension = sheetFilesManager.getSheetManager(sheetName).getSheetDimension();
        return new SheetDimensionDto(sheetDimension.getNumOfRows(), sheetDimension.getNumOfColumns(),
                sheetDimension.getRowHeight(), sheetDimension.getColumnWidth());
    }

    private Set<CellPositionDto> getIncludedPositionsInRange(Range range) {
        Set<CellPositionDto> includedPositionsDto = new LinkedHashSet<>();

        range.getIncludedPositions().forEach((includedPosition) ->
                includedPositionsDto.add(new CellPositionDto(includedPosition.getRow(), includedPosition.getColumn())));

        return includedPositionsDto;
    }

    @Override
    public RangeDto getRangeByName(String sheetName, String rangeName) {
        Range range = sheetFilesManager.getSheetManager(sheetName).getRangesManager().getRangeByName(rangeName);
        Set<CellPositionDto> includedPositionsDto = getIncludedPositionsInRange(range);

        return new RangeDto(new CellPositionDto(range.getFromPosition().getRow(), range.getFromPosition().getColumn()),
                new CellPositionDto(range.getToPosition().getRow(), range.getToPosition().getColumn()),
                includedPositionsDto);
    }

    @Override
    public List<String> getRangeNames(String sheetName) {
        List<String> rangeNames = new ArrayList<>();
        sheetFilesManager.getSheetManager(sheetName).getRangesManager().getName2Range()
                .forEach((name, range) -> rangeNames.add(name));

        return rangeNames;
    }

    @Override
    public RangeDto createRange(String sheetName, String rangeName, CellPositionInSheet fromPosition, CellPositionInSheet toPosition) {
        Range range = sheetFilesManager.getSheetManager(sheetName).createRange(rangeName, fromPosition, toPosition);
        Set<CellPositionDto> includedPositionsDto = getIncludedPositionsInRange(range);

        return new RangeDto(new CellPositionDto(range.getFromPosition().getRow(), range.getFromPosition().getColumn()),
                new CellPositionDto(range.getToPosition().getRow(), range.getToPosition().getColumn()), includedPositionsDto);
    }

    @Override
    public RangeDto getUnNamedRange(String sheetName, CellPositionInSheet fromPosition, CellPositionInSheet toPosition) {
        Range range = sheetFilesManager.getSheetManager(sheetName).getRangesManager().getUnNamedRange(fromPosition, toPosition);
        Set<CellPositionDto> includedPositionsDto = getIncludedPositionsInRange(range);

        return new RangeDto(new CellPositionDto(range.getFromPosition().getRow(), range.getFromPosition().getColumn()),
                new CellPositionDto(range.getToPosition().getRow(), range.getToPosition().getColumn()), includedPositionsDto);
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
    public LinkedList<RowDto> getSortedRowsSheet(String sheetName, int sheetVersion, Range rangeToSort, Set<String> columnsSortedBy) {
        validateColumnsInRange(rangeToSort, columnsSortedBy);
        Sheet inWorkSheet = sheetFilesManager.getSheetManager(sheetName)
                .getSheetByVersion(sheetVersion).clone();

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

        return getRowsDto(sortedNumericRows);
    }

    private LinkedList<RowDto> getRowsDto(List<Row> rows) {
        LinkedList<RowDto> rowsDto = new LinkedList<>();

        for (Row row : rows) {
            RowDto rowDto = new RowDto(row.getRowNumber(), new HashMap<>());
            for (Map.Entry<String, Cell> column2cell : row.getCells().entrySet()) {
                CellDto cellDto;
                if (column2cell.getValue() != null) {
                    cellDto = getCellDto(column2cell.getValue(), column2cell.getValue().getUpdatedByName());
                } else {
                    cellDto = getCellDto(null, "");
                }
                rowDto.getCells().put(column2cell.getKey(), cellDto);
            }
            rowsDto.add(rowDto);
        }

        return rowsDto;
    }

    private CellDto getCellDto(Cell cell, String updateByName) {
        CellDto cellDto;
        if (cell == null || cell.getEffectiveValue() == null) {
            EffectiveValueDto effectiveValueDto = new EffectiveValueDto(CellTypeDto.UNKNOWN, "");
            cellDto = new CellDto("", effectiveValueDto, effectiveValueDto, new LinkedHashSet<>(),
                    new LinkedHashSet<>(), 0, updateByName);
        } else {
            EffectiveValueDto effectiveValue = new EffectiveValueDto(CellTypeDto.valueOf(
                    cell.getEffectiveValue().getCellType().name()), cell.getEffectiveValue().getValue()
            );
            Set<CellPositionDto> influencedByDto = new HashSet<>();
            Set<CellPositionDto> influencesDto = new HashSet<>();

            cell.getInfluencedBy().forEach((influencedBy) ->
                    influencedByDto.add(new CellPositionDto(influencedBy.getRow(), influencedBy.getColumn())));
            cell.getInfluences().forEach((influence) ->
                    influencesDto.add(new CellPositionDto(influence.getRow(), influence.getColumn())));

            cellDto = new CellDto(cell.getOriginalValue(), effectiveValue, getEffectiveValueDtoForDisplay(effectiveValue),
                    influencedByDto, influencesDto, cell.getLastUpdatedInVersion(), cell.getUpdatedByName());
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
    public Map<String, Set<EffectiveValueDto>> getUniqueColumnValuesByRange(String sheetName, int sheetVersion,
                                                                            Range range, Set<String> columns) {
        validateColumnsInRange(range, columns);
        Map<String, Set<EffectiveValue>> column2uniqueEffectiveValues = new HashMap<>();
        columns.forEach((column) -> column2uniqueEffectiveValues.put(column, new LinkedHashSet<>()));

        range.getIncludedPositions().forEach((cellPosition) -> {
            Set<EffectiveValue> uniqueColumnValues = column2uniqueEffectiveValues.get(CellPositionInSheet.parseColumn(cellPosition.getColumn()));
            if (uniqueColumnValues != null) {
                EffectiveValue originalEffectiveValue = sheetFilesManager.getSheetManager(sheetName)
                        .getSheetByVersion(sheetVersion).getCellEffectiveValue(cellPosition);
                EffectiveValue effectiveValueForDisplay = getEffectiveValueForDisplay(originalEffectiveValue);
                uniqueColumnValues.add(effectiveValueForDisplay);
            }
        });

        Map<String, Set<EffectiveValueDto>> column2uniqueEffectiveValuesDto = new HashMap<>();
        column2uniqueEffectiveValues.forEach((column, uniqueEffectiveValue) -> {
            column2uniqueEffectiveValuesDto.put(column, new LinkedHashSet<>());
            uniqueEffectiveValue.forEach((effectiveValue) -> {
                EffectiveValueDto effectiveValueDto;
                if (effectiveValue != null) {
                    effectiveValueDto = new EffectiveValueDto(CellTypeDto.valueOf(effectiveValue.getCellType().name()), effectiveValue.getValue());
                } else {
                    effectiveValueDto = new EffectiveValueDto(CellTypeDto.UNKNOWN, "");
                }
                column2uniqueEffectiveValuesDto.get(column).add(effectiveValueDto);
            });
        });

        return column2uniqueEffectiveValuesDto;
    }

    @Override
    public LinkedList<RowDto> getFilteredRowsSheet(String sheetName, int sheetVersion, Range rangeToFilter,
                                                   Map<String, Set<String>> column2effectiveValuesFilteredBy) {

        List<Row> rowsToFilter = extractRowsInRange(sheetFilesManager.getSheetManager(sheetName)
                .getSheetByVersion(sheetVersion), rangeToFilter);

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
                        EffectiveValueDto originalEffectiveValueDto = new EffectiveValueDto(
                                CellTypeDto.valueOf(originalEffectiveValue.getCellType().name()),
                                originalEffectiveValue.getValue()
                        );
                        EffectiveValueDto effectiveValueForDisplay = getEffectiveValueDtoForDisplay(
                                originalEffectiveValueDto
                        );
                        if (!entry.getValue().contains(effectiveValueForDisplay.getValue().toString())) {
                            return false;
                        }
                    }
                }
                return true;
            }).collect(Collectors.toCollection(LinkedList::new));

        return getRowsDto(filteredRows);
    }



    @Override
    public SheetDto getSheetAfterDynamicAnalysisOfCell(String sheetName, int sheetVersion,
                                                       CellPositionInSheet cellPosition, double cellOriginalValue) {
        SheetDto dynamicAnalysedSheetDto;
        Sheet inWorkSheet = sheetFilesManager.getSheetManager(sheetName)
                .getSheetByVersion(sheetVersion).clone();
        Set<CellPositionInSheet> visitedCellPositions = new HashSet<>();

        String originalValueStr = String.valueOf(cellOriginalValue);
        setCellInfo(inWorkSheet, cellPosition, originalValueStr, "");
        updateInfluencedByCell(inWorkSheet, cellPosition, visitedCellPositions, "");

        dynamicAnalysedSheetDto = createSheetDto(inWorkSheet, sheetFilesManager.getSheetManager(sheetName).getOwnerName());
        return dynamicAnalysedSheetDto;
    }
}