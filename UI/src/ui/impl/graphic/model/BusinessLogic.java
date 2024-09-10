package ui.impl.graphic.model;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.CellDto;
import engine.entity.dto.SheetDto;
import engine.entity.sheet.SheetDimension;
import engine.impl.EngineImpl;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import ui.impl.graphic.components.app.MainAppController;

import java.util.HashMap;
import java.util.Map;

public class BusinessLogic {
    private final MainAppController mainAppController;

    public BusinessLogic(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }
}
