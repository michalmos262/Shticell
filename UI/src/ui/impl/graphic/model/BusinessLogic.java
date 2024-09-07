package ui.impl.graphic.model;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.dto.CellDto;
import engine.entity.dto.SheetDto;
import engine.entity.sheet.impl.SheetDimension;
import engine.impl.EngineImpl;
import ui.impl.graphic.components.app.MainAppController;

public class BusinessLogic {
    private final MainAppController mainAppController;
    private final Engine engine;

    public BusinessLogic(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
        this.engine = new EngineImpl();
    }

    public void loadFile(String fileName) throws Exception {
        engine.loadFile(fileName);
    }

    public SheetDimension getSheetDimension() {
        return engine.getSheetDimension();
    }

    public int getCurrentSheetVersion() {
        return engine.getCurrentSheetVersion();
    }

    public SheetDto getSheet(int sheetVersion) {
        return engine.getSheet(sheetVersion);
    }

    public CellDto getCell(int row, int column, int version) {
        return engine.findCellInSheet(row, column, version);
    }

    public int getLastCellVersion(CellPositionInSheet cellPositionInSheet) {
        return engine.getLastCellVersion(cellPositionInSheet.getRow(), cellPositionInSheet.getColumn());
    }

    public CellDto updateCell(CellPositionInSheet cellPositionInSheet, String newCellValue) {
        int row = cellPositionInSheet.getRow();
        int column = cellPositionInSheet.getColumn();

        engine.updateSheetCell(row, column, newCellValue);

        return getCell(row, column, getCurrentSheetVersion());
    }
}
