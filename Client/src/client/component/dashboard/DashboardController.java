package client.component.dashboard;

import client.component.dashboard.loadfile.LoadFileController;
import client.component.dashboard.requestpermission.RequestPermissionController;
import client.component.mainapp.MainAppController;
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
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static client.resources.CommonResourcesPaths.*;

public class DashboardController implements Closeable {
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
    private String clickedSheetName;
    private TimerTask sheetsTableRefresher;
    private Timer timer;

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

    @FXML
    void availableSheetOnMouseClickedListener(MouseEvent event) {
        DashboardModelUI.SheetsTableEntry selectedRow = availableSheetsTableView.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            clickedSheetName = selectedRow.sheetNameProperty().getValue();
        }
    }

    @FXML
    public void viewSheetButtonListener(ActionEvent actionEvent) {
        if (clickedSheetName != null) {
            mainAppController.switchToSheet(clickedSheetName);
        }
    }

    @FXML
    public void AccDenyPermissionRequestButtonListener(ActionEvent actionEvent) {

    }

    @FXML
    public void RequestPermissionButtonListener(ActionEvent actionEvent) throws IOException {
        if (clickedSheetName != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(REQUEST_SHEET_PERMISSION_RESOURCE_LOCATION));
            Parent root = fxmlLoader.load();

            RequestPermissionController requestPermissionController = fxmlLoader.getController();
            requestPermissionController.setDashboardController(this);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Request permission for sheet: " + getClickedSheetName());
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Block other windows until this is closed
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        }
    }

    public String getClickedSheetName() {
        return clickedSheetName;
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

    public void startSheetsTableRefresher() {
        sheetsTableRefresher = new DashboardRefresher(
                this::updateSheetsTable);
        timer = new Timer();
        timer.schedule(sheetsTableRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void setActive() {
        startSheetsTableRefresher();
    }

    @Override
    public void close() {
        if (sheetsTableRefresher != null && timer != null) {
            sheetsTableRefresher.cancel();
            timer.cancel();
        }
    }
}