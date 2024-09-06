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

import java.util.HashMap;
import java.util.Map;

public class MainAppController {
    @FXML private GridPane loadFileComponent;
    @FXML private LoadFileController loadFileComponentController;
    @FXML private GridPane actionLineComponent;
    @FXML private ActionLineController actionLineComponentController;
    @FXML private BorderPane commandsComponent;
    @FXML private BorderPane rangesComponent;
    @FXML private ScrollPane sheetComponent;
    @FXML private GridController sheetComponentController;

    private Stage primaryStage;
    private BusinessLogic businessLogic;
    private boolean isFileSelected;
    private SimpleBooleanProperty isDataLoaded;
    private SimpleBooleanProperty isAnyCellClicked;
    private SimpleStringProperty currentClickedCellId;
    private SimpleStringProperty currentClickedCellOriginalValue;
    private SimpleIntegerProperty currentClickedCellLastVersion;
    private Map<CellPositionInSheet, SimpleStringProperty> cellPosition2displayedValue;

    @FXML
    public void initialize() {
        cellPosition2displayedValue = new HashMap<>();
        isDataLoaded = new SimpleBooleanProperty(false);
        isAnyCellClicked = new SimpleBooleanProperty(false);
        isFileSelected = false;

        if (loadFileComponentController != null && sheetComponentController != null && actionLineComponent != null) {
            loadFileComponentController.setMainController(this);
            sheetComponentController.setMainController(this);
            actionLineComponentController.setMainController(this);
        }

        initActionLineLabels();
    }

    private void initActionLineLabels() {
        currentClickedCellId = new SimpleStringProperty();
        currentClickedCellOriginalValue = new SimpleStringProperty();
        currentClickedCellLastVersion = new SimpleIntegerProperty();
        actionLineComponentController.setLabels(currentClickedCellId, currentClickedCellOriginalValue, currentClickedCellLastVersion);
    }

    public boolean getIsFileSelected() {
        return isFileSelected;
    }

    public BooleanProperty getIsAnyCellClicked() {
        return isAnyCellClicked;
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

    public Map<CellPositionInSheet, SimpleStringProperty> getCellPosition2displayedValue() {
        return cellPosition2displayedValue;
    }

    public void loadFile() {
        isFileSelected = false;
        try {
            businessLogic.loadFile(loadFileComponentController.getAbsolutePath());
            isFileSelected = true;
            isDataLoaded.set(true);
            isAnyCellClicked.set(false);
            initActionLineLabels();

            SheetDimension sheetDimension = businessLogic.getSheetDimension();
            SheetDto sheetDto = businessLogic.getSheet(businessLogic.getCurrentSheetVersion());

            sheetComponentController.initGrid(sheetDimension, sheetDto);
            //TODO: set ranges

        } catch (Exception e) {
            loadFileComponentController.loadFileFailed(e.getMessage());
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

        if (!isAnyCellClicked.getValue()) {
            isAnyCellClicked.set(true);
        }
    }

    public void updateCell(String cellNewOriginalValue) {
        try {
            CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(currentClickedCellId.getValue());
            CellDto cellDto = businessLogic.updateCell(cellPositionInSheet, cellNewOriginalValue);
            SimpleStringProperty strProperty = cellPosition2displayedValue.get(cellPositionInSheet);
            strProperty.setValue(cellDto.getEffectiveValueForDisplay().toString());
            currentClickedCellOriginalValue.set(cellNewOriginalValue);
            currentClickedCellLastVersion.set(businessLogic.getLastCellVersion(cellPositionInSheet));
        } catch (Exception e) {
            actionLineComponentController.updateCellFailed(e.getMessage());
        }
    }
}