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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
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
        setGridRowsHeaders(mainGridPane, numOfRows);
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

    public void setGridOnLastVersionCells(GridPane gridPane, SheetDto sheetDto) {
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

    public void showSortedSheet(SheetDto sheetDto, LinkedList<RowDto> sortedRows, Range rangeToSort) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Show sorted sheet");

        GridPane sortedGrid = getCopiedMainGreed(sheetDto);
        Iterator<CellPositionInSheet> positionInRangeIterator = rangeToSort.getIncludedPositions().iterator();

        for (RowDto row : sortedRows) {
            for (Map.Entry<String, CellDto> column2cell : row.getCells().entrySet()) {
                CellPositionInSheet positionInRange = positionInRangeIterator.next();
                while (positionInRangeIterator.hasNext() && !isPositionInSortedRow(sortedRows, positionInRange)) {
                    positionInRange = positionInRangeIterator.next();
                }
                Label cellInOriginalGrid = (Label) mainGridPane.lookup("#" + column2cell.getKey() + row.getRowNumber());
                Label cellInSortedGrid = (Label) sortedGrid.lookup("#" + positionInRange + "-copied");
                cellInSortedGrid.setText(cellInOriginalGrid.getText());
                cellInSortedGrid.setTextFill(cellInOriginalGrid.getTextFill());
                cellInSortedGrid.setBackground(cellInOriginalGrid.getBackground());
            }
        }

        dialog.getDialogPane().setContent(sortedGrid);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
        dialog.showAndWait();
    }

    private GridPane getCopiedMainGreed(SheetDto sheetDto) {
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

        // Populate the GridPane with Labels in the main grid area
        for (int row = 0; row < sheetDto.getNumOfRows(); row++) {
            for (int col = 0; col < numOfColumns; col++) {
                CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(row+1, col+1);
                Label cellInOriginalGrid = (Label) mainGridPane.lookup("#" + CellPositionInSheet.parseColumn(cellPositionInSheet.getColumn()) + cellPositionInSheet.getRow());
                Label cellInCopiedGrid = new Label();
                cellInCopiedGrid.setId(CellPositionInSheet.parseColumn(cellPositionInSheet.getColumn()) + cellPositionInSheet.getRow() + "-copied");
                cellInCopiedGrid.setText(cellInOriginalGrid.getText());
                cellInCopiedGrid.setPrefWidth(cellInOriginalGrid.getPrefWidth());
                cellInCopiedGrid.setMinWidth(cellInOriginalGrid.getMinWidth());
                cellInCopiedGrid.setMaxWidth(cellInOriginalGrid.getMaxWidth());
                cellInCopiedGrid.setPrefHeight(cellInOriginalGrid.getPrefHeight());
                cellInCopiedGrid.setMinHeight(cellInOriginalGrid.getMinHeight());
                cellInCopiedGrid.setMaxHeight(cellInOriginalGrid.getMaxHeight());
                cellInCopiedGrid.setBorder(cellInOriginalGrid.getBorder());

                cellInCopiedGrid.setBackground(cellInOriginalGrid.getBackground());
                cellInCopiedGrid.setTextFill(cellInOriginalGrid.getTextFill());
                cellInCopiedGrid.setAlignment(cellInOriginalGrid.getAlignment());

                gridPane.add(cellInCopiedGrid, col + 1, row + 1);  // Offset by 1 to leave space for headers
            }
        }

        return gridPane;
    }

    public void showFilteredSheet(SheetDto sheetDto) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Show filtered sheet");

        GridPane gridPane = getUnStyledGrid(sheetDto);

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
        dialog.showAndWait();
    }

    private GridPane getUnStyledGrid(SheetDto sheetDto) {
        GridPane gridPane = new GridPane();
        gridPane.setPrefWidth(700);

        setGridColumnsHeaders(gridPane, numOfColumns);
        setGridRowsHeaders(gridPane, sheetDto.getNumOfRows());
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
}