package ui.impl.graphic.components.grid;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.SheetDto;
import engine.entity.sheet.impl.SheetDimension;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import ui.impl.graphic.components.app.MainAppController;

import java.util.Map;

public class GridController {

    @FXML private ScrollPane scrollPane;
    @FXML private GridPane gridPane;

    private MainAppController mainAppController;

    public void setMainController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    @FXML
    private void handleCellClick(MouseEvent event) {
        Label clickedLabel = (Label) event.getSource();
        mainAppController.cellClicked(clickedLabel.getId());
    }

    public void initGrid(SheetDimension sheetDimension, SheetDto sheetDto) {
        int numOfRows = sheetDimension.getNumOfRows();
        int numOfColumns = sheetDimension.getNumOfColumns();
        //TODO: delete the "* 10"
        int rowHeight = sheetDimension.getRowHeight() * 10;
        int columnWidth = sheetDimension.getColumnWidth() * 10;

         // Clear the existing content in the gridContainer
        gridPane.getChildren().clear();

        setColumnsHeaders(numOfColumns, rowHeight / 2, columnWidth);
        setRowsHeaders(numOfRows, rowHeight, columnWidth / 2);
        setCells(sheetDto, numOfRows, numOfColumns, rowHeight, columnWidth);

        // Force a layout pass after adding new nodes
        gridPane.requestLayout();
    }

    private void setColumnsHeaders(int numOfColumns, int rowHeight, int columnWidth) {
        // Add the column headers (A, B, C, ...)
        for (int col = 0; col < numOfColumns; col++) {
            Label columnHeader = new Label(String.valueOf((char) ('A' + col)));
            columnHeader.setFont(Font.font("Arial", 14));
            columnHeader.setPrefHeight(rowHeight);
            columnHeader.setPrefWidth(columnWidth);
            columnHeader.setStyle("-fx-border-color: black; -fx-background-color: lightgray; -fx-padding: 10;");
            columnHeader.setAlignment(Pos.CENTER);
            gridPane.add(columnHeader, col + 1, 0);  // Place the column header in the first row
        }
    }

    private void setRowsHeaders(int numOfRows, int rowHeight, int columnWidth) {
        // Add the row headers (1, 2, 3, ...)
        for (int row = 0; row < numOfRows; row++) {
            Label rowHeader = new Label(String.valueOf(row + 1));
            rowHeader.setFont(Font.font("Arial", 14));
            rowHeader.setPrefHeight(rowHeight);
            rowHeader.setPrefWidth(columnWidth);
            rowHeader.setStyle("-fx-border-color: black; -fx-background-color: lightgray; -fx-padding: 10;");
            rowHeader.setAlignment(Pos.CENTER);
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
                label.setId((char) ('A' + col) + String.valueOf(row + 1));
                setCellLabelBinding(label, sheetDto, cellPositionInSheet);
                label.setPrefHeight(rowHeight);
                label.setPrefWidth(columnWidth);
                label.setAlignment(Pos.CENTER);

                // Attach the click event handler
                label.setOnMouseClicked(this::handleCellClick);

                gridPane.add(label, col + 1, row + 1);  // Offset by 1 to leave space for headers
            }
        }
    }
}