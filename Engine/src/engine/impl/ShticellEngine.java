package engine.impl;

import engine.api.Engine;
import engine.entity.cell.*;
import engine.entity.sheet.Sheet;
import engine.entity.sheet.SheetDimension;
import engine.entity.sheet.SheetManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static engine.expression.impl.ExpressionEvaluator.evaluateExpression;

public class ShticellEngine implements Engine {
    private final SheetManager sheetManager;

    public ShticellEngine(String sheetName, int numOfRows, int numOfColumns, int rowHeight, int columnWidth) {
        SheetDimension sheetDimension = new SheetDimension(numOfRows, numOfColumns, rowHeight, columnWidth);
        sheetManager = new SheetManager(sheetName, sheetDimension);
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
    public SheetDimension getSheetDimension() {
        return sheetManager.getDimension();
    }

    @Override
    public CellDto findCellInSheet(int row, int column, int sheetVersion) {
        Sheet sheet = sheetManager.getSheetByVersion(sheetVersion);
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        return new CellDto(sheet.getCell(cellPosition));
    }

    @Override
    public int getLastCellVersion(int row, int column) {
        Sheet sheet = sheetManager.getSheetByVersion(sheetManager.getCurrentVersion());
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        return sheet.getCell(cellPosition).getLastUpdatedInVersion();
    }

    @Override
    public List<Cell> getDependsOnList(int row, int column, int sheetVersion) {
        Sheet sheet = sheetManager.getSheetByVersion(sheetManager.getCurrentVersion());
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        return new CellDto(sheet.getCell(cellPosition)).getInfluencedBy();
    }

    @Override
    public List<Cell> getInfluencingOnList(int row, int column, int sheetVersion) {
        Sheet sheet = sheetManager.getSheetByVersion(sheetManager.getCurrentVersion());
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        return new CellDto(sheet.getCell(cellPosition)).getInfluences();
    }

    private EffectiveValue getEffectiveValue(Sheet sheet, CellPositionInSheet cellPosition, String originalValue) {
        EffectiveValue effectiveValue;

        if (originalValue.matches("-?\\d+(\\.\\d+)?")) {
            DecimalFormat formatter = new DecimalFormat("#,###.##");
            effectiveValue = new EffectiveValue(CellType.NUMERIC, formatter.format(new BigDecimal(originalValue)));
        }
        else if (originalValue.equalsIgnoreCase("true") || originalValue.equalsIgnoreCase("false")) {
            effectiveValue = new EffectiveValue(CellType.BOOLEAN, originalValue.toUpperCase());
        }
        else if (originalValue.charAt(0) == '{' && originalValue.charAt(originalValue.length() - 1) == '}') {
            List<CellPositionInSheet> influencingCellPositions = new LinkedList<>();
            effectiveValue = evaluateExpression(sheet, originalValue, influencingCellPositions).getEffectiveValue();
            // TODO: add influencers!!
        }
        else {
            effectiveValue = new EffectiveValue(CellType.STRING, originalValue);
        }

        return effectiveValue;
    }

    //RECURSIVE UPDATE
    private void updateInfluencedByCell(Sheet sheet, CellPositionInSheet cellPosition) {
        Cell cell = sheet.getCell(cellPosition);
        String originalValue = cell.getOriginalValue();
        EffectiveValue effectiveValue = getEffectiveValue(sheet, cellPosition, originalValue);

        cell.getInfluences()
                .forEach((influencedCell) -> {
                    sheet.updateCell(influencedCell, originalValue, effectiveValue);
                    updateInfluencedByCell(sheet, cellPosition);
                }
        );
    }

    @Override
    //THE FIRST UPDATE
    public void updateSheetCell(int row, int column, String newOriginalValue) {
        Sheet clonedSheet = sheetManager.getSheetByVersion(sheetManager.getCurrentVersion()).clone();
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        Cell cellInUpdate = clonedSheet.getCell(cellPosition);
        EffectiveValue effectiveValue = getEffectiveValue(clonedSheet, cellPosition, newOriginalValue);

        if (cellInUpdate == null) { // need to create new cell
            clonedSheet.createNewCell(cellPosition, newOriginalValue, effectiveValue);
        } else {
            clonedSheet.updateCell(cellInUpdate, newOriginalValue, effectiveValue);
            updateInfluencedByCell(clonedSheet, cellPosition);
            cellInUpdate.getInfluencedBy().removeAll(cellInUpdate.getInfluencedBy());
        }
        sheetManager.addNewSheet(clonedSheet);
    }

    @Override
    public Map<Integer, Integer> getSheetVersions() {
        Map<Integer, Integer> version2cellsUpdatedCount = new HashMap<>();
        sheetManager.getVersion2sheet().forEach((version, sheet) ->
                version2cellsUpdatedCount.put(version, sheet.getUpdatedCellsCount()));
        return version2cellsUpdatedCount;
    }

    @Override
    public CellPositionInSheet getCellPositionInSheet(int row, int column) {
        return PositionFactory.createPosition(row, column);
    }

    @Override
    public CellPositionInSheet getCellPositionInSheet(String position) {
        return PositionFactory.createPosition(position);
    }

    @Override
    public int parseRowFromPosition(String position) {
        return getCellPositionInSheet(position).getRow();
    }

    @Override
    public int parseColumnFromPosition(String position) {
        return getCellPositionInSheet(position).getColumn();
    }
}