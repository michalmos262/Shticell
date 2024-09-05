package ui.impl.graphic.components.app;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ui.api.Ui;
import ui.impl.graphic.components.file.LoadFileController;
import ui.impl.graphic.model.BusinessLogic;

public class MainAppController implements Ui {
    @FXML private GridPane loadFileComponent;
    @FXML private LoadFileController loadFileComponentController;
    @FXML private GridPane actionLineComponent;
    @FXML private BorderPane commandsComponent;
    @FXML private BorderPane rangesComponent;
    @FXML private ScrollPane sheetComponent;

    private SimpleBooleanProperty isFileSelected = new SimpleBooleanProperty(false);
    private Stage primaryStage;
    private BusinessLogic businessLogic;

    @FXML
    public void initialize() {
        if (loadFileComponentController != null) {
            loadFileComponentController.setMainController(this);
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setBusinessLogic(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void loadFile() {
        isFileSelected.set(true);
    }

    @Override
    public void checkIfThereIsData() {

    }

    @Override
    public void showCurrentVersionSheet() {

    }

    @Override
    public void showCellFromSheet() {

    }

    @Override
    public void updateSheetCell() {

    }

    @Override
    public void showSheetVersionsForDisplay() {

    }

    @Override
    public void saveCurrentSheetVersionsToFile() {

    }

    @Override
    public void loadSystemFromFile() {

    }

    @Override
    public void exitProgram() {

    }
}