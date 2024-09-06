package ui.impl.graphic.components.app;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.CellDto;
import engine.entity.dto.SheetDto;
import engine.entity.sheet.impl.SheetDimension;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ui.impl.graphic.components.actionline.ActionLineController;
import ui.impl.graphic.components.file.LoadFileController;
import ui.impl.graphic.components.grid.GridController;
import ui.impl.graphic.model.BusinessLogic;

public class MainAppController {
    @FXML private GridPane loadFileComponent;
    @FXML private LoadFileController loadFileComponentController;
    @FXML private GridPane actionLineComponent;
    @FXML private ActionLineController actionLineComponentController;
    @FXML private BorderPane commandsComponent;
    @FXML private BorderPane rangesComponent;
    @FXML private ScrollPane sheetComponent;
    @FXML private GridController sheetComponentController;

    private boolean isFileSelected;
    private SimpleBooleanProperty isDataLoaded;
    private Stage primaryStage;
    private BusinessLogic businessLogic;
    private SimpleStringProperty currentClickedCellId;
    private SimpleStringProperty currentClickedCellOriginalValue;
    private SimpleIntegerProperty currentClickedCellLastVersion;

    public boolean getIsFileSelected() {
        return isFileSelected;
    }

    public BooleanProperty getIsDataLoaded() {
        return isDataLoaded;
    }

    @FXML
    public void initialize() {
        if (loadFileComponentController != null && sheetComponentController != null && actionLineComponent != null) {
            loadFileComponentController.setMainController(this);
            sheetComponentController.setMainController(this);
            actionLineComponentController.setMainController(this);
        }
        isDataLoaded = new SimpleBooleanProperty(false);
        isFileSelected = false;
        initActionLineLables();
    }

    private void initActionLineLables() {
        currentClickedCellId = new SimpleStringProperty();
        currentClickedCellOriginalValue = new SimpleStringProperty();
        currentClickedCellLastVersion = new SimpleIntegerProperty();
        actionLineComponentController.setLabels(currentClickedCellId, currentClickedCellOriginalValue, currentClickedCellLastVersion);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setBusinessLogic(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void loadFile() {
        isFileSelected = false;
        try {
            businessLogic.loadFile(loadFileComponentController.getAbsolutePath());
            isFileSelected = true;
            isDataLoaded.set(true);
            initActionLineLables();

            SheetDimension sheetDimension = businessLogic.getSheetDimension();
            SheetDto sheetDto = businessLogic.getSheet(businessLogic.getCurrentSheetVersion());

            sheetComponentController.initGrid(sheetDimension, sheetDto);
            //TODO: set ranges

        } catch (Exception e) {
            loadFileComponentController.fileIsNotValid(e.getMessage());
        }
    }

    public void cellClicked(String cellPositionId) {
        CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellPositionId);
        CellDto cellDto = businessLogic.getSheet(businessLogic.getCurrentSheetVersion()).getCell(cellPositionInSheet);
        int lastCellValue = businessLogic.getLastCellVersion(cellPositionInSheet);
        String originalValue = cellDto == null ? "" : cellDto.getOriginalValue();

        currentClickedCellId.set(cellPositionId);
        currentClickedCellLastVersion.set(lastCellValue);
        currentClickedCellOriginalValue.set(originalValue);
    }
}