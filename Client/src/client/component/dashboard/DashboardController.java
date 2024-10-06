package client.component.dashboard;

import client.component.dashboard.loadfile.LoadFileController;
import client.component.mainapp.MainAppController;
import dto.user.UserPermissionDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import dto.sheet.FileMetadata;

public class DashboardController {
    @FXML private Button viewSheetButton;
    @FXML private Button requestPermissionButton;
    @FXML private Button accDenyPermReqButton;

    @FXML private TableView<DashboardModelUI.SheetsTableEntry> availableSheetsTableView;
    @FXML private TableColumn<DashboardModelUI.SheetsTableEntry, String> sheetNameColumn;
    @FXML private TableColumn<DashboardModelUI.SheetsTableEntry, String> ownerUsernameColumn;
    @FXML private TableColumn<DashboardModelUI.SheetsTableEntry, String> sheetSizeColumn;
    @FXML private TableColumn<DashboardModelUI.SheetsTableEntry, String> yourPermissionTypeColumn;

    @FXML private TableView<?> permissionsTableView;

    @FXML private GridPane loadFileComponent;
    @FXML private LoadFileController loadFileComponentController;

    private DashboardModelUI modelUi;
    private MainAppController mainAppController;

    @FXML
    public void initialize() {
        if (loadFileComponent != null) {
            loadFileComponentController.setDashboardController(this);
        }
        modelUi = new DashboardModelUI(availableSheetsTableView,
                sheetNameColumn, ownerUsernameColumn, sheetSizeColumn, yourPermissionTypeColumn);
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    public void fileLoadedSuccessfully(FileMetadata fileMetadata) {
        modelUi.addSheet(fileMetadata.getSheetName(), fileMetadata.getOwner(), fileMetadata.getSheetSize(), UserPermissionDto.OWNER.toString());
        mainAppController.loadSheetPage(fileMetadata.getSheetName());
    }

    @FXML
    void availableSheetOnMouseClickedListener(MouseEvent event) {

    }

    @FXML
    public void ViewSheetButtonListener(ActionEvent actionEvent) {
        DashboardModelUI.SheetsTableEntry selectedRow = availableSheetsTableView.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            mainAppController.switchToSheet(selectedRow.sheetNameProperty().getValue());
        }
    }

    @FXML
    public void AccDenyPermReqButtonListener(ActionEvent actionEvent) {

    }

    @FXML
    public void RequestPermissionButtonListener(ActionEvent actionEvent) {

    }
}