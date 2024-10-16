package client.component.dashboard;

import client.component.dashboard.loadfile.LoadFileController;
import client.component.dashboard.requestpermission.RequestPermissionController;
import client.component.mainapp.MainAppController;
import dto.user.PermissionRequestDto;
import dto.user.SheetNamesAndFileMetadatasDto;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import dto.sheet.FileMetadata;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static client.resources.CommonResourcesPaths.*;

public class DashboardController implements Closeable {
    @FXML private Button viewSheetButton;
    @FXML private Button requestPermissionButton;
    @FXML private Button acceptPermissionRequestButton;
    @FXML private Button rejectPermissionRequestButton;

    @FXML private TableView<DashboardModelUI.SheetsTableEntry> availableSheetsTableView;
    @FXML private TableColumn<DashboardModelUI.SheetsTableEntry, String> sheetNameColumn;
    @FXML private TableColumn<DashboardModelUI.SheetsTableEntry, String> ownerUsernameColumn;
    @FXML private TableColumn<DashboardModelUI.SheetsTableEntry, String> sheetSizeColumn;
    @FXML private TableColumn<DashboardModelUI.SheetsTableEntry, String> yourPermissionTypeColumn;

    @FXML private TableView<DashboardModelUI.PermissionsTableEntry> permissionsTableView;
    @FXML private TableColumn<DashboardModelUI.PermissionsTableEntry, String> usernameColumn;
    @FXML private TableColumn<DashboardModelUI.PermissionsTableEntry, String> permissionTypeColumn;
    @FXML private TableColumn<DashboardModelUI.PermissionsTableEntry, String> approvalStateColumn;

    @FXML private GridPane loadFileComponent;
    @FXML private LoadFileController loadFileComponentController;

    private DashboardModelUI modelUi;
    private MainAppController mainAppController;
    private DashboardModelUI.SheetsTableEntry selectedSheetTableEntry;
    private TimerTask sheetsTableRefresher;
    private TimerTask sheetUserPermissionsRefresher;
    private Timer showAvailableSheetsTimer;
    private Timer showPermissionsTimer;

    @FXML
    public void initialize() {
        if (loadFileComponent != null) {
            loadFileComponentController.setDashboardController(this);
        }
        modelUi = new DashboardModelUI(availableSheetsTableView,
                sheetNameColumn, ownerUsernameColumn, sheetSizeColumn, yourPermissionTypeColumn,
                permissionsTableView, usernameColumn, permissionTypeColumn, approvalStateColumn);
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    @FXML
    void availableSheetOnMouseClickedListener(MouseEvent event) {
        selectedSheetTableEntry = availableSheetsTableView.getSelectionModel().getSelectedItem();
        modelUi.selectedSheetNameProperty().set(selectedSheetTableEntry.sheetNameProperty().getValue());
    }

    @FXML
    public void viewSheetButtonListener(ActionEvent actionEvent) {
        if (selectedSheetTableEntry != null) {
            mainAppController.switchToSheet(selectedSheetTableEntry.sheetNameProperty().getValue());
        }
    }

    @FXML
    public void acceptPermissionRequestButtonListener(ActionEvent actionEvent) {
        // disable from accepting a permission request if the sheet owner is not me
        if (selectedSheetTableEntry != null && selectedSheetTableEntry.ownerNameProperty().getValue()
                .equals(mainAppController.getLoggedInUsername())) {


        }
    }

    @FXML
    public void rejectPermissionRequestButtonListener(ActionEvent actionEvent) {
        // disable from rejecting a permission request if the sheet owner is not me
        if (selectedSheetTableEntry != null && selectedSheetTableEntry.ownerNameProperty().getValue()
                .equals(mainAppController.getLoggedInUsername())) {

        }
    }

    @FXML
    public void requestPermissionButtonListener(ActionEvent actionEvent) throws IOException {
        // disable from request permission for myself
        if (selectedSheetTableEntry != null && !selectedSheetTableEntry.ownerNameProperty().getValue()
                .equals(mainAppController.getLoggedInUsername())) {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(REQUEST_SHEET_PERMISSION_RESOURCE_LOCATION));
            Parent root = fxmlLoader.load();

            RequestPermissionController requestPermissionController = fxmlLoader.getController();
            requestPermissionController.setDashboardController(this);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("[Request a permission] - " + getClickedSheetName());
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        }
    }

    public String getClickedSheetName() {
        return modelUi.selectedSheetNameProperty().getValue();
    }

    private void updateSheetsTable(SheetNamesAndFileMetadatasDto sheetNamesAndFileMetadatasDto) {
        Platform.runLater(() -> {
            ObservableList<DashboardModelUI.SheetsTableEntry> items = availableSheetsTableView.getItems();
            items.clear();
            for (Map.Entry<String, FileMetadata> username2fileMetadataEntry: sheetNamesAndFileMetadatasDto.getSheetName2fileMetadata().entrySet()) {
                FileMetadata fileMetadata = username2fileMetadataEntry.getValue();
                modelUi.addSheet(fileMetadata.getSheetName(), fileMetadata.getOwner(), fileMetadata.getSheetSize(), fileMetadata.getYourPermission());
            }
        });
    }

    private void clickOnLastClickedSheet() {
        Platform.runLater(() -> {
            int rowIndex = availableSheetsTableView.getItems().indexOf(selectedSheetTableEntry);
            if (rowIndex >= 0) {
                availableSheetsTableView.getSelectionModel().select(rowIndex);
            }
        });
    }

    private void updateSheetPermissionsTable(List<PermissionRequestDto> permissionRequests) {
        Platform.runLater(() -> {
            ObservableList<DashboardModelUI.PermissionsTableEntry> items = permissionsTableView.getItems();
            items.clear();
            for (PermissionRequestDto permissionRequest: permissionRequests) {
                modelUi.addSheetUserPermission(permissionRequest.getRequestUsername(),
                        permissionRequest.getPermission().name(),
                        permissionRequest.getCurrentApprovalStatus().name());
            }
        });
    }

    private void startSheetsTableRefresher() {
        sheetsTableRefresher = new AvailableSheetsTableRefresher(
                this::updateSheetsTable,
                this::clickOnLastClickedSheet
        );
        showAvailableSheetsTimer = new Timer();
        showAvailableSheetsTimer.schedule(sheetsTableRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    private void startPermissionsTableRefresher() {
        sheetUserPermissionsRefresher = new SheetUserPermissionsRefresher(
                modelUi.selectedSheetNameProperty(),
                this::updateSheetPermissionsTable
        );
        showPermissionsTimer = new Timer();
        showPermissionsTimer.schedule(sheetUserPermissionsRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void setActive() {
        startSheetsTableRefresher();
        startPermissionsTableRefresher();
    }

    @Override
    public void close() {
        if (sheetsTableRefresher != null && showAvailableSheetsTimer != null) {
            sheetsTableRefresher.cancel();
            showAvailableSheetsTimer.cancel();
        }
    }
}