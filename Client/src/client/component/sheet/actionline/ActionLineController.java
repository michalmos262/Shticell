package client.component.sheet.actionline;

import client.component.alert.AlertsHandler;
import client.util.http.HttpClientUtil;
import dto.cell.CellDto;
import dto.sheet.SheetDimensionDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import client.component.sheet.mainsheet.MainSheetController;
import okhttp3.*;
import dto.cell.CellTypeDto;
import serversdk.exception.ServerException;
import serversdk.request.body.CellBody;

import static client.resources.CommonResourcesPaths.*;
import static serversdk.request.parameter.RequestParameters.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ActionLineController {

    @FXML private Label lastCellVersionLabel;
    @FXML private TextField originalCellValueTextField;
    @FXML private Label selectedCellIdLabel;
    @FXML private Button updateValueButton;
    @FXML private Button showSheetVersionButton;
    @FXML private Button backToDefaultDesignButton;
    @FXML private Button dynamicAnalysisButton;
    @FXML private ChoiceBox<Pos> columnTextAlignmentChoiceBox;
    @FXML private ChoiceBox<Integer> showSheetVersionSelector;
    @FXML private Spinner<Integer> columnWidthSpinner;
    @FXML private Spinner<Integer> rowHeightSpinner;
    @FXML private ColorPicker cellBackgroundColorPicker;
    @FXML private ColorPicker cellTextColorPicker;

    private MainSheetController mainSheetController;
    private ActionLineModelUI modelUi;
    private Color defaultCellBackgroundColor;
    private Color defaultCellTextColor;
    private Label clickedCellLabel;

    @FXML
    private void initialize() {
        // put all possible text alignments in columnTextAlignmentChoiceBox
        columnTextAlignmentChoiceBox.getItems().addAll(Pos.values());
        columnTextAlignmentChoiceBox.getSelectionModel().selectFirst();

        // set the spinners
        rowHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 500, 0, 1));
        columnWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 500, 0, 1));

        List<Button> cellButtons = new LinkedList<>();
        cellButtons.add(updateValueButton);
        cellButtons.add(backToDefaultDesignButton);
        cellButtons.add(dynamicAnalysisButton);

        defaultCellBackgroundColor = cellBackgroundColorPicker.getValue();
        defaultCellTextColor = cellTextColorPicker.getValue();

        modelUi = new ActionLineModelUI(cellButtons, selectedCellIdLabel, originalCellValueTextField,
                lastCellVersionLabel, showSheetVersionSelector, columnTextAlignmentChoiceBox,
                columnWidthSpinner, rowHeightSpinner, cellBackgroundColorPicker, cellTextColorPicker);

        rowHeightSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (modelUi.isAnyCellClickedProperty().get()) {
                mainSheetController.changeRowHeight(selectedCellIdLabel.getText(), newValue);
            }
        });

        columnWidthSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (modelUi.isAnyCellClickedProperty().get()) {
                mainSheetController.changeColumnWidth(selectedCellIdLabel.getText(), newValue);
            }
        });

        cellBackgroundColorPicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (modelUi.isAnyCellClickedProperty().get()) {
                mainSheetController.changeCellBackground(selectedCellIdLabel.getText(), newValue);
            }
        });

        cellTextColorPicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (modelUi.isAnyCellClickedProperty().get()) {
                mainSheetController.changeCellTextColor(selectedCellIdLabel.getText(), newValue);
            }
        });

        columnTextAlignmentChoiceBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (modelUi.isAnyCellClickedProperty().get()) {
                mainSheetController.changeColumnTextAlignment(selectedCellIdLabel.getText(), newValue);
            }
        });
    }

    public void setMainController(MainSheetController mainSheetController) {
        this.mainSheetController = mainSheetController;
    }

    public void init(String sheetName) throws IOException {
        String url = HttpUrl
                .parse(SHEET_DIMENSION_ENDPOINT)
                .newBuilder()
                .addQueryParameter(SHEET_NAME, sheetName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
        String responseBody = response.body().string();
        if (response.isSuccessful()) {
            SheetDimensionDto sheetDimensionDto = GSON_INSTANCE.fromJson(responseBody, SheetDimensionDto.class);
            // Set an initial value
            int rowHeight = sheetDimensionDto.getRowHeight();
            rowHeightSpinner.getValueFactory().setValue(rowHeight);

            int columnWidth = sheetDimensionDto.getColumnWidth();
            columnWidthSpinner.getValueFactory().setValue(columnWidth);
        } else {
            System.out.println("Error: " + responseBody);
        }

        modelUi.currentSheetVersionProperty().set(1);
    }

    public void removeCellClickFocus() {
        modelUi.isAnyCellClickedProperty().set(false);
        modelUi.selectedCellIdProperty().set("");
        modelUi.selectedCellOriginalValueProperty().set("");
        modelUi.selectedCellLastVersionProperty().set(0);
    }

    @FXML
    void UpdateValueButtonListener(ActionEvent event) {
        String cellId = modelUi.selectedCellIdProperty().getValue();
        String cellNewOriginalValue = originalCellValueTextField.getText();

        // create the request body
        String updateCellBodyJson = GSON_INSTANCE.toJson(new CellBody(cellNewOriginalValue));
        MediaType mediaType = MediaType.get(JSON_MEDIA_TYPE);
        RequestBody requestBody = RequestBody.create(updateCellBodyJson, mediaType);

        String url = HttpUrl
                .parse(CELL_ENDPOINT)
                .newBuilder()
                .addQueryParameter(CELL_POSITION, cellId)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();

        try {
            Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
            String responseBody = response.body().string();
            if (response.isSuccessful()) {
                CellDto cellDto = GSON_INSTANCE.fromJson(responseBody, CellDto.class);
                modelUi.selectedCellOriginalValueProperty().set(cellNewOriginalValue);
                modelUi.selectedCellLastVersionProperty().set(cellDto.getLastUpdatedInVersion());
                modelUi.currentSheetVersionProperty().set(cellDto.getLastUpdatedInVersion());
                mainSheetController.cellIsUpdated(cellId, cellDto);
            } else {
                ServerException.ErrorResponse errorResponse = GSON_INSTANCE.fromJson(responseBody, ServerException.ErrorResponse.class);
                updateCellFailed(errorResponse.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            updateCellFailed(e.getMessage());
        }
    }

    @FXML
    void showSheetVersionButtonListener(ActionEvent event) {
        Integer selectedValue = showSheetVersionSelector.getSelectionModel().getSelectedItem();
        if (selectedValue != null) {
            mainSheetController.selectSheetVersion(selectedValue);
        } else {
            AlertsHandler.HandleErrorAlert("Show sheet version", "You need to choose a sheet version.");
        }
    }

    public void updateCellFailed(String errorMessage) {
        AlertsHandler.HandleErrorAlert("Error on updating cell", errorMessage);
    }

    public void updateCellSucceeded() {
        AlertsHandler.HandleOkAlert("Update succeeded!");
    }

    public CellDto cellClicked(Label clickedCell) throws IOException {
        this.clickedCellLabel = clickedCell;
        String cellPositionId = clickedCell.getId();
        CellDto cellDto = null;

        String url = HttpUrl
                .parse(CELL_ENDPOINT)
                .newBuilder()
                .addQueryParameter(CELL_POSITION, cellPositionId)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            cellDto = GSON_INSTANCE.fromJson(responseBody, CellDto.class);
            String originalValue = cellDto == null ? "" : cellDto.getOriginalValue();
            int lastCellVersion = cellDto == null ? 0 : cellDto.getLastUpdatedInVersion();
            modelUi.selectedCellIdProperty().set(cellPositionId);
            modelUi.selectedCellLastVersionProperty().set(lastCellVersion);
            modelUi.selectedCellOriginalValueProperty().set(originalValue);

            Color backgroundColor = defaultCellBackgroundColor;

            // Check if the Label has a background
            Background background = clickedCell.getBackground();
            if (background != null && !background.getFills().isEmpty()) {
                backgroundColor = (Color) background.getFills().getFirst().getFill();
            }
            Color textColor = (Color) clickedCell.getTextFill();
            Pos textAlignment = clickedCell.getAlignment();
            int rowHeight = (int) clickedCell.getHeight();
            int columnWidth = (int) clickedCell.getWidth();

            // Update the header based on the selected cell's properties (background color, text color, etc.)
            cellBackgroundColorPicker.setValue(backgroundColor);
            cellTextColorPicker.setValue(textColor);
            columnTextAlignmentChoiceBox.setValue(textAlignment);
            rowHeightSpinner.getValueFactory().setValue(rowHeight);
            columnWidthSpinner.getValueFactory().setValue(columnWidth);

            modelUi.isAnyCellClickedProperty().set(true);

            return cellDto;
        }

        return cellDto;
    }

    @FXML
    void backToDefaultDesignButtonListener(ActionEvent event) throws IOException {
        String cellId = modelUi.selectedCellIdProperty().get();

        mainSheetController.updateCellColors(cellId, defaultCellBackgroundColor, defaultCellTextColor);
        cellClicked(this.clickedCellLabel);
    }

    @FXML
    void dynamicAnalysisButtonListener(ActionEvent event) throws IOException {
        String cellId = modelUi.selectedCellIdProperty().getValue();
        CellDto cellDto;

        String url = HttpUrl
                .parse(CELL_ENDPOINT)
                .newBuilder()
                .addQueryParameter(CELL_POSITION, cellId)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
        String responseBody = response.body().string();
        if (response.isSuccessful()) {
            cellDto = GSON_INSTANCE.fromJson(responseBody, CellDto.class);
            String originalValue = "";
            CellTypeDto cellType = CellTypeDto.UNKNOWN;

            try {
                if (cellDto != null) {
                    originalValue = cellDto.getOriginalValue();
                    cellType = cellDto.getEffectiveValue().getCellType();
                }
                Double.parseDouble(originalValue);
                if (cellType != CellTypeDto.NUMERIC) {
                    throw new NumberFormatException();
                }
                mainSheetController.showDynamicAnalysis(cellId);

            } catch (Exception e) {
                AlertsHandler.HandleErrorAlert("Dynamic Analysis", "Dynamic analysis is available only for numeric and not functioned values");
            }
        } else {
            System.out.println("Error: " + responseBody);
        }
    }
}