package ui.impl.graphic.components.app;

import engine.entity.cell.CellPositionInSheet;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.HashMap;
import java.util.Map;

public class MainAppModelUI {
    private final SimpleBooleanProperty isDataLoaded;


    public MainAppModelUI() {
        isDataLoaded = new SimpleBooleanProperty(false);
    }
}
