package ui.impl.graphic.components.grid;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.CellDto;
import engine.entity.dto.SheetDto;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import ui.impl.graphic.components.app.MainAppController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GridController {

    @FXML private ScrollPane scrollPane;
    @FXML private GridPane gridPane;

    private MainAppController mainAppController;
    private Engine engine;
    private GridModelUI modelUi;
    private final List<Label> currentlyPaintedCells = new ArrayList<>(); // List to store painted cells
    private Label clickedLabel;
    private int numOfRows, numOfColumns, defaultRowHeight, defaultColumnWidth;

    @FXML
    private void initialize() {
        modelUi = new GridModelUI(gridPane);
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
        gridPane.getChildren().clear();

        setGridColumnsHeaders(gridPane, numOfColumns);
        setGridRowsHeaders(gridPane, numOfRows);
        setMainGridCells(sheetDto);

        fileIsLoading(false);
    }

    private void setGridColumnsHeaders(GridPane gridPane, int numOfColumns) {
        // Add the column headers (A, B, C, ...)
        for (int col = 0; col < numOfColumns; col++) {
            String colStr = String.valueOf((char) ('A' + col));
            Label columnHeader = new Label(colStr);
            columnHeader.getStyleClass().add("column-header");
            columnHeader.setId(colStr);
            columnHeader.setMinWidth(defaultColumnWidth);
            columnHeader.setPrefWidth(defaultColumnWidth);
            columnHeader.setMaxWidth(defaultColumnWidth);
            gridPane.add(columnHeader, col + 1, 0);  // Place the column header in the first row
        }
    }

    private void setGridRowsHeaders(GridPane gridPane, int numOfRows) {
        // Add the row headers (1, 2, 3, ...)
        for (int row = 0; row < numOfRows; row++) {
            String rowStr = String.valueOf(row + 1);
            Label rowHeader = new Label(rowStr);
            rowHeader.getStyleClass().add("row-header");
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
                label.getStyleClass().add("cell");
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

                gridPane.add(label, col + 1, row + 1);  // Offset by 1 to leave space for headers
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

        clickedLabel.getStyleClass().add("clicked");
        currentlyPaintedCells.add(clickedLabel); // Keep track of the painted cells

        if (cellDto != null) {
            // Paint some influences and influenced by cells based on the clicked cell
            List<Label> influencesCellsLabels = getInfluencesCellsToPaint(cellDto);
            List<Label> influencedByCellsLabels = getInfluencedByCellsToPaint(cellDto);
            for (Label cellLabel : influencesCellsLabels) {
                cellLabel.getStyleClass().add("influenced-cell");
                currentlyPaintedCells.add(cellLabel); // Keep track of the painted cells
            }
            for (Label cellLabel : influencedByCellsLabels) {
                cellLabel.getStyleClass().add("influencing-cell");
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
            cellLabel.getStyleClass().add("of-range");
            currentlyPaintedCells.add(cellLabel);
        }
    }

    private List<Label> getInfluencesCellsToPaint(CellDto cellDto) {
        List<Label> influencesCellsLabels = new ArrayList<>();
        Set<CellPositionInSheet> influencesCells = cellDto.getInfluences();

        influencesCells.forEach(influencesCellPosition ->
                influencesCellsLabels.add((Label) gridPane.lookup("#" + influencesCellPosition))
        );

        return influencesCellsLabels;
    }

    private List<Label> getInfluencedByCellsToPaint(CellDto cellDto) {
        List<Label> influencedByCellsLabels = new ArrayList<>();
        Set<CellPositionInSheet> influencedByCells = cellDto.getInfluencedBy();

        influencedByCells.forEach(influencedByCellPosition ->
                influencedByCellsLabels.add((Label) gridPane.lookup("#" + influencedByCellPosition))
        );

        return influencedByCellsLabels;
    }

    private List<Label> getRangeCellsToPaint(String rangeName) {
        List<Label> rangeCellsLabels = new ArrayList<>();
        Set<CellPositionInSheet> rangeCellPositions = engine.getRangeByName(rangeName).getIncludedPositions();

        rangeCellPositions.forEach(position ->
                rangeCellsLabels.add((Label) gridPane.lookup("#" + position))
        );

        return rangeCellsLabels;
    }

    // Method to clear the previously painted cells
    private void clearPaintedCells() {
        for (Label cell : currentlyPaintedCells) {
            cell.getStyleClass().removeAll("influenced-cell", "clicked", "influencing-cell", "of-range");
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

    public void setGridOnVersionCells(GridPane gridPane, SheetDto sheetDto) {
        // Populate the GridPane with Labels in the main grid area
        for (int row = 0; row < sheetDto.getNumOfRows(); row++) {
            for (int col = 0; col < numOfColumns; col++) {
                CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(row+1, col+1);
                Label label = new Label();
                label.getStyleClass().add("cell");
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
        dialog.setHeaderText("Sheet version " + version);

        GridPane gridPane = getGrid(sheetDto);

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
        dialog.showAndWait();
    }

    public void showSortedSheet(SheetDto sheetDto) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Show sorted sheet");

        GridPane gridPane = getGrid(sheetDto);

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
        dialog.showAndWait();
    }

    public void showFilteredSheet(SheetDto sheetDto) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Show filtered sheet");

        GridPane gridPane = getGrid(sheetDto);

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
        dialog.showAndWait();
    }

    private GridPane getGrid(SheetDto sheetDto) {
        GridPane gridPane = new GridPane();
        gridPane.setPrefWidth(700);

        setGridColumnsHeaders(gridPane, numOfColumns);
        setGridRowsHeaders(gridPane, sheetDto.getNumOfRows());
        setGridOnVersionCells(gridPane, sheetDto);

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

    public void updateCellDesign(String cellId, Color cellBackgroundColor, Color cellTextColor,
                                 Pos columnTextAlignment, int rowHeight, int columnWidth) {
        CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellId);
        modelUi.getCellPosition2displayedValue().get(cellPositionInSheet).backgroundColorProperty().setValue(cellBackgroundColor);
        modelUi.getCellPosition2displayedValue().get(cellPositionInSheet).textColorProperty().setValue(cellTextColor);
        modelUi.getCellPosition2displayedValue().get(cellPositionInSheet).textAlignmentProperty().setValue(columnTextAlignment);
        modelUi.getCellPosition2displayedValue().get(cellPositionInSheet).rowHeightProperty().setValue(rowHeight);
        modelUi.getCellPosition2displayedValue().get(cellPositionInSheet).columnWidthProperty().setValue(columnWidth);
    }
}