package engine.impl;

import engine.api.Engine;
import engine.entity.cell.Cell;
import engine.entity.cell.CellDto;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.sheet.Sheet;
import engine.entity.sheet.SheetDto;

import java.util.List;
import java.util.Map;

public class ShticellEngine implements Engine {
    private final Sheet sheet;

    public ShticellEngine(String sheetName, int numOfRows, int numOfColumns, int rowHeight, int columnWidth) {
        Sheet.Dimension sheetDimension = new Sheet.Dimension(numOfRows, numOfColumns, rowHeight, columnWidth);
        sheet = new Sheet(sheetName, sheetDimension);
    }

    @Override
    public SheetDto getSheetDto(int version) {
        return new SheetDto(sheet, version);
    }

    @Override
    public String getSheetName() {
        return sheet.getName();
    }

    @Override
    public int getCurrentSheetVersion() {
        return sheet.getCurrVersion();
    }

    @Override
    public Sheet.Dimension getSheetDimension() {
        return sheet.getDimension();
    }

    @Override
    public CellDto findCellInSheet(int row, int column, int sheetVersion) {
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        return new CellDto(getSheetDto(sheetVersion).getCellTable()[cellPosition.getRow()][cellPosition.getColumn()]);
    }

    @Override
    public int getLastCellVersion(int row, int column) {
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        return sheet.getCellByVersion(cellPosition, getCurrentSheetVersion()).getKey();
    }

    @Override
    public List<Cell> getDependsOnList(int row, int column, int sheetVersion) {
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        Cell originalCell = sheet.getCellByVersion(cellPosition, sheetVersion).getValue();
        return new CellDto(originalCell).getDependsOn();
    }

    @Override
    public List<Cell> getInfluencingOnList(int row, int column, int sheetVersion) {
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        Cell originalCell = sheet.getCellByVersion(cellPosition, sheetVersion).getValue();
        return new CellDto(originalCell).getInfluencingOn();
    }

    @Override
    public void updateSheetCell(int row, int column, String newValue) {
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        sheet.updateCell(cellPosition, newValue);
    }

    @Override
    public Map<Integer, Integer> getSheetVersions() {
        return getSheetDto(getCurrentSheetVersion()).getVersion2updatedCellsCount();
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