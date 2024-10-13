package client.component.dashboard;

import client.component.dashboard.loadfile.LoadFileController;
import client.component.mainapp.MainAppController;
import dto.user.SheetNameAndFileMetadataDto;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import dto.sheet.FileMetadata;

import java.io.Closeable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static client.resources.CommonResourcesPaths.REFRESH_RATE;

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

    }

    @FXML
    public void ViewSheetButtonListener(ActionEvent actionEvent) {
        DashboardModelUI.SheetsTableEntry selectedRow = availableSheetsTableView.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            String sheetName = selectedRow.sheetNameProperty().getValue();
            mainAppController.switchToSheet(sheetName);
        }
    }

    @FXML
    public void AccDenyPermissionRequestButtonListener(ActionEvent actionEvent) {

    }

    @FXML
    public void RequestPermissionButtonListener(ActionEvent actionEvent) {

    }

    private void updateSheetsTable(SheetNameAndFileMetadataDto sheetNameAndFileMetadataDto) {
        Platform.runLater(() -> {
            ObservableList<DashboardModelUI.SheetsTableEntry> items = availableSheetsTableView.getItems();
            items.clear();
            for (Map.Entry<String, FileMetadata> username2fileMetadataEntry: sheetNameAndFileMetadataDto.getSheetName2fileMetadata().entrySet()) {
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