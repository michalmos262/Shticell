package client.component.dashboard;

import client.component.dashboard.loadfile.LoadFileController;
import client.component.mainapp.MainAppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class DashboardController {
    @FXML private Button viewSheetButton;
    @FXML private Button requestPermissionButton;
    @FXML private Button accDenyPermReqButton;
    @FXML private TableView<?> availableSheetsTableView;
    @FXML private TableView<?> permissionsTableView;
    @FXML private GridPane mainPanel;
    @FXML private GridPane loadFileComponent;
    @FXML private LoadFileController loadFileComponentController;

    private DashboardModelUI modelUi;
    private MainAppController mainAppController;
    private Stage primaryStage;

    @FXML
    public void initialize() {
        if (loadFileComponent != null) {
            loadFileComponentController.setDashboardController(this);
        }

        modelUi = new DashboardModelUI();
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void fileIsLoading() {
//        modelUi.fileIsLoading(true);
    }

    public void fileFailedLoading() {
//        modelUi.fileIsLoading(false);
    }

    public void fileLoadedSuccessfully() {
//        actionLineComponentController.fileLoadedSuccessfully();
//        rangesComponentController.fileLoadedSuccessfully();
//        commandsComponentController.fileLoadedSuccessfully();
//
//        SheetDto sheetDto = engine.getSheet(engine.getCurrentSheetVersion());
//        sheetComponentController.initMainGrid(sheetDto);
    }

    @FXML
    public void ViewSheetButtonListener(ActionEvent actionEvent) {

    }

    @FXML
    public void AccDenyPermReqButtonListener(ActionEvent actionEvent) {

    }

    @FXML
    public void RequestPermissionButtonListener(ActionEvent actionEvent) {

    }
}