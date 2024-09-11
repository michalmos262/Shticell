package ui.impl.graphic.components.grid;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.CellDto;
import engine.entity.dto.SheetDto;
import engine.entity.sheet.SheetDimension;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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

    @FXML
    private void initialize() {
        modelUi = new GridModelUI();
    }

    public void setMainController(MainAppController mainAppController, Engine engine) {
        this.mainAppController = mainAppController;
        this.engine = engine;
    }

    public void initMainGrid(SheetDimension sheetDimension, SheetDto sheetDto) {
        int numOfRows = sheetDimension.getNumOfRows();
        int numOfColumns = sheetDimension.getNumOfColumns();
        int rowHeight = sheetDimension.getRowHeight();
        int columnWidth = sheetDimension.getColumnWidth();

         // Clear the existing content in the gridContainer
        gridPane.getChildren().clear();

        setMainGridColumnsHeaders(gridPane, numOfColumns, columnWidth);
        setMainGridRowsHeaders(gridPane, numOfRows, rowHeight);
        setMainGridCells(sheetDto, sheetDimension);
    }

    private void setMainGridColumnsHeaders(GridPane gridPane, int numOfColumns, int columnWidth) {
        // Add the column headers (A, B, C, ...)
        for (int col = 0; col < numOfColumns; col++) {
            Label columnHeader = new Label(String.valueOf((char) ('A' + col)));
            columnHeader.getStyleClass().add("column-header");
            columnHeader.setPrefWidth(columnWidth);
            gridPane.add(columnHeader, col + 1, 0);  // Place the column header in the first row
        }
    }

    private void setMainGridRowsHeaders(GridPane gridPane, int numOfRows, int rowHeight) {
        // Add the row headers (1, 2, 3, ...)
        for (int row = 0; row < numOfRows; row++) {
            Label rowHeader = new Label(String.valueOf(row + 1));
            rowHeader.getStyleClass().add("row-header");
            rowHeader.setPrefHeight(rowHeight);
            rowHeader.setPadding(new Insets(rowHeight));
            gridPane.add(rowHeader, 0, row + 1);  // Place the row header in the first column
        }
    }

    private void setMainGridCells(SheetDto sheetDto, SheetDimension sheetDimension) {
        int numOfRows = sheetDimension.getNumOfRows();
        int numOfColumns = sheetDimension.getNumOfColumns();
        int rowHeight = sheetDimension.getRowHeight();
        int columnWidth = sheetDimension.getColumnWidth();

        // Populate the GridPane with Labels in the main grid area
        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfColumns; col++) {
                CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(row+1, col+1);
                Label label = new Label();
                label.getStyleClass().add("cell");
                label.setId((char) ('A' + col) + String.valueOf(row + 1));
                modelUi.setCellLabelBinding(label, sheetDto, cellPositionInSheet);
                label.setPrefHeight(rowHeight);
                label.setPrefWidth(columnWidth);

                // Attach the click event handler
                label.setOnMouseClicked(this::handleCellClick);

                gridPane.add(label, col + 1, row + 1);  // Offset by 1 to leave space for headers
            }
        }
    }

    @FXML
    private void handleCellClick(MouseEvent event) {
        clickedLabel = (Label) event.getSource();
        CellDto cellDto = mainAppController.cellClicked(clickedLabel.getId());
        setClickedCellColors(cellDto);
    }

    private void setClickedCellColors(CellDto cellDto) {
        // Clear the previously painted cells
        clearPaintedCells();

        clickedLabel.getStyleClass().remove("cell");
        clickedLabel.getStyleClass().add("cell-clicked");
        currentlyPaintedCells.add(clickedLabel); // Keep track of the painted cells

        if (cellDto != null) {
            // Paint some influences and influenced by cells based on the clicked cell
            List<Label> influencesCellsLabels = getInfluencesCellsToPaint(cellDto);
            List<Label> influencedByCellsLabels = getInfluencedByCellsToPaint(cellDto);
            for (Label cellLabel : influencesCellsLabels) {
                cellLabel.getStyleClass().remove("cell");
                cellLabel.getStyleClass().add("influenced-cell");
                currentlyPaintedCells.add(cellLabel); // Keep track of the painted cells
            }
            for (Label cellLabel : influencedByCellsLabels) {
                cellLabel.getStyleClass().remove("cell");
                cellLabel.getStyleClass().add("influencing-cell");
                currentlyPaintedCells.add(cellLabel); // Keep track of the painted cells
            }
        }
    }

    private List<Label> getInfluencesCellsToPaint(CellDto cellDto) {
        List<Label> influencesCellsLabels = new ArrayList<>();
        Set<CellPositionInSheet> influencesCells = cellDto.getInfluences();

        influencesCells.forEach(influencesCell ->
                influencesCellsLabels.add((Label) gridPane.lookup("#" + influencesCell))
        );

        return influencesCellsLabels;
    }

    private List<Label> getInfluencedByCellsToPaint(CellDto cellDto) {
        List<Label> influencedByCellsLabels = new ArrayList<>();
        Set<CellPositionInSheet> influencedByCells = cellDto.getInfluencedBy();

        influencedByCells.forEach(influencedByCell ->
                influencedByCellsLabels.add((Label) gridPane.lookup("#" + influencedByCell))
        );

        return influencedByCellsLabels;
    }

    // Method to clear the previously painted cells
    private void clearPaintedCells() {
        for (Label cell : currentlyPaintedCells) {
            cell.getStyleClass().removeAll("influenced-cell", "cell-clicked", "influencing-cell");
            cell.getStyleClass().add("cell");
        }
        currentlyPaintedCells.clear(); // Clear the list after un-painting
    }

    public void cellUpdated(CellPositionInSheet cellPositionInSheet, CellDto cellDto) {
        SimpleStringProperty strProperty = modelUi.getCellPosition2displayedValue().get(cellPositionInSheet);
        strProperty.setValue(cellDto.getEffectiveValueForDisplay().toString());
        // Update the visible affected cells
        cellDto.getInfluences().forEach(influencedPosition -> {
            SimpleStringProperty visibleValue = modelUi.getCellPosition2displayedValue().get(influencedPosition);
            CellDto influencedCell = engine.findCellInSheet(influencedPosition.getRow(), influencedPosition.getColumn(), engine.getCurrentSheetVersion());
            visibleValue.setValue(influencedCell.getEffectiveValueForDisplay().toString());
        });
        setClickedCellColors(cellDto);
    }

    public void setGridOnVersionCells(GridPane gridPane, SheetDto sheetDto, SheetDimension sheetDimension) {
        int numOfRows = sheetDimension.getNumOfRows();
        int numOfColumns = sheetDimension.getNumOfColumns();
        int rowHeight = sheetDimension.getRowHeight();
        int columnWidth = sheetDimension.getColumnWidth();

        // Populate the GridPane with Labels in the main grid area
        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfColumns; col++) {
                CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(row+1, col+1);
                Label label = new Label();
                label.getStyleClass().add("cell");
                String cellDisplayedValue = sheetDto.getCell(cellPositionInSheet) == null
                        ? ""
                        : sheetDto.getCell(cellPositionInSheet).getEffectiveValueForDisplay().toString();
                label.setText(cellDisplayedValue);
                label.setPrefWidth(columnWidth);
                label.setPrefHeight(rowHeight);
                label.setMaxWidth(Double.MAX_VALUE);
                label.setMaxHeight(Double.MAX_VALUE);

                gridPane.add(label, col + 1, row + 1);  // Offset by 1 to leave space for headers
            }
        }
    }

    public void showSheetInVersion(SheetDimension sheetDimension, SheetDto sheetDto, int version) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Show sheet on specific version");
        dialog.setHeaderText("Sheet version " + version);

        int numOfRows = sheetDimension.getNumOfRows();
        int numOfColumns = sheetDimension.getNumOfColumns();
        int rowHeight = sheetDimension.getRowHeight();
        int columnWidth = sheetDimension.getColumnWidth();

        GridPane gridPane = new GridPane();
        gridPane.setPrefWidth(700);

        setMainGridColumnsHeaders(gridPane, numOfColumns, columnWidth);
        setMainGridRowsHeaders(gridPane, numOfRows, rowHeight);
        setGridOnVersionCells(gridPane, sheetDto, sheetDimension);

        for (Node node : gridPane.getChildren()) {
            if (node instanceof Label label) {
                label.setStyle("-fx-border-color: black; -fx-alignment: top-center;");
            }
        }

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
        dialog.showAndWait();
    }
}