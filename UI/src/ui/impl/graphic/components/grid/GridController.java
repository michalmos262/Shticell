package ui.impl.graphic.components.grid;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.CellDto;
import engine.entity.dto.SheetDto;
import engine.entity.sheet.impl.SheetDimension;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import ui.impl.graphic.components.app.MainAppController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GridController {

    @FXML private ScrollPane scrollPane;
    @FXML private GridPane gridPane;

    private MainAppController mainAppController;
    private List<Label> currentlyPaintedCells = new ArrayList<>(); // List to store painted cells

    public void setMainController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    public void initGrid(SheetDimension sheetDimension, SheetDto sheetDto) {
        int numOfRows = sheetDimension.getNumOfRows();
        int numOfColumns = sheetDimension.getNumOfColumns();
        //TODO: delete the "* 10" after creating ranges
        int rowHeight = sheetDimension.getRowHeight() * 10;
        int columnWidth = sheetDimension.getColumnWidth() * 10;

         // Clear the existing content in the gridContainer
        gridPane.getChildren().clear();

        setColumnsHeaders(numOfColumns, columnWidth);
        setRowsHeaders(numOfRows, rowHeight);
        setCells(sheetDto, numOfRows, numOfColumns, rowHeight, columnWidth);

        // Force a layout pass after adding new nodes
        gridPane.requestLayout();
    }

    private void setColumnsHeaders(int numOfColumns, int columnWidth) {
        // Add the column headers (A, B, C, ...)
        for (int col = 0; col < numOfColumns; col++) {
            Label columnHeader = new Label(String.valueOf((char) ('A' + col)));
            columnHeader.getStyleClass().add("column-header");
            columnHeader.setPrefWidth(columnWidth);
            gridPane.add(columnHeader, col + 1, 0);  // Place the column header in the first row
        }
    }

    private void setRowsHeaders(int numOfRows, int rowHeight) {
        // Add the row headers (1, 2, 3, ...)
        for (int row = 0; row < numOfRows; row++) {
            Label rowHeader = new Label(String.valueOf(row + 1));
            rowHeader.getStyleClass().add("row-header");
            rowHeader.setPrefHeight(rowHeight);
            rowHeader.setPadding(new Insets(rowHeight));
            gridPane.add(rowHeader, 0, row + 1);  // Place the row header in the first column
        }
    }

    private void setCellLabelBinding(Label label, SheetDto sheetDto, CellPositionInSheet cellPositionInSheet) {
        Map<CellPositionInSheet, SimpleStringProperty> cellPosition2displayedValue = mainAppController.getCellPosition2displayedValue();
        SimpleStringProperty strProperty = sheetDto.getCell(cellPositionInSheet) == null
                ? new SimpleStringProperty("")
                : new SimpleStringProperty(sheetDto.getCell(cellPositionInSheet)
                    .getEffectiveValueForDisplay().toString());
        cellPosition2displayedValue.put(cellPositionInSheet, strProperty);
        label.textProperty().bind(cellPosition2displayedValue.get(cellPositionInSheet));
    }

    private void setCells(SheetDto sheetDto, int numOfRows, int numOfColumns, int rowHeight, int columnWidth) {
        // Populate the GridPane with Labels in the main grid area
        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfColumns; col++) {
                CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(row+1, col+1);
                Label label = new Label();
                label.getStyleClass().add("cell");  // Add the default CSS class
                label.setId((char) ('A' + col) + String.valueOf(row + 1));
                setCellLabelBinding(label, sheetDto, cellPositionInSheet);
                label.setPrefHeight(rowHeight);
                label.setPrefWidth(columnWidth);
                label.setPadding(new Insets(rowHeight+2));

                // Attach the click event handler
                label.setOnMouseClicked(this::handleCellClick);

                gridPane.add(label, col + 1, row + 1);  // Offset by 1 to leave space for headers
            }
        }
    }

    @FXML
    private void handleCellClick(MouseEvent event) {
        // Clear the previously painted cells
        clearPaintedCells();

        // Apply the clicked style to the clicked cell
        Label clickedLabel = (Label) event.getSource();
        clickedLabel.getStyleClass().remove("cell");
        clickedLabel.getStyleClass().add("cell-clicked");
        currentlyPaintedCells.add(clickedLabel); // Keep track of the painted cells

        CellDto cellDto = mainAppController.cellClicked(clickedLabel.getId());
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
        List<CellPositionInSheet> influencesCells = cellDto.getInfluences();

        influencesCells.forEach(influencesCell -> {
            influencesCellsLabels.add((Label) gridPane.lookup("#" + influencesCell));
        });

        return influencesCellsLabels;
    }

    private List<Label> getInfluencedByCellsToPaint(CellDto cellDto) {
        List<Label> influencedByCellsLabels = new ArrayList<>();
        List<CellPositionInSheet> influencedByCells = cellDto.getInfluencedBy();

        influencedByCells.forEach(influencedByCell -> {
            influencedByCellsLabels.add((Label) gridPane.lookup("#" + influencedByCell));
        });

        return influencedByCellsLabels;
    }

    // Method to clear the previously painted cells
    private void clearPaintedCells() {
        for (Label cell : currentlyPaintedCells) {
            cell.getStyleClass().removeAll("influenced-cell", "cell-clicked", "influencing-cell");
            cell.getStyleClass().add("cell");
        }
        currentlyPaintedCells.clear(); // Clear the list after unpainting
    }
}