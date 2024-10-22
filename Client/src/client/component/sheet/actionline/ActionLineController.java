package client.component.sheet.actionline;

import client.component.alert.AlertsHandler;
import client.util.http.HttpClientUtil;
import dto.cell.CellDto;
import dto.sheet.SheetDimensionDto;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import client.component.sheet.mainsheet.MainSheetController;
import okhttp3.*;
import dto.cell.CellTypeDto;
import org.jetbrains.annotations.NotNull;
import serversdk.exception.ServerException;
import serversdk.request.body.EditCellBody;

import static client.resources.CommonResourcesPaths.*;
import static serversdk.request.parameter.RequestParameters.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

public class ActionLineController implements Closeable {

    @FXML private Label lastCellVersionLabel;
    @FXML private Label selectedCellIdLabel;
    @FXML private TextField originalCellValueTextField;
    @FXML private Label updatedByLabel;
    @FXML private Button updateValueButton;
    @FXML private Button showSheetVersionButton;
    @FXML private Button backToDefaultDesignButton;
    @FXML private Button dynamicAnalysisButton;
    @FXML private Button moveToNewestVersionButton;
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
    private ActionLineRefresher actionLineRefresher;
    private Timer timer;

    @FXML
    private void initialize() {
        // put all possible text alignments in columnTextAlignmentChoiceBox
        columnTextAlignmentChoiceBox.getItems().addAll(Pos.values());
        columnTextAlignmentChoiceBox.getSelectionModel().selectFirst();

        // set the spinners
        rowHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 500, 0, 1));
        columnWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 500, 0, 1));

        List<Button> writerOnlyButtons = Arrays.asList(updateValueButton, backToDefaultDesignButton);

        defaultCellBackgroundColor = cellBackgroundColorPicker.getValue();
        defaultCellTextColor = cellTextColorPicker.getValue();

        modelUi = new ActionLineModelUI(dynamicAnalysisButton, writerOnlyButtons, originalCellValueTextField, selectedCellIdLabel,
                lastCellVersionLabel, updatedByLabel, showSheetVersionSelector, columnTextAlignmentChoiceBox,
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

    public void initComponent(String sheetName) {
        String url = HttpUrl
                .parse(SHEET_DIMENSION_ENDPOINT)
                .newBuilder()
                .addQueryParameter(SHEET_NAME, sheetName)
                .build()
                .toString();

        HttpClientUtil.runAsyncGet(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    SheetDimensionDto sheetDimensionDto = GSON_INSTANCE.fromJson(responseBody, SheetDimensionDto.class);
                    Platform.runLater(() -> {
                        int rowHeight = sheetDimensionDto.getRowHeight();
                        rowHeightSpinner.getValueFactory().setValue(rowHeight);

                        int columnWidth = sheetDimensionDto.getColumnWidth();
                        columnWidthSpinner.getValueFactory().setValue(columnWidth);

                        clickOnMoveToNewestVersionButton();
                    });
                } else {
                    Platform.runLater(() -> System.out.println("Error: " + responseBody));
                }
            }
        });
    }

    public int getCurrentSheetVersion() {
        return modelUi.currentSheetVersionProperty().getValue();
    }

    public void removeCellClickFocus() {
        modelUi.isAnyCellClickedProperty().set(false);
        modelUi.selectedCellIdProperty().set("");
        modelUi.selectedCellOriginalValueProperty().set("");
        modelUi.selectedUpdatedByNameProperty().set("");
        modelUi.selectedCellLastVersionProperty().set(0);
    }

    @FXML
    void updateValueButtonListener(ActionEvent event) throws IOException {
        if (modelUi.currentSheetVersionProperty().getValue() == mainSheetController.getLastSheetVersion()) {
            String cellId = modelUi.selectedCellIdProperty().getValue();
            String cellNewOriginalValue = originalCellValueTextField.getText();

            // create the request body
            String updateCellBodyJson = GSON_INSTANCE.toJson(new EditCellBody(cellNewOriginalValue));
            MediaType mediaType = MediaType.get(JSON_MEDIA_TYPE);
            RequestBody requestBody = RequestBody.create(updateCellBodyJson, mediaType);

            String url = HttpUrl
                    .parse(CELL_ENDPOINT)
                    .newBuilder()
                    .addQueryParameter(CELL_POSITION, cellId)
                    .build()
                    .toString();

            HttpClientUtil.runAsyncPut(url, requestBody, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    Platform.runLater(() -> updateCellFailed(e.getMessage()));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.isSuccessful()) {
                        CellDto cellDto = GSON_INSTANCE.fromJson(responseBody, CellDto.class);
                        Platform.runLater(() -> {
                            modelUi.selectedCellOriginalValueProperty().set(cellNewOriginalValue);
                            modelUi.selectedCellLastVersionProperty().set(cellDto.getLastUpdatedInVersion());
                            modelUi.selectedUpdatedByNameProperty().set(cellDto.getUpdatedByName());
                            modelUi.currentSheetVersionProperty().set(cellDto.getLastUpdatedInVersion());
                            mainSheetController.cellIsUpdated(cellId, cellDto);
                        });
                    } else {
                        Platform.runLater(() -> {
                            ServerException.ErrorResponse errorResponse = GSON_INSTANCE.fromJson(responseBody, ServerException.ErrorResponse.class);
                            updateCellFailed(errorResponse.getMessage());
                        });
                    }
                }
            });
        } else {
            updateCellFailed("Sheet has a newer version, please move to it first");
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
        if (clickedCell == null) {
            return null;
        }
        String cellPositionId = clickedCell.getId();
        CellDto cellDto = null;

        String url = HttpUrl
                .parse(CELL_ENDPOINT)
                .newBuilder()
                .addQueryParameter(CELL_POSITION, cellPositionId)
                .addQueryParameter(SHEET_VERSION, String.valueOf(modelUi.currentSheetVersionProperty().getValue()))
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
            String updatedByName = cellDto == null ? "" : cellDto.getUpdatedByName();
            modelUi.selectedCellIdProperty().set(cellPositionId);
            modelUi.selectedCellLastVersionProperty().set(lastCellVersion);
            modelUi.selectedCellOriginalValueProperty().set(originalValue);
            modelUi.selectedUpdatedByNameProperty().set(updatedByName);

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
        int sheetVersion = mainSheetController.getLastSheetVersion();

        String url = HttpUrl
                .parse(CELL_ENDPOINT)
                .newBuilder()
                .addQueryParameter(CELL_POSITION, cellId)
                .addQueryParameter(SHEET_VERSION, String.valueOf(sheetVersion))
                .build()
                .toString();

        HttpClientUtil.runAsyncGet(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error on dynamic analysis: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    CellDto cellDto = GSON_INSTANCE.fromJson(responseBody, CellDto.class);
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
                        Platform.runLater(() -> {
                            mainSheetController.showDynamicAnalysis(cellId);
                        });
                    } catch (NumberFormatException e) {
                        Platform.runLater(() ->
                                AlertsHandler.HandleErrorAlert("Dynamic Analysis",
                                        "Dynamic analysis is available only for numeric and not functioned values"));
                    } catch (Exception e) {
                        Platform.runLater(() -> System.out.println("Error on dynamic analysis: " + e.getMessage()));
                    }
                } else {
                    Platform.runLater(() -> System.out.println("Error on dynamic analysis: " + responseBody));
                }
            }
        });
    }

    public void setIsUserWriter(boolean isWriter) {
        modelUi.isUserWriterProperty().set(isWriter);
    }

    @FXML
    void moveToNewestVersionButtonListener(ActionEvent event) throws IOException {
        moveToNewestVersionButton.setEffect(null);
        removeCellClickFocus();
        int newestVersion = mainSheetController.getLastSheetVersion();
        modelUi.currentSheetVersionProperty().set(newestVersion);
        mainSheetController.moveToNewestSheetVersion();
    }

    public void clickOnMoveToNewestVersionButton() {
        // navigates to moveToNewestVersionButtonListener function
        moveToNewestVersionButton.fire();
    }

    private void indicateMoveToNewestVersionButton() {
        DropShadow shadowEffect = new DropShadow();
        shadowEffect.setColor(Color.BLUE);
        shadowEffect.setRadius(10);
        shadowEffect.setSpread(0.3);

        this.moveToNewestVersionButton.setEffect(shadowEffect);

        this.moveToNewestVersionButton.setBorder(new Border(new BorderStroke(
                Color.TRANSPARENT,
                BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(3)
        )));
    }

    public void startMoveToNewestVersionButtonRefresher() {
        actionLineRefresher = new ActionLineRefresher(
                this::indicateMoveToNewestVersionButton,
                modelUi.currentSheetVersionProperty());
        timer = new Timer();
        timer.schedule(actionLineRefresher, 2000, 2000);
    }

    public void setActive() {
        startMoveToNewestVersionButtonRefresher();
    }

    @Override
    public void close() {
        if (actionLineRefresher != null && timer != null) {
            actionLineRefresher.cancel();
            timer.cancel();
        }
    }
}