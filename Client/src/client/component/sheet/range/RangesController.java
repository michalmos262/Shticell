package client.component.sheet.range;

import client.component.alert.AlertsHandler;
import client.util.http.HttpClientUtil;
import com.google.gson.reflect.TypeToken;
import dto.sheet.RangeDto;
import dto.sheet.RowDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import client.component.sheet.mainsheet.MainSheetController;
import okhttp3.*;
import serversdk.request.body.RangeBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static client.resources.CommonResourcesPaths.*;
import static serversdk.request.parameter.RequestParameters.*;

public class RangesController {

    @FXML private TextField addFromRangeTextInput;
    @FXML private TitledPane addNewRangeTitledPane;
    @FXML private Button addRangeButton;
    @FXML private TextField addRangeNameTextInput;
    @FXML private TextField addToRangeTextInput;
    @FXML private Button deleteRangeButton;
    @FXML private ChoiceBox<String> deleteRangeNameChoiceBox;
    @FXML private TitledPane deleteRangeTitledPane;
    @FXML private TableView<RangeModelUI.TableEntry> showRangesTable;
    @FXML private TableColumn<RangeModelUI.TableEntry, String> nameColumn;
    @FXML private TableColumn<RangeModelUI.TableEntry, String> rangeColumn;
    @FXML private TitledPane showRangesTitledPane;

    private MainSheetController mainSheetController;
    private RangeModelUI modelUi;

    @FXML
    private void initialize() {
        List<TextField> textFields = Arrays.asList(addFromRangeTextInput, addRangeNameTextInput, addToRangeTextInput);
        modelUi = new RangeModelUI(showRangesTable, nameColumn, rangeColumn, deleteRangeNameChoiceBox, textFields);
    }

    public void setMainController(MainSheetController mainSheetController) {
        this.mainSheetController = mainSheetController;
    }

    public void init() throws IOException {
        Request sheetNamesRequest = new Request.Builder()
                .url(RANGE_NAMES_ENDPOINT)
                .build();

        Response sheetNamesResponse = HttpClientUtil.HTTP_CLIENT.newCall(sheetNamesRequest).execute();
        String responseBody = sheetNamesResponse.body().string();

        if (sheetNamesResponse.isSuccessful()) {
            Type listType = new TypeToken<List<String>>(){}.getType();
            List<String> rangeNames = GSON_INSTANCE.fromJson(responseBody, listType);

            for (String rangeName : rangeNames) {
                String url = HttpUrl
                        .parse(RANGE_ENDPOINT)
                        .newBuilder()
                        .addQueryParameter(RANGE_NAME, rangeName)
                        .build()
                        .toString();

                Request rangeRequest = new Request.Builder()
                        .url(url)
                        .build();

                Response rangeResponse = HttpClientUtil.HTTP_CLIENT.newCall(rangeRequest).execute();
                responseBody = rangeResponse.body().string();
                if (sheetNamesResponse.isSuccessful()) {
                    RangeDto range = GSON_INSTANCE.fromJson(responseBody, RangeDto.class);
                    modelUi.addRange(rangeName, range);
                } else {
                    System.out.println("Error: " + responseBody);
                }
            }
        } else {
            System.out.println("Error: " + responseBody);
        }
    }

    @FXML
    void addRangeButtonListener(ActionEvent event) {
        String alertTitle = "Add range";
        try {
            String rangeName = addRangeNameTextInput.getText();

            if (!rangeName.isEmpty()) {
                String fromPositionStr = addFromRangeTextInput.getText();
                String toPositionStr = addRangeNameTextInput.getText();

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
                    String responseBody = response.body().string();
                    RangeDto rangeDto = GSON_INSTANCE.fromJson(responseBody, RangeDto.class);
                    modelUi.addRange(rangeName, rangeDto);
                    modelUi.isRangeAddedProperty().set(true);
                    modelUi.isRangeAddedProperty().set(false);
                    AlertsHandler.HandleOkAlert("Range " + rangeName + " added successfully!");
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
                    .build();

            Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
            if (response.isSuccessful()) {
                modelUi.removeRange(rangeName);
                deleteRangeNameChoiceBox.setValue(null); // clean current choice
                mainSheetController.removeCellsPaints();
                AlertsHandler.HandleOkAlert("Range " + rangeName + " deleted successfully!");
            }
        } catch (Exception e) {
            AlertsHandler.HandleErrorAlert("Delete range", e.getMessage());
        }
    }

    @FXML
    void tableViewOnMouseClickedListener(MouseEvent event) throws IOException {
        RangeModelUI.TableEntry selectedRow = showRangesTable.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            mainSheetController.showCellsInRange(selectedRow.nameProperty().getValue());
        }
    }
}