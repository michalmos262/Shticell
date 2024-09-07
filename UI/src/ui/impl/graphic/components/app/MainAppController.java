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
    private SimpleBooleanProperty isDataLoaded;
    private SimpleBooleanProperty isAnyCellClicked;
    private SimpleStringProperty selectedFileAbsolutePath;
    private SimpleStringProperty selectedCellId;
    private SimpleStringProperty selectedCellOriginalValue;
    private SimpleIntegerProperty selectedCellLastVersion;
    private Map<CellPositionInSheet, SimpleStringProperty> cellPosition2displayedValue;

    @FXML
    public void initialize() {
        cellPosition2displayedValue = new HashMap<>();
        isDataLoaded = new SimpleBooleanProperty(false);
        isAnyCellClicked = new SimpleBooleanProperty(false);
        selectedFileAbsolutePath = new SimpleStringProperty("");
        selectedCellId = new SimpleStringProperty("");
        selectedCellOriginalValue = new SimpleStringProperty("");
        selectedCellLastVersion = new SimpleIntegerProperty();

        if (loadFileComponentController != null && sheetComponentController != null && actionLineComponent != null) {
            loadFileComponentController.setMainController(this);
            sheetComponentController.setMainController(this);
            actionLineComponentController.setMainController(this);
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setBusinessLogic(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }

    public BooleanProperty isAnyCellClickedProperty() {
        return isAnyCellClicked;
    }

    public SimpleStringProperty selectedFileAbsolutePathProperty() {
        return selectedFileAbsolutePath;
    }

    public SimpleStringProperty selectedCellIdProperty() {
        return selectedCellId;
    }

    public SimpleStringProperty selectedCellOriginalValueProperty() {
        return selectedCellOriginalValue;
    }

    public SimpleIntegerProperty selectedCellLastVersionProperty() {
        return selectedCellLastVersion;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Map<CellPositionInSheet, SimpleStringProperty> getCellPosition2displayedValue() {
        return cellPosition2displayedValue;
    }

    public void loadFile() {
        try {
            businessLogic.loadFile(loadFileComponentController.getAbsolutePath());
            selectedFileAbsolutePath.set(loadFileComponentController.getAbsolutePath());
            isDataLoaded.set(true);
            isAnyCellClicked.set(false);
            selectedCellId.set("");
            selectedCellOriginalValue.set("");
            selectedCellLastVersion.set(0);

            SheetDimension sheetDimension = businessLogic.getSheetDimension();
            SheetDto sheetDto = businessLogic.getSheet(businessLogic.getCurrentSheetVersion());

            sheetComponentController.initGrid(sheetDimension, sheetDto);
            //TODO: set ranges

        } catch (Exception e) {
            loadFileComponentController.loadFileFailed(e.getMessage());
        }
    }

    public CellDto cellClicked(String cellPositionId) {
        CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellPositionId);
        CellDto cellDto = businessLogic.getSheet(businessLogic.getCurrentSheetVersion()).getCell(cellPositionInSheet);
        int lastCellValue = businessLogic.getLastCellVersion(cellPositionInSheet);
        String originalValue = cellDto == null ? "" : cellDto.getOriginalValue();

        selectedCellId.set(cellPositionId);
        selectedCellLastVersion.set(lastCellValue);
        selectedCellOriginalValue.set(originalValue);

        if (!isAnyCellClicked.getValue()) {
            isAnyCellClicked.set(true);
        }

        return cellDto;
    }

    public void updateCell(String cellNewOriginalValue) {
        try {
            CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(selectedCellId.getValue());
            CellDto cellDto = businessLogic.updateCell(cellPositionInSheet, cellNewOriginalValue);
            SimpleStringProperty strProperty = cellPosition2displayedValue.get(cellPositionInSheet);
            strProperty.setValue(cellDto.getEffectiveValueForDisplay().toString());
            selectedCellOriginalValue.set(cellNewOriginalValue);
            selectedCellLastVersion.set(businessLogic.getLastCellVersion(cellPositionInSheet));
            // Update the visible affected cells
            cellDto.getInfluences().forEach(influencedPosition -> {
                SimpleStringProperty visibleValue = cellPosition2displayedValue.get(influencedPosition);
                CellDto influencedCell = businessLogic.getCell(influencedPosition.getRow(), influencedPosition.getColumn(), businessLogic.getCurrentSheetVersion());
                visibleValue.setValue(influencedCell.getEffectiveValueForDisplay().toString());
            });
            actionLineComponentController.updateCellSucceeded();
        } catch (Exception e) {
            actionLineComponentController.updateCellFailed(e.getMessage());
        }
    }
}