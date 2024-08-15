package engine.impl;

import engine.api.Engine;
import engine.entity.cell.Cell;
import engine.entity.cell.CellDto;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.sheet.Sheet;
import engine.entity.sheet.SheetDimension;
import engine.entity.sheet.SheetManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return new CellDto(sheet.getCell(cellPosition)).getDependsOn();
    }

    @Override
    public List<Cell> getInfluencingOnList(int row, int column, int sheetVersion) {
        Sheet sheet = sheetManager.getSheetByVersion(sheetManager.getCurrentVersion());
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        return new CellDto(sheet.getCell(cellPosition)).getInfluencingOn();
    }

    @Override
    public void updateSheetCell(int row, int column, String newValue) {
        Sheet newSheet = sheetManager.getSheetByVersion(sheetManager.getCurrentVersion()).clone();
        newSheet.updateCell(PositionFactory.createPosition(row, column), newValue);

        sheetManager.addNewSheet(newSheet);
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