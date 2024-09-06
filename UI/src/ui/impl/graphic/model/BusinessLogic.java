package ui.impl.graphic.model;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.dto.SheetDto;
import engine.entity.sheet.impl.SheetDimension;
import engine.impl.EngineImpl;
import javafx.beans.property.StringProperty;
import ui.impl.graphic.components.app.MainAppController;

import java.util.Map;

public class BusinessLogic {
    private MainAppController mainAppController;
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

    public int getLastCellVersion(CellPositionInSheet cellPositionInSheet) {
        return engine.getLastCellVersion(cellPositionInSheet.getRow(), cellPositionInSheet.getColumn());
    }
}
