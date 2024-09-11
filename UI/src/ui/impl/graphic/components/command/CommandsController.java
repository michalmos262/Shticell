package ui.impl.graphic.components.command;

import engine.api.Engine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ui.impl.graphic.components.app.MainAppController;

public class CommandsController {

    @FXML private Button filterSheetByColumnButton;
    @FXML private Button setSheetPropertiesButton;
    @FXML private Button sortSheetByColumnButton;

    private MainAppController mainAppController;
    private Engine engine;

    public void setMainController(MainAppController mainAppController, Engine engine) {
        this.mainAppController = mainAppController;
        this.engine = engine;
    }

    public void fileLoaded() {
        filterSheetByColumnButton.disableProperty().set(false);
        setSheetPropertiesButton.disableProperty().set(false);
        sortSheetByColumnButton.disableProperty().set(false);
    }

    @FXML
    void filterSheetByColumnButtonListener(ActionEvent event) {

    }

    @FXML
    void setSheetPropertiesButtonListener(ActionEvent event) {

    }

    @FXML
    void sortSheetByColumnButtonListener(ActionEvent event) {

    }
}