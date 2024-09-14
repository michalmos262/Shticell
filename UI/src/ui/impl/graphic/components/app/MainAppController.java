package ui.impl.graphic.components.app;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.CellDto;
import engine.entity.dto.SheetDto;
import engine.impl.EngineImpl;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ui.impl.graphic.components.actionline.ActionLineController;
import ui.impl.graphic.components.command.CommandsController;
import ui.impl.graphic.components.file.LoadFileController;
import ui.impl.graphic.components.grid.GridController;
import ui.impl.graphic.components.range.RangesController;

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

    public CellDto cellClicked(String cellPositionId) {
        CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellPositionId);
        CellDto cellDto = engine.getSheet(engine.getCurrentSheetVersion()).getCell(cellPositionInSheet);
        int lastCellVersion = engine.getLastCellVersion(cellPositionInSheet.getRow(), cellPositionInSheet.getColumn());
        String originalValue = cellDto == null ? "" : cellDto.getOriginalValue();

        actionLineComponentController.cellClicked(cellPositionId, originalValue, lastCellVersion);

        return cellDto;
    }

    public void cellIsUpdated(CellPositionInSheet cellPositionInSheet, CellDto cellDto) {
        sheetComponentController.cellUpdated(cellPositionInSheet, cellDto);
        actionLineComponentController.updateCellSucceeded();
    }

    public void selectSheetVersion(int version) {
        SheetDto sheetDto = engine.getSheet(version);

        sheetComponentController.showSheetInVersion(sheetDto, version);
    }

    public void showCellsInRange(String name) {
        sheetComponentController.showCellsInRange(name);
        actionLineComponentController.removeCellClickFocus();
    }

    public void sortedSheet(SheetDto sheetDto) {
        sheetComponentController.showSortedSheet(sheetDto);
    }
}