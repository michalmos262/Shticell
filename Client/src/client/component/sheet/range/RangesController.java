package client.component.sheet.range;

import client.component.alert.AlertsHandler;
import client.util.http.HttpClientUtil;
import dto.sheet.RangeDto;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import client.component.sheet.mainsheet.MainSheetController;
import okhttp3.*;
import serversdk.exception.ServerException;
import serversdk.request.body.RangeBody;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import static client.resources.CommonResourcesPaths.*;
import static serversdk.request.parameter.RequestParameters.*;

public class RangesController implements Closeable {

    @FXML private TextField addFromRangeTextInput;
    @FXML private TitledPane addNewRangeTitledPane;
    @FXML private Button addRangeButton;
    @FXML private TextField addRangeNameTextInput;
    @FXML private TextField addToRangeTextInput;
    @FXML private Button deleteRangeButton;
    @FXML private ChoiceBox<String> deleteRangeNameChoiceBox;
    @FXML private TitledPane deleteRangeTitledPane;
    @FXML private TableView<RangeModelUI.RangeTableEntry> showRangesTable;
    @FXML private TableColumn<RangeModelUI.RangeTableEntry, String> nameColumn;
    @FXML private TableColumn<RangeModelUI.RangeTableEntry, String> rangeColumn;
    @FXML private TitledPane showRangesTitledPane;

    private MainSheetController mainSheetController;
    private RangeModelUI modelUi;
    private RangesRefresher rangesRefresher;
    private Timer timer;

    @FXML
    private void initialize() {
        List<TextField> textFields = Arrays.asList(addFromRangeTextInput, addRangeNameTextInput, addToRangeTextInput);
        List<TitledPane> writeOnlyTitledPanes = Arrays.asList(addNewRangeTitledPane, deleteRangeTitledPane);

        modelUi = new RangeModelUI(showRangesTable, nameColumn, rangeColumn, deleteRangeNameChoiceBox, textFields,
                writeOnlyTitledPanes);
    }

    public void setMainController(MainSheetController mainSheetController) {
        this.mainSheetController = mainSheetController;
    }

    @FXML
    void addRangeButtonListener(ActionEvent event) {
        String alertTitle = "Add range";
        try {
            String rangeName = addRangeNameTextInput.getText();

            if (!rangeName.isEmpty()) {
                String fromPositionStr = addFromRangeTextInput.getText();
                String toPositionStr = addToRangeTextInput.getText();

                // create the request body
                String addRangeBodyJson = GSON_INSTANCE.toJson(new RangeBody(rangeName, fromPositionStr, toPositionStr));
                MediaType mediaType = MediaType.get(JSON_MEDIA_TYPE);
                RequestBody requestBody = RequestBody.create(addRangeBodyJson, mediaType);

                Request request = new Request.Builder()
                        .url(RANGE_ENDPOINT)
                        .post(requestBody)
                        .build();

                Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
                if (response.isSuccessful()) {
                    modelUi.isRangeAddedProperty().set(true);
                    modelUi.isRangeAddedProperty().set(false);
                    AlertsHandler.HandleOkAlert("Range " + rangeName + " added successfully!");
                } else {
                    ServerException.ErrorResponse errorResponse = GSON_INSTANCE.fromJson(response.body().string(), ServerException.ErrorResponse.class);
                    AlertsHandler.HandleErrorAlert(alertTitle, errorResponse.getMessage());
                }
            } else {
                AlertsHandler.HandleErrorAlert(alertTitle, "Range name cannot be empty");
            }
        } catch (Exception e) {
            AlertsHandler.HandleErrorAlert(alertTitle, e.getMessage());
        }
    }

    @FXML
    void deleteRangeButtonListener(ActionEvent event) {
        try {
            String rangeName = deleteRangeNameChoiceBox.getValue();
            String url = HttpUrl
                .parse(RANGE_ENDPOINT)
                .newBuilder()
                .addQueryParameter(RANGE_NAME, rangeName)
                .build()
                .toString();

            Request request = new Request.Builder()
                    .url(url)
                    .delete()
                    .build();

            Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
            if (response.isSuccessful()) {
                modelUi.removeRange(rangeName);
                deleteRangeNameChoiceBox.setValue(null); // clean current choice
                mainSheetController.removeCellsPaints();
                AlertsHandler.HandleOkAlert("Range " + rangeName + " deleted successfully!");
            } else {
                ServerException.ErrorResponse errorResponse = GSON_INSTANCE.fromJson(response.body().string(), ServerException.ErrorResponse.class);
                AlertsHandler.HandleErrorAlert("Delete range", errorResponse.getMessage());
            }
        } catch (Exception e) {
            AlertsHandler.HandleErrorAlert("Delete range", e.getMessage());
        }
    }

    @FXML
    void tableViewOnMouseClickedListener(MouseEvent event) throws IOException {
        RangeModelUI.RangeTableEntry selectedRow = showRangesTable.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            mainSheetController.showCellsInRange(selectedRow.nameProperty().getValue());
        }
    }

    private void updateRangesTableAndDeleteRangeChoiceBox(List<String> rangeNames) {
        Platform.runLater(() -> {
            ObservableList<RangeModelUI.RangeTableEntry> tableItems = showRangesTable.getItems();
            ObservableList<String> choiceBoxItems = deleteRangeNameChoiceBox.getItems();

            tableItems.clear();
            choiceBoxItems.clear();

            for (String rangeName: rangeNames) {
                String url = HttpUrl
                    .parse(RANGE_ENDPOINT)
                    .newBuilder()
                    .addQueryParameter(RANGE_NAME, rangeName)
                    .build()
                    .toString();

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response;
                try {
                    response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
                    String responseBody = response.body().string();
                    if (response.isSuccessful()) {
                        RangeDto rangeDto = GSON_INSTANCE.fromJson(responseBody, RangeDto.class);
                        modelUi.addRange(rangeName, rangeDto);
                    } else {
                        System.out.println("Error: " + responseBody);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void startRangesRefresher() {
        rangesRefresher = new RangesRefresher(
                this::updateRangesTableAndDeleteRangeChoiceBox);
        timer = new Timer();
        timer.schedule(rangesRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void setActive() {
        startRangesRefresher();
    }

    public void setIsUserWriter(boolean isWriter) {
        modelUi.isUserWriterProperty().set(isWriter);
    }

    @Override
    public void close() {
        if (rangesRefresher != null && timer != null) {
            rangesRefresher.cancel();
            timer.cancel();
        }
    }
}