package client.component.sheet.range;

import client.component.alert.AlertsHandler;
import client.util.http.HttpClientUtil;
import dto.sheet.RangeDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import client.component.sheet.mainsheet.MainSheetController;
import okhttp3.*;
import serversdk.request.body.RangeBody;

import java.util.Arrays;
import java.util.List;

import static client.resources.CommonResourcesPaths.*;
import static serversdk.request.parameter.RequestParameters.RANGE_NAME;

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
    void tableViewOnMouseClickedListener(MouseEvent event) {
        RangeModelUI.TableEntry selectedRow = showRangesTable.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            mainSheetController.showCellsInRange(selectedRow.nameProperty().getValue());
        }
    }
}