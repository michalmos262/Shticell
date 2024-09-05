package ui.impl.graphic.components.app;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ui.impl.graphic.components.file.LoadFileController;
import ui.impl.graphic.model.BusinessLogic;

public class MainAppController {
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

    public void loadFile() {
        isFileSelected.set(true);
    }
}
