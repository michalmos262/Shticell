package ui.impl.graphic.components.actionline;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.CellDto;
import engine.operation.Operation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import ui.impl.graphic.components.alert.AlertsHandler;
import ui.impl.graphic.components.app.MainAppController;

import java.util.LinkedList;
import java.util.List;

public class ActionLineController {

    @FXML private Label lastCellVersionLabel;
    @FXML private TextField originalCellValueTextField;
    @FXML private Label selectedCellIdLabel;
    @FXML private Button updateValueButton;
    @FXML private Button showSheetVersionButton;
    @FXML private Button backToDefaultDesignButton;
    @FXML private Button setDesignButton;
    @FXML private ChoiceBox<Pos> columnTextAlignmentChoiceBox;
    @FXML private ChoiceBox<Integer> showSheetVersionSelector;
    @FXML private Spinner<Integer> columnWidthSpinner;
    @FXML private Spinner<Integer> rowHeightSpinner;
    @FXML private ColorPicker cellBackgroundColorPicker;
    @FXML private ColorPicker cellTextColorPicker;

    private MainAppController mainAppController;
    private ActionLineModelUI modelUi;
    private Engine engine;

    @FXML
    private void initialize() {
        // put all possible text alignments in columnTextAlignmentChoiceBox
        columnTextAlignmentChoiceBox.getItems().addAll(Pos.values());
        columnTextAlignmentChoiceBox.getSelectionModel().selectFirst();

        // set the spinners
        rowHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(20, 500, 0, 1));
        columnWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(20, 500, 0, 1));

        List<Button> cellButtons = new LinkedList<>();
        cellButtons.add(updateValueButton);
        cellButtons.add(backToDefaultDesignButton);
        cellButtons.add(setDesignButton);

        modelUi = new ActionLineModelUI(cellButtons, selectedCellIdLabel, originalCellValueTextField,
                lastCellVersionLabel, showSheetVersionSelector, columnTextAlignmentChoiceBox, showSheetVersionButton,
                columnWidthSpinner, rowHeightSpinner, cellBackgroundColorPicker, cellTextColorPicker);
    }

    public void setMainController(MainAppController mainAppController, Engine engine) {
        this.mainAppController = mainAppController;
        this.engine = engine;
    }

    public void removeCellClickFocus() {
        modelUi.isAnyCellClickedProperty().set(false);
        modelUi.selectedCellIdProperty().set("");
        modelUi.selectedCellOriginalValueProperty().set("");
        modelUi.selectedCellLastVersionProperty().set(0);
    }

    public void fileLoadedSuccessfully() {
        fileIsLoading(false);
        removeCellClickFocus();
        modelUi.currentSheetVersionProperty().set(1);
        // Set an initial value
        int rowHeight = engine.getSheetRowHeight();
        rowHeightSpinner.getValueFactory().setValue(rowHeight);

        int columnWidth = engine.getSheetColumnWidth();
        columnWidthSpinner.getValueFactory().setValue(columnWidth);
    }

    public void fileIsLoading(boolean isStarted) {
        modelUi.isFileLoadingProperty().set(isStarted);
    }

    @FXML
    void UpdateValueButtonListener(ActionEvent event) {
        try {
            String cellNewOriginalValue = originalCellValueTextField.getText();
            CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(modelUi.selectedCellIdProperty().getValue());
            CellDto cellDto = engine.updateSheetCell(cellPositionInSheet.getRow(), cellPositionInSheet.getColumn(), cellNewOriginalValue);
            modelUi.selectedCellOriginalValueProperty().set(cellNewOriginalValue);
            modelUi.selectedCellLastVersionProperty().set(engine.getLastCellVersion(cellPositionInSheet.getRow(), cellPositionInSheet.getColumn()));
            modelUi.currentSheetVersionProperty().set(engine.getCurrentSheetVersion());
            mainAppController.cellIsUpdated(cellPositionInSheet, cellDto);
        } catch (Exception e) {
            updateCellFailed(e.getMessage());
        }
//        Button showDetailsButton = new Button("Functions Documentation");
//
//        StringBuilder content = new StringBuilder();
//        for(Operation operation : Operation.values()) {
//            content.append(operation.getDocumentation())
//                    .append("\n");
//        }
//
//        // Create a VBox for additional details
//        VBox detailsBox = new VBox();
//        detailsBox.setStyle("-fx-padding: 10; -fx-background-color: lightgrey;");
//        detailsBox.setVisible(false); // Initially hidden
//
//        // Set content to show in the functions documentation box
//        Label detailsLabel = new Label(String.valueOf(content));
//        detailsBox.getChildren().add(detailsLabel);
    }

    @FXML
    void showSheetVersionButtonListener(ActionEvent event) {
        Integer selectedValue = showSheetVersionSelector.getSelectionModel().getSelectedItem();
        if (selectedValue != null) {
            mainAppController.selectSheetVersion(selectedValue);
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

    public CellDto cellClicked(Label clickedCell) {
        String cellPositionId = clickedCell.getId();
        CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellPositionId);
        CellDto cellDto = engine.getSheet(engine.getCurrentSheetVersion()).getCell(cellPositionInSheet);
        int lastCellVersion = engine.getLastCellVersion(cellPositionInSheet.getRow(), cellPositionInSheet.getColumn());
        String originalValue = cellDto == null ? "" : cellDto.getOriginalValue();

        modelUi.selectedCellIdProperty().set(cellPositionId);
        modelUi.selectedCellLastVersionProperty().set(lastCellVersion);
        modelUi.selectedCellOriginalValueProperty().set(originalValue);

        Color backgroundColor = Color.WHITE;

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

    @FXML
    void backToDefaultDesignButtonListener(ActionEvent event) {

    }

    @FXML
    void setDesignButtonListener(ActionEvent event) {
        String cellId = modelUi.selectedCellIdProperty().get();
        Color cellBackgroundColor = cellBackgroundColorPicker.getValue();
        Color cellTextColor = cellTextColorPicker.getValue();
        Pos columnTextAlignment = columnTextAlignmentChoiceBox.getValue();
        int rowHeight = rowHeightSpinner.getValue();
        int columnWidth = columnWidthSpinner.getValue();

        mainAppController.updateCellDesign(cellId, cellBackgroundColor, cellTextColor, columnTextAlignment, rowHeight, columnWidth);
    }
}