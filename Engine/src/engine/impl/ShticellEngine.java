package engine.impl;

import engine.api.Engine;
import engine.entity.cell.*;
import engine.entity.sheet.Sheet;
import engine.entity.sheet.SheetDimension;
import engine.entity.sheet.SheetManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

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
    public List<CellPositionInSheet> getDependsOnList(int row, int column, int sheetVersion) {
        Sheet sheet = sheetManager.getSheetByVersion(sheetManager.getCurrentVersion());
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        return new CellDto(sheet.getCell(cellPosition)).getInfluencedBy();
    }

    @Override
    public List<CellPositionInSheet> getInfluencingOnList(int row, int column, int sheetVersion) {
        Sheet sheet = sheetManager.getSheetByVersion(sheetManager.getCurrentVersion());
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        return new CellDto(sheet.getCell(cellPosition)).getInfluences();
    }

    private EffectiveValue handleEffectiveValue(Sheet sheet, CellPositionInSheet cellPosition, String originalValue) {
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
            for (CellPositionInSheet influencingPosition : influencingCellPositions) {
                sheet.addCellConnection(influencingPosition, cellPosition);
            }
        }
        else {
            effectiveValue = new EffectiveValue(CellType.STRING, originalValue);
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
    public void updateSheetCell(int row, int column, String newOriginalValue) {
        int cellsUpdatedCounter = 1;
        Sheet clonedSheet = sheetManager.getSheetByVersion(sheetManager.getCurrentVersion()).clone();
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        Cell cellInUpdate = clonedSheet.getCell(cellPosition);
        EffectiveValue effectiveValue;

        if (cellInUpdate == null) { // need to create new cell
            clonedSheet.createNewCell(cellPosition, newOriginalValue);
        } else {
            List<CellPositionInSheet> influencedByCellPositions = new LinkedList<>(cellInUpdate.getInfluencedBy());
            for (CellPositionInSheet influencingCellPosition: influencedByCellPositions) {
                clonedSheet.removeCellConnection(influencingCellPosition, cellPosition);
            }
        }
        effectiveValue = handleEffectiveValue(clonedSheet, cellPosition, newOriginalValue);
        clonedSheet.updateCell(cellPosition, newOriginalValue, effectiveValue);
        Set<CellPositionInSheet> visitedCellPositions = new HashSet<>();
        updateInfluencedByCell(clonedSheet, cellPosition, visitedCellPositions);
        cellsUpdatedCounter += visitedCellPositions.size();
        clonedSheet.setUpdatedCellsCount(cellsUpdatedCounter);
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