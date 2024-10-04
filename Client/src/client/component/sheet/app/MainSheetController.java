package client.component.sheet.app;

import client.component.sheet.actionline.ActionLineController;
import client.component.sheet.range.RangesController;
import dto.CellDto;
import dto.RowDto;
import dto.SheetDto;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import client.component.sheet.command.CommandsController;
import client.component.sheet.grid.GridController;
import serversdk.response.CellPositionInSheet;
import serversdk.response.Range;

import java.util.LinkedList;

public class MainSheetController {
    @FXML private GridPane actionLineComponent;
    @FXML private ActionLineController actionLineComponentController;
    @FXML private BorderPane commandsComponent;
    @FXML private CommandsController commandsComponentController;
    @FXML private BorderPane rangesComponent;
    @FXML private RangesController rangesComponentController;
    @FXML private ScrollPane sheetComponent;
    @FXML private GridController sheetComponentController;

    @FXML
    void initialize() {
        if (sheetComponentController != null && actionLineComponent != null &&
                rangesComponentController != null && commandsComponentController != null) {
            sheetComponentController.setMainController(this);
            actionLineComponentController.setMainController(this);
            rangesComponentController.setMainController(this);
            commandsComponentController.setMainController(this);
        }
    }

    @FXML
    private void backToDashboardButtonListener() {

    }

    public CellDto cellClicked(Label clickedCell) {
        return actionLineComponentController.cellClicked(clickedCell);
    }

    public void cellIsUpdated(String cellPositionInSheet, CellDto cellDto) {
        sheetComponentController.cellUpdated(cellPositionInSheet, cellDto);
        actionLineComponentController.updateCellSucceeded();
    }

    public void changeCellBackground(String cellId, Color cellBackgroundColor) {
        sheetComponentController.changeCellBackground(cellId, cellBackgroundColor);
    }

    public void changeCellTextColor(String cellId, Color cellTextColor) {
        sheetComponentController.changeCellTextColor(cellId, cellTextColor);
    }

    public void changeColumnTextAlignment(String cellId, Pos columnTextAlignment) {
        sheetComponentController.changeColumnTextAlignment(cellId, columnTextAlignment);
    }

    public void changeRowHeight(String cellId, int rowHeight) {
        sheetComponentController.changeRowHeight(cellId, rowHeight);
    }

    public void changeColumnWidth(String cellId, int columnWidth) {
        sheetComponentController.changeColumnWidth(cellId, columnWidth);
    }

    public void updateCellColors(String cellId, Color cellBackgroundColor, Color cellTextColor) {
        sheetComponentController.updateCellColors(cellId, cellBackgroundColor, cellTextColor);
    }

    public void selectSheetVersion(int version) {
        SheetDto sheetDto = engine.getSheet(version);

        sheetComponentController.showSheetInVersion(sheetDto, version);
    }

    public void showCellsInRange(String name) {
        sheetComponentController.showCellsInRange(name);
        actionLineComponentController.removeCellClickFocus();
    }

    public void sheetIsSorted(LinkedList<RowDto> sortedRows, Range rangeToSort) {
        sheetComponentController.showSortedSheet(sortedRows, rangeToSort);
    }

    public void sheetIsFiltered(LinkedList<RowDto> filteredRows, Range rangeToFilter) {
        sheetComponentController.showFilteredSheet(filteredRows, rangeToFilter);
    }

    public void removeCellsPaints() {
        sheetComponentController.removeCellsPaints();
    }

    public void showDynamicAnalysis(String cellId) {
        sheetComponentController.showDynamicAnalysis(cellId);
    }
}