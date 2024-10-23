package client.component.dashboard;

import client.component.alert.AlertsHandler;
import client.component.dashboard.requestpermission.RequestPermissionController;
import client.component.mainapp.MainAppController;
import client.util.http.HttpClientUtil;
import com.google.gson.reflect.TypeToken;
import dto.user.ApprovalStatus;
import dto.user.PermissionRequestDto;
import dto.user.SheetNamesAndFileMetadatasDto;
import dto.user.UserPermission;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import dto.sheet.FileMetadata;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import serversdk.request.body.UpdatePermissionRequestBody;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static client.resources.CommonResourcesPaths.*;
import static serversdk.request.parameter.RequestParameters.SHEET_NAME;

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

    private DashboardModelUI modelUi;
    private MainAppController mainAppController;
    private DashboardModelUI.SheetsTableEntry selectedSheetTableEntry;
    private DashboardModelUI.PermissionsTableEntry selectedPermissionsTableEntry;

    private boolean isComponentActive = false;
    private TimerTask sheetsTableRefresher;
    private TimerTask sheetUserPermissionsRefresher;
    private Timer showAvailableSheetsTimer;
    private Timer showPermissionsTimer;

    @FXML
    public void initialize() {
        List<Button> ownerOnlyButtons = new LinkedList<>();
        ownerOnlyButtons.add(acceptPermissionRequestButton);
        ownerOnlyButtons.add(rejectPermissionRequestButton);

        modelUi = new DashboardModelUI(viewSheetButton, requestPermissionButton, ownerOnlyButtons,
                availableSheetsTableView,
                sheetNameColumn, ownerUsernameColumn, sheetSizeColumn, yourPermissionTypeColumn,
                permissionsTableView, usernameColumn, permissionTypeColumn, approvalStateColumn);
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    @FXML
    void availableSheetOnMouseClickedListener() {
        selectedSheetTableEntry = availableSheetsTableView.getSelectionModel().getSelectedItem();
        changeSheetButtonsDisability();
    }

    private void changeSheetButtonsDisability() {
        if (selectedSheetTableEntry != null) {
            modelUi.isSheetClickedProperty().set(true);
            modelUi.isPermissionClicked().set(false);

            boolean isSheetOwner = selectedSheetTableEntry.ownerNameProperty().getValue()
                    .equals(mainAppController.getLoggedInUsername());

            boolean isAllowedToViewSheet = !selectedSheetTableEntry.yourPermissionTypeProperty().getValue()
                    .equals(UserPermission.NONE.name());

            modelUi.isOwnerProperty().set(isSheetOwner);
            modelUi.isAllowedToViewSheetProperty().set(isAllowedToViewSheet);
            modelUi.selectedSheetNameProperty().set(selectedSheetTableEntry.sheetNameProperty().getValue());
        }
    }

    @FXML
    void permissionsTableViewOnMouseClicked() {
        if (selectedSheetTableEntry != null) {
            selectedPermissionsTableEntry = permissionsTableView.getSelectionModel().getSelectedItem();
            if (selectedPermissionsTableEntry != null) {
                modelUi.isPermissionClicked().set(true);

                boolean isPermissionPending = selectedPermissionsTableEntry.approvalStateProperty().getValue()
                                .equals(ApprovalStatus.PENDING.name());

                modelUi.isPermissionPendingProperty().set(isPermissionPending);
            }
        }
    }

    @FXML
    public void viewSheetButtonListener() {
        if (selectedSheetTableEntry != null) {
            String yourPermission = selectedSheetTableEntry.yourPermissionTypeProperty().getValue();

            boolean isUserWriter = yourPermission.equals(UserPermission.WRITER.name()) ||
                    yourPermission.equals(UserPermission.OWNER.name());

            mainAppController.switchToSheet(selectedSheetTableEntry.sheetNameProperty().getValue(), isUserWriter);
        }
    }

    @FXML
    public void acceptPermissionRequestButtonListener() {
        changePermissionRequestStatus(ApprovalStatus.APPROVED);
    }

    @FXML
    public void rejectPermissionRequestButtonListener() {
        changePermissionRequestStatus(ApprovalStatus.REJECTED);
    }

    private void changePermissionRequestStatus(ApprovalStatus approvalStatus) {
        // disable from accepting a permission request if the sheet owner is not me
        if (selectedPermissionsTableEntry != null && selectedSheetTableEntry.ownerNameProperty().getValue()
                .equals(mainAppController.getLoggedInUsername())) {

            String url = HttpUrl
                .parse(PERMISSION_REQUEST_ENDPOINT)
                .newBuilder()
                .addQueryParameter(SHEET_NAME, selectedSheetTableEntry.sheetNameProperty().getValue())
                .build()
                .toString();

            HttpClientUtil.runAsyncGet(url, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("Error changing permission request status: " + e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Type listType = new TypeToken<List<PermissionRequestDto>>(){}.getType();
                        List<PermissionRequestDto> permissionRequests = GSON_INSTANCE
                                .fromJson(response.body().string(), listType);

                        PermissionRequestDto permissionRequest = permissionRequests.stream().filter((req) ->
                                req.getRequestUid().equals(selectedPermissionsTableEntry.requestUidProperty().getValue()))
                                .findFirst().orElse(null);

                        if (permissionRequest != null) {
                            String acceptRejectPermissionRequestBodyJson = GSON_INSTANCE.toJson(
                                    new UpdatePermissionRequestBody(permissionRequest.getRequestUid(),
                                            selectedSheetTableEntry.sheetNameProperty().getValue(),
                                            approvalStatus.name()
                                    )
                            );

                            MediaType mediaType = MediaType.get(JSON_MEDIA_TYPE);
                            RequestBody acceptPermissionRequestBody = RequestBody.create(acceptRejectPermissionRequestBodyJson, mediaType);

                            HttpClientUtil.runAsyncPut(PERMISSION_REQUEST_ENDPOINT, acceptPermissionRequestBody,
                                    new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    System.out.println("Error changing permission request status: " + e.getMessage());
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        Platform.runLater(() -> {
                                            AlertsHandler.HandleOkAlert("Permission request " +
                                                approvalStatus.name().toLowerCase() + " successfully!");
                                            modelUi.isPermissionClicked().set(false);
                                        });
                                    } else {
                                        System.out.println("Error changing permission request status: " + response.body().string());
                                    }
                                }
                            });
                        }
                    } else {
                        System.out.println("Error changing permission request status: " + response.body().string());
                    }
                }
            });
        }
    }

    @FXML
    public void requestPermissionButtonListener() throws IOException {
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
            clickOnLastClickedSheet();
        });
    }

    private void clickOnLastClickedSheet() {
        if (selectedSheetTableEntry != null) {
            int rowIndex = availableSheetsTableView.getItems().indexOf(availableSheetsTableView.getItems()
                    .stream().filter((entry) ->
                            entry.sheetNameProperty().getValue()
                                    .equals(selectedSheetTableEntry.sheetNameProperty().getValue())
                    ).findFirst().orElse(null)
            );

            if (rowIndex >= 0) {
                availableSheetsTableView.getSelectionModel().select(rowIndex);
                selectedSheetTableEntry = availableSheetsTableView.getItems().get(rowIndex);
            }
            changeSheetButtonsDisability();
        }
    }

    private void updateSheetPermissionsTable(List<PermissionRequestDto> permissionRequests) {
        Platform.runLater(() -> {
            ObservableList<DashboardModelUI.PermissionsTableEntry> items = permissionsTableView.getItems();
            items.clear();
            for (PermissionRequestDto permissionRequest: permissionRequests) {
                modelUi.addSheetUserPermission(permissionRequest.getRequestUsername(),
                        permissionRequest.getPermission().name(),
                        permissionRequest.getCurrentApprovalStatus().name(),
                        permissionRequest.getRequestUid());
            }
        });
    }

    private void startSheetsTableRefresher() {
        if (isComponentActive) return;
        sheetsTableRefresher = new AvailableSheetsTableRefresher(
                this::updateSheetsTable
        );
        showAvailableSheetsTimer = new Timer();
        showAvailableSheetsTimer.schedule(sheetsTableRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    private void startPermissionsTableRefresher() {
        if (isComponentActive) return;
        sheetUserPermissionsRefresher = new SheetUserPermissionsRefresher(
                modelUi.selectedSheetNameProperty(),
                this::updateSheetPermissionsTable
        );
        showPermissionsTimer = new Timer();
        showPermissionsTimer.schedule(sheetUserPermissionsRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void setActive() {
        if (!isComponentActive) {
            startSheetsTableRefresher();
            startPermissionsTableRefresher();
            isComponentActive = true;
        }
    }

    @Override
    public void close() {
        isComponentActive = false;
        if (sheetsTableRefresher != null) {
            sheetsTableRefresher.cancel();
        }
        if (sheetUserPermissionsRefresher != null) {
            sheetUserPermissionsRefresher.cancel();
        }
        if (showAvailableSheetsTimer != null) {
            showAvailableSheetsTimer.cancel();
            showAvailableSheetsTimer.purge();
        }
        if (showPermissionsTimer != null) {
            showPermissionsTimer.cancel();
            showPermissionsTimer.purge();
        }
    }
}