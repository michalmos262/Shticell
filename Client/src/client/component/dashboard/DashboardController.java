package client.component.dashboard;

import client.component.mainapp.MainAppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

public class DashboardController {
    @FXML private Button viewSheetButton;
    @FXML private Button requestPermissionButton;
    @FXML private Button accDenyPermReqButton;
    @FXML private TableView<?> availableSheetsTableView;
    @FXML private TableView<?> permissionsTableView;
    @FXML private GridPane mainPanel;
    @FXML private GridPane loadFileComponent;
    @FXML private Label usernameLabel;

    DashboardModelUI modelUi;
    private MainAppController mainAppController;

    @FXML
    public void initialize() {
        if (loadFileComponent != null) {

        }

        modelUi = new DashboardModelUI(usernameLabel);
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
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