package ui.impl.graphic.components.grid;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.CellDto;
import engine.entity.dto.RowDto;
import engine.entity.dto.SheetDto;
import engine.entity.range.Range;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import ui.impl.graphic.components.alert.AlertsHandler;
import ui.impl.graphic.components.app.MainAppController;

import java.util.*;

public class GridController {

    @FXML private ScrollPane scrollPane;
    @FXML private GridPane mainGridPane;

    private MainAppController mainAppController;
    private Engine engine;
    private GridModelUI modelUi;
    private final List<Label> currentlyPaintedCells = new ArrayList<>(); // List to store painted cells
    private Label clickedLabel;
    private int numOfRows, numOfColumns, defaultRowHeight, defaultColumnWidth;

    private final String CELL_CSS_CLASS = "cell";
    private final String INFLUENCED_CELL_CSS_CLASS = "influenced-cell";
    private final String INFLUENCING_CELL_CSS_CLASS = "influencing-cell";
    private final String CLICKED_CELL_CSS_CLASS = "clicked";
    private final String OF_RANGE_CSS_CLASS = "of-range";
    private final String COLUMN_HEADER_CSS_CLASS = "column-header";
    private final String ROW_HEADER_CSS_CLASS = "row-header";
    private final String COPIED_CELL_PREFIX_CSS_CLASS = "-copied";
    private double currentStepSize = 1;
    private double currentFromRange = 0;
    private double currentToRange = 100;


    @FXML
    private void initialize() {
        modelUi = new GridModelUI(mainGridPane);
    }

    public void setMainController(MainAppController mainAppController, Engine engine) {
        this.mainAppController = mainAppController;
        this.engine = engine;
    }

    public void fileIsLoading(boolean isStarted) {
        modelUi.isFileLoadingProperty().set(isStarted);
    }

    public void initMainGrid(SheetDto sheetDto) {
        numOfRows = engine.getNumOfSheetRows();
        numOfColumns = engine.getNumOfSheetColumns();
        defaultRowHeight = engine.getSheetRowHeight();
        defaultColumnWidth = engine.getSheetColumnWidth();

         // Clear the existing content in the gridContainer
        mainGridPane.getChildren().clear();

        setGridColumnsHeaders(mainGridPane, numOfColumns);
        setGridRowsHeaders(mainGridPane);
        setMainGridCells(sheetDto);

        fileIsLoading(false);
    }

    private void setGridColumnsHeaders(GridPane gridPane, int numOfColumns) {
        // Add the column headers (A, B, C, ...)
        for (int col = 0; col < numOfColumns; col++) {
            String colStr = String.valueOf((char) ('A' + col));
            Label columnHeader = new Label(colStr);
            columnHeader.getStyleClass().add(COLUMN_HEADER_CSS_CLASS);
            columnHeader.setId(colStr);
            columnHeader.setMinWidth(defaultColumnWidth);
            columnHeader.setPrefWidth(defaultColumnWidth);
            columnHeader.setMaxWidth(defaultColumnWidth);
            gridPane.add(columnHeader, col + 1, 0);  // Place the column header in the first row
        }
    }

    private void setGridRowsHeaders(GridPane gridPane) {
        // Add the row headers (1, 2, 3, ...)
        for (int row = 0; row < numOfRows; row++) {
            String rowStr = String.valueOf(row + 1);
            Label rowHeader = new Label(rowStr);
            rowHeader.getStyleClass().add(ROW_HEADER_CSS_CLASS);
            rowHeader.setId(rowStr);
            rowHeader.setPrefSize(20, defaultRowHeight);
            rowHeader.setMinSize(20, defaultRowHeight);
            rowHeader.setPrefSize(20, defaultRowHeight);
            rowHeader.setMaxHeight(defaultRowHeight);
            gridPane.add(rowHeader, 0, row + 1);  // Place the row header in the first column
        }
    }

    private void setMainGridCells(SheetDto sheetDto) {
        // Populate the GridPane with Labels in the main grid area
        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfColumns; col++) {
                CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(row+1, col+1);
                Label label = new Label();
                label.getStyleClass().add(CELL_CSS_CLASS);
                label.setId((char) ('A' + col) + String.valueOf(row + 1));
                modelUi.setCellLabelBinding(label, sheetDto, cellPositionInSheet);

                label.setMinHeight(defaultRowHeight);
                label.setPrefHeight(defaultRowHeight);
                label.setMaxHeight(defaultRowHeight);

                label.setMinWidth(defaultColumnWidth);
                label.setPrefWidth(defaultColumnWidth);
                label.setMaxWidth(defaultColumnWidth);

                // Attach the click event handler
                label.setOnMouseClicked(this::handleCellClick);

                mainGridPane.add(label, col + 1, row + 1);  // Offset by 1 to leave space for headers
            }
        }

        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfColumns; col++) {
                CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(row + 1, col + 1);
                modelUi.setRowsAndColumnsBindings(cellPositionInSheet);
            }
        }
    }

    @FXML
    private void handleCellClick(MouseEvent event) {
        clickedLabel = (Label) event.getSource();
        CellDto cellDto = mainAppController.cellClicked(clickedLabel);
        setClickedCellColors(cellDto);
    }

    private void setClickedCellColors(CellDto cellDto) {
        // Clear the previously painted cells
        clearPaintedCells();

        clickedLabel.getStyleClass().add(CLICKED_CELL_CSS_CLASS);
        currentlyPaintedCells.add(clickedLabel); // Keep track of the painted cells

        if (cellDto != null) {
            // Paint some influences and influenced by cells based on the clicked cell
            List<Label> influencesCellsLabels = getInfluencesCellsToPaint(cellDto);
            List<Label> influencedByCellsLabels = getInfluencedByCellsToPaint(cellDto);
            for (Label cellLabel : influencesCellsLabels) {
                cellLabel.getStyleClass().add(INFLUENCED_CELL_CSS_CLASS);
                currentlyPaintedCells.add(cellLabel); // Keep track of the painted cells
            }
            for (Label cellLabel : influencedByCellsLabels) {
                cellLabel.getStyleClass().add(INFLUENCING_CELL_CSS_CLASS);
                currentlyPaintedCells.add(cellLabel); // Keep track of the painted cells
            }
        }
    }

    private void setRangeCellsColors(String rangeName) {
        // Clear the previously painted cells
        clearPaintedCells();

        // get cell positions
        List<Label> cellsToPainter = getRangeCellsToPaint(rangeName);
        for (Label cellLabel : cellsToPainter) {
            cellLabel.getStyleClass().add(OF_RANGE_CSS_CLASS);
            currentlyPaintedCells.add(cellLabel);
        }
    }

    private List<Label> getInfluencesCellsToPaint(CellDto cellDto) {
        List<Label> influencesCellsLabels = new ArrayList<>();
        Set<CellPositionInSheet> influencesCells = cellDto.getInfluences();

        influencesCells.forEach(influencesCellPosition ->
                influencesCellsLabels.add((Label) mainGridPane.lookup("#" + influencesCellPosition))
        );

        return influencesCellsLabels;
    }

    private List<Label> getInfluencedByCellsToPaint(CellDto cellDto) {
        List<Label> influencedByCellsLabels = new ArrayList<>();
        Set<CellPositionInSheet> influencedByCells = cellDto.getInfluencedBy();

        influencedByCells.forEach(influencedByCellPosition ->
                influencedByCellsLabels.add((Label) mainGridPane.lookup("#" + influencedByCellPosition))
        );

        return influencedByCellsLabels;
    }

    private List<Label> getRangeCellsToPaint(String rangeName) {
        List<Label> rangeCellsLabels = new ArrayList<>();
        Set<CellPositionInSheet> rangeCellPositions = engine.getRangeByName(rangeName).getIncludedPositions();

        rangeCellPositions.forEach(position ->
                rangeCellsLabels.add((Label) mainGridPane.lookup("#" + position))
        );

        return rangeCellsLabels;
    }

    // Method to clear the previously painted cells
    private void clearPaintedCells() {
        for (Label cell : currentlyPaintedCells) {
            cell.getStyleClass().removeAll(INFLUENCED_CELL_CSS_CLASS, CLICKED_CELL_CSS_CLASS,
                    INFLUENCING_CELL_CSS_CLASS, OF_RANGE_CSS_CLASS);
        }
        currentlyPaintedCells.clear(); // Clear the list after un-painting
    }

    public void cellUpdated(CellPositionInSheet cellPositionInSheet, CellDto cellDto) {
        SimpleStringProperty displayedValue = modelUi.getCellPosition2displayedValue().get(cellPositionInSheet).
                displayedValueProperty();
        displayedValue.setValue(cellDto.getEffectiveValueForDisplay().toString());
        // Update the visible affected cells
        cellDto.getInfluences().forEach(influencedPosition -> {
            SimpleStringProperty visibleValue = modelUi.getCellPosition2displayedValue().get(influencedPosition).
                    displayedValueProperty();
            CellDto influencedCell = engine.findCellInSheet(influencedPosition.getRow(), influencedPosition.getColumn(), engine.getCurrentSheetVersion());
            visibleValue.setValue(influencedCell.getEffectiveValueForDisplay().toString());
        });
        setClickedCellColors(cellDto);
    }

    public void setGridOnLastVersionCells(GridPane gridPane, SheetDto sheetDto) {
        // Populate the GridPane with Labels in the main grid area
        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfColumns; col++) {
                CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(row+1, col+1);
                Label label = new Label();
                label.getStyleClass().add(CELL_CSS_CLASS);
                String cellDisplayedValue = sheetDto.getCell(cellPositionInSheet) == null
                        ? ""
                        : sheetDto.getCell(cellPositionInSheet).getEffectiveValueForDisplay().toString();
                label.setText(cellDisplayedValue);
                label.setPrefWidth(defaultColumnWidth);
                label.setMinWidth(defaultColumnWidth);
                label.setMaxWidth(defaultColumnWidth);
                label.setPrefHeight(defaultRowHeight);
                label.setMinHeight(defaultRowHeight);
                label.setMaxHeight(defaultRowHeight);

                gridPane.add(label, col + 1, row + 1);  // Offset by 1 to leave space for headers
            }
        }
    }

    public void showSheetInVersion(SheetDto sheetDto, int version) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Show sheet on specific version");
        dialog.setHeaderText("Sheet version: " + version);

        GridPane gridPane = getUnStyledGrid(sheetDto);

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
        dialog.showAndWait();
    }

    private boolean isPositionInSortedRow(LinkedList<RowDto> sortedRows, CellPositionInSheet cellPositionInSheet) {
        for (RowDto row : sortedRows) {
            if (row.getRowNumber() == cellPositionInSheet.getRow()) {
                return true;
            }
        }
        return false;
    }

    public void showSortedSheet(LinkedList<RowDto> sortedRows, Range rangeToSort) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Show sorted sheet");

        GridPane sortedGrid = getCopiedMainGreed();
        Iterator<CellPositionInSheet> positionInRangeIterator = rangeToSort.getIncludedPositions().iterator();

        for (RowDto row : sortedRows) {
            for (Map.Entry<String, CellDto> column2cell : row.getCells().entrySet()) {
                CellPositionInSheet positionInRange = positionInRangeIterator.next();
                while (positionInRangeIterator.hasNext() && !isPositionInSortedRow(sortedRows, positionInRange)) {
                    positionInRange = positionInRangeIterator.next();
                }
                Label cellInOriginalGrid = (Label) mainGridPane.lookup("#" + column2cell.getKey() + row.getRowNumber());
                Label cellInSortedGrid = (Label) sortedGrid.lookup("#" + positionInRange + COPIED_CELL_PREFIX_CSS_CLASS);
                copyCellStyle(cellInOriginalGrid, cellInSortedGrid);
            }
        }

        dialog.getDialogPane().setContent(sortedGrid);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
        dialog.showAndWait();
    }

    private void copyCellStyle(Label originalCell, Label copiedCell) {
        copiedCell.setText(originalCell.getText());
        copiedCell.setTextFill(originalCell.getTextFill());
        copiedCell.setBackground(originalCell.getBackground());
    }

    private GridPane getCopiedMainGreed() {
        GridPane gridPane = new GridPane();
        gridPane.setPrefWidth(700);

        // Add the row headers (1, 2, 3, ...)
        for (int row = 0; row < numOfRows; row++) {
            String rowStr = String.valueOf(row + 1);
            Label rowHeaderInOriginalGrid = (Label) mainGridPane.lookup("#" + rowStr);
            Label rowHeaderInCopiedGrid = new Label(rowStr);
            rowHeaderInCopiedGrid.setPrefSize(rowHeaderInOriginalGrid.getPrefWidth(), rowHeaderInOriginalGrid.getPrefHeight());
            rowHeaderInCopiedGrid.setMinSize(rowHeaderInOriginalGrid.getMinWidth(), rowHeaderInOriginalGrid.getMinHeight());
            rowHeaderInCopiedGrid.setMaxHeight(rowHeaderInOriginalGrid.getMaxHeight());
            rowHeaderInCopiedGrid.setBorder(rowHeaderInOriginalGrid.getBorder());
            gridPane.add(rowHeaderInCopiedGrid, 0, row + 1);  // Place the row header in the first column
        }

        // Add the column headers (A, B, C, ...)
        for (int col = 0; col < numOfColumns; col++) {
            String colStr = String.valueOf((char) ('A' + col));
            Label columnHeaderInOriginalGrid = (Label) mainGridPane.lookup("#" + colStr);
            Label columnHeaderInCopiedGrid = new Label(colStr);
            columnHeaderInCopiedGrid.setMinWidth(columnHeaderInOriginalGrid.getMinWidth());
            columnHeaderInCopiedGrid.setPrefWidth(columnHeaderInOriginalGrid.getPrefWidth());
            columnHeaderInCopiedGrid.setMaxWidth(columnHeaderInOriginalGrid.getMaxWidth());
            columnHeaderInCopiedGrid.setBorder(columnHeaderInOriginalGrid.getBorder());
            gridPane.add(columnHeaderInCopiedGrid, col + 1, 0);  // Place the column header in the first row
        }

        // Populate the GridPane with Labels (sheet cells)
        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfColumns; col++) {
                CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(row+1, col+1);
                Label cellInOriginalGrid = (Label) mainGridPane.lookup("#" + CellPositionInSheet.parseColumn(cellPositionInSheet.getColumn()) + cellPositionInSheet.getRow());
                Label cellInCopiedGrid = new Label();
                cellInCopiedGrid.setId(CellPositionInSheet.parseColumn(cellPositionInSheet.getColumn()) + cellPositionInSheet.getRow() + COPIED_CELL_PREFIX_CSS_CLASS);
                cellInCopiedGrid.setText(cellInOriginalGrid.getText());
                cellInCopiedGrid.setPrefWidth(cellInOriginalGrid.getPrefWidth());
                cellInCopiedGrid.setMinWidth(cellInOriginalGrid.getMinWidth());
                cellInCopiedGrid.setMaxWidth(cellInOriginalGrid.getMaxWidth());
                cellInCopiedGrid.setPrefHeight(cellInOriginalGrid.getPrefHeight());
                cellInCopiedGrid.setMinHeight(cellInOriginalGrid.getMinHeight());
                cellInCopiedGrid.setMaxHeight(cellInOriginalGrid.getMaxHeight());
                cellInCopiedGrid.setBorder(Border.stroke(Color.GRAY));

                cellInCopiedGrid.setBackground(cellInOriginalGrid.getBackground());
                cellInCopiedGrid.setTextFill(cellInOriginalGrid.getTextFill());
                cellInCopiedGrid.setAlignment(cellInOriginalGrid.getAlignment());

                gridPane.add(cellInCopiedGrid, col + 1, row + 1);  // Offset by 1 to leave space for headers
            }
        }

        return gridPane;
    }

    public void showFilteredSheet(LinkedList<RowDto> filteredRows, Range rangeToFilter) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Show filtered sheet");

        GridPane filteredGrid = getCopiedMainGreed();
        Iterator<CellPositionInSheet> positionInRangeIterator = rangeToFilter.getIncludedPositions().iterator();

        for (RowDto row : filteredRows) {
            for (Map.Entry<String, CellDto> column2cell : row.getCells().entrySet()) {
                CellPositionInSheet positionInRange = positionInRangeIterator.next();
                Label cellInOriginalGrid = (Label) mainGridPane.lookup("#" + column2cell.getKey() + row.getRowNumber());
                Label cellInSortedGrid = (Label) filteredGrid.lookup("#" + positionInRange + COPIED_CELL_PREFIX_CSS_CLASS);
                copyCellStyle(cellInOriginalGrid, cellInSortedGrid);
            }
        }

        if (positionInRangeIterator.hasNext()) {
            while (positionInRangeIterator.hasNext()) {
                CellPositionInSheet positionInRange = positionInRangeIterator.next();
                Label cellInSortedGrid = (Label) filteredGrid.lookup("#" + positionInRange + COPIED_CELL_PREFIX_CSS_CLASS);
                cellInSortedGrid.setText("");
                cellInSortedGrid.setBackground(Background.fill(Color.WHITE));
            }
        }

        dialog.getDialogPane().setContent(filteredGrid);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
        dialog.showAndWait();
    }

    private GridPane getUnStyledGrid(SheetDto sheetDto) {
        GridPane gridPane = new GridPane();
        gridPane.setPrefWidth(700);

        setGridColumnsHeaders(gridPane, numOfColumns);
        setGridRowsHeaders(gridPane);
        setGridOnLastVersionCells(gridPane, sheetDto);

        for (Node node : gridPane.getChildren()) {
            if (node instanceof Label label) {
                label.setStyle("-fx-border-color: black; -fx-alignment: top-center;");
            }
        }
        return gridPane;
    }

    public void showCellsInRange(String name) {
        setRangeCellsColors(name);
    }

    public void removeCellsPaints() {
        clearPaintedCells();
    }

    public void changeCellBackground(String cellId, Color cellBackgroundColor) {
        CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellId);
        modelUi.getCellPosition2displayedValue().get(cellPositionInSheet).backgroundColorProperty().setValue(cellBackgroundColor);
    }

    public void changeCellTextColor(String cellId, Color cellTextColor) {
        CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellId);
        modelUi.getCellPosition2displayedValue().get(cellPositionInSheet).textColorProperty().setValue(cellTextColor);
    }

    public void changeColumnTextAlignment(String cellId, Pos columnTextAlignment) {
        CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellId);
        modelUi.getCellPosition2displayedValue().get(cellPositionInSheet).textAlignmentProperty().setValue(columnTextAlignment);
    }

    public void changeRowHeight(String cellId, int rowHeight) {
        CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellId);
        modelUi.getCellPosition2displayedValue().get(cellPositionInSheet).rowHeightProperty().setValue(rowHeight);
    }

    public void changeColumnWidth(String cellId, int columnWidth) {
        CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellId);
        modelUi.getCellPosition2displayedValue().get(cellPositionInSheet).columnWidthProperty().setValue(columnWidth);
    }

    public void updateCellColors(String cellId, Color cellBackgroundColor, Color cellTextColor) {
        changeCellBackground(cellId, cellBackgroundColor);
        changeCellTextColor(cellId, cellTextColor);
    }

    public void showDynamicAnalysis(String cellId) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Dynamic analysis");
        dialog.setHeaderText("Dynamic analysis of cell in position: " + cellId);

        GridPane copiedGrid = getCopiedMainGreed();

        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfColumns; col++) {
                CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(row + 1, col + 1);
                Label cellLabel = (Label) copiedGrid.lookup("#" + CellPositionInSheet.parseColumn(cellPositionInSheet.getColumn()) + cellPositionInSheet.getRow() + COPIED_CELL_PREFIX_CSS_CLASS);
                SheetDto sheetDto = engine.getSheet(engine.getCurrentSheetVersion());
                modelUi.setCellLabelBindingDynamicAnalysis(cellLabel, sheetDto, cellPositionInSheet);
            }
        }

        TextField fromRangeTextField = new TextField();
        TextField toRangeTextField = new TextField();
        TextField stepSizeTextField = new TextField();

        fromRangeTextField.setPromptText("default: " + currentFromRange);
        toRangeTextField.setPromptText("default: " + currentToRange);
        stepSizeTextField.setPromptText("default: " + currentStepSize);

        Label fromRangeLabel = new Label("From number:");
        Label toRangeLabel = new Label("To number:");
        Label stepSizeLabel = new Label("Step size:");

        Slider slider = new Slider(0, 600, 14);

        // Create the content for the dialog
        GridPane dialogGridPane = new GridPane();
        dialogGridPane.setHgap(10);
        dialogGridPane.setVgap(10);

        GridPane textFieldsGridPane = new GridPane();
        textFieldsGridPane.setHgap(10);
        textFieldsGridPane.setVgap(10);

        textFieldsGridPane.add(fromRangeLabel, 0, 0);
        textFieldsGridPane.add(fromRangeTextField, 1, 0);
        textFieldsGridPane.add(toRangeLabel, 0, 1);
        textFieldsGridPane.add(toRangeTextField, 1, 1);
        textFieldsGridPane.add(stepSizeLabel, 0, 2);
        textFieldsGridPane.add(stepSizeTextField, 1, 2);
        textFieldsGridPane.add(slider, 1, 3);

        dialogGridPane.add(textFieldsGridPane, 0, 0);
        dialogGridPane.add(copiedGrid, 1, 0);

        fromRangeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.isEmpty()) {
                    slider.setMin(Double.parseDouble(fromRangeTextField.getText()));
                } else {
                    slider.setMin(0);
                }
            } catch (Exception e) {
                AlertsHandler.HandleErrorAlert("Set from range", "Please enter a valid number");
            }
        });

        toRangeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.isEmpty()) {
                    slider.setMax(Double.parseDouble(toRangeTextField.getText()));
                } else {
                    slider.setMax(1000);
                }
            } catch (Exception e) {
                AlertsHandler.HandleErrorAlert("Set to range", "Please enter a valid number");
            }
        });

        stepSizeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.isEmpty()) {
                    currentStepSize = Double.parseDouble(newValue);
                } else {
                    currentStepSize = 1;
                }
                slider.setMajorTickUnit(currentStepSize);
                slider.setBlockIncrement(currentStepSize);
                slider.setSnapToTicks(true); // Ensure the slider snaps to the defined ticks
                slider.setShowTickMarks(true);
                slider.setMinorTickCount(0); // No minor ticks
            } catch (NumberFormatException e) {
                AlertsHandler.HandleErrorAlert("Set step size", "Please enter a valid number");
            }
        });

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Calculate the nearest value according to the step size
            double newValueRounded = Math.round(newValue.doubleValue() / currentStepSize) * currentStepSize;
            slider.setValue(newValueRounded); // Set the slider to the rounded value
            System.out.println(newValueRounded); // Print the adjusted value

            CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellId);
            SheetDto newSheetDto = engine.getSheetAfterDynamicAnalysisOfCell(cellPositionInSheet, newValueRounded);
            SimpleStringProperty displayedValue = modelUi.getCellPosition2displayedValueDynamicAnalysis().get(cellPositionInSheet).
                displayedValueProperty();

            CellDto cellDto = newSheetDto.getCell(cellPositionInSheet);
            displayedValue.setValue(cellDto.getEffectiveValueForDisplay().toString());
            // Update the visible affected cells
            cellDto.getInfluences().forEach(influencedPosition -> {
                SimpleStringProperty visibleValue = modelUi.getCellPosition2displayedValueDynamicAnalysis().get(influencedPosition).
                        displayedValueProperty();
                CellDto influencedCell = newSheetDto.getCell(influencedPosition);
                visibleValue.setValue(influencedCell.getEffectiveValueForDisplay().toString());
            });
        });

        dialog.getDialogPane().setContent(dialogGridPane);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
        dialog.showAndWait();
    }
}