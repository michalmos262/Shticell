package ui.impl.graphic.components.app;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.dto.CellDto;
import engine.entity.dto.RowDto;
import engine.entity.dto.SheetDto;
import engine.entity.range.Range;
import engine.impl.EngineImpl;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ui.impl.graphic.components.actionline.ActionLineController;
import ui.impl.graphic.components.command.CommandsController;
import ui.impl.graphic.components.loadfile.LoadFileController;
import ui.impl.graphic.components.grid.GridController;
import ui.impl.graphic.components.range.RangesController;

import java.io.IOException;
import java.util.LinkedList;

import static ui.impl.graphic.resources.CommonResourcesPaths.*;

public class MainAppController {
    @FXML private GridPane loadFileComponent;
    @FXML private LoadFileController loadFileComponentController;
    @FXML private GridPane actionLineComponent;
    @FXML private ActionLineController actionLineComponentController;
    @FXML private BorderPane commandsComponent;
    @FXML private CommandsController commandsComponentController;
    @FXML private BorderPane rangesComponent;
    @FXML private RangesController rangesComponentController;
    @FXML private ScrollPane sheetComponent;
    @FXML private GridController sheetComponentController;

    private Stage primaryStage;
    private Engine engine;

    @FXML
    void initialize() {
        this.engine = new EngineImpl();

        if (loadFileComponentController != null && sheetComponentController != null && actionLineComponent != null &&
                rangesComponentController != null && commandsComponentController != null) {
            loadFileComponentController.setMainController(this, engine);
            sheetComponentController.setMainController(this, engine);
            actionLineComponentController.setMainController(this, engine);
            rangesComponentController.setMainController(this, engine);
            commandsComponentController.setMainController(this, engine);
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void fileIsLoading() {
        actionLineComponentController.fileIsLoading(true);
        rangesComponentController.fileIsLoading(true);
        commandsComponentController.fileIsLoading(true);
        sheetComponentController.fileIsLoading(true);
    }

    public void fileFailedLoading() {
        actionLineComponentController.fileIsLoading(false);
        rangesComponentController.fileIsLoading(false);
        commandsComponentController.fileIsLoading(false);
        sheetComponentController.fileIsLoading(false);
    }

    public void fileLoadedSuccessfully() {
        actionLineComponentController.fileLoadedSuccessfully();
        rangesComponentController.fileLoadedSuccessfully();
        commandsComponentController.fileLoadedSuccessfully();

        SheetDto sheetDto = engine.getSheet(engine.getCurrentSheetVersion());
        sheetComponentController.initMainGrid(sheetDto);
    }

    public CellDto cellClicked(Label clickedCell) {
        return actionLineComponentController.cellClicked(clickedCell);
    }

    public void cellIsUpdated(CellPositionInSheet cellPositionInSheet, CellDto cellDto) {
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

    public void changeSystemSkin(String skinOption) throws IOException {
        primaryStage.getScene().getStylesheets().clear();
        sheetComponent.getScene().getStylesheets().clear();

        switch (skinOption) {
            case "Light":
                primaryStage.getScene().getStylesheets().add(getClass().getResource(MAIN_APP_LIGHT_CSS_RESOURCE).toExternalForm());
                break;
            case "Dark":
                primaryStage.getScene().getStylesheets().add(getClass().getResource(MAIN_APP_DARK_CSS_RESOURCE).toExternalForm());
                break;
        }
    }
}