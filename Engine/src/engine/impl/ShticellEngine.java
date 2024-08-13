package engine.impl;

import engine.api.Engine;
import engine.entity.cell.CellDto;
import engine.entity.cell.CellPositionInSheet;
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
    public CellDto findCellInSheet(CellPositionInSheet cellPosition, int sheetVersion) {
        return new CellDto(getSheetDto(sheetVersion).getCellTable()[cellPosition.getRow()][cellPosition.getColumn()]);
    }

    @Override
    public int getLastCellVersion(CellPositionInSheet cellPosition) {
        return sheet.getCellByVersion(cellPosition, getCurrentSheetVersion()).getKey();
    }

    @Override
    public List<CellPositionInSheet> getAffectedByCellsList(CellPositionInSheet cellPosition, int sheetVersion) {
        return getSheetDto(sheetVersion).getCellPos2affectedByCellsPos().get(cellPosition);
    }

    @Override
    public List<CellPositionInSheet> getAffectedCellsList(CellPositionInSheet cellPosition, int sheetVersion) {
        return getSheetDto(sheetVersion).getCellPos2affectingCellsPos().get(cellPosition);
    }

    @Override
    public void updateSheetCell(CellPositionInSheet cellPosition, String newValue) {
        sheet.updateCell(cellPosition, newValue);
    }

    @Override
    public Map<Integer, Integer> getSheetVersions() {
        return getSheetDto(getCurrentSheetVersion()).getVersion2updatedCellsCount();
    }
}