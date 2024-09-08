package ui.impl.graphic.model;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.CellDto;
import engine.entity.dto.SheetDto;
import engine.entity.sheet.impl.SheetDimension;
import engine.impl.EngineImpl;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import ui.impl.graphic.components.app.MainAppController;

import java.util.HashMap;
import java.util.Map;

public class BusinessLogic {
    private final MainAppController mainAppController;
    private final Engine engine;
    private SimpleBooleanProperty isDataLoaded;
    private SimpleBooleanProperty isAnyCellClicked;
    private SimpleStringProperty selectedFileAbsolutePath;
    private SimpleStringProperty selectedCellId;
    private SimpleStringProperty selectedCellOriginalValue;
    private SimpleIntegerProperty selectedCellLastVersion;
    private SimpleIntegerProperty currentSheetVersion;
    private Map<CellPositionInSheet, SimpleStringProperty> cellPosition2displayedValue;

    public BusinessLogic(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
        this.engine = new EngineImpl();

        isDataLoaded = new SimpleBooleanProperty(false);
        isAnyCellClicked = new SimpleBooleanProperty(false);
        selectedFileAbsolutePath = new SimpleStringProperty("");
        selectedCellId = new SimpleStringProperty("");
        selectedCellOriginalValue = new SimpleStringProperty("");
        selectedCellLastVersion = new SimpleIntegerProperty();
        currentSheetVersion = new SimpleIntegerProperty();
        cellPosition2displayedValue = new HashMap<>();
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

    public SimpleIntegerProperty currentSheetVersionProperty() {
        return currentSheetVersion;
    }

    public Map<CellPositionInSheet, SimpleStringProperty> getCellPosition2displayedValue() {
        return cellPosition2displayedValue;
    }

    public SimpleBooleanProperty isDataLoadedProperty() {
        return isDataLoaded;
    }

    public void loadFile(String fileName) throws Exception {
        engine.loadFile(fileName);
        // reset properties
        selectedFileAbsolutePath.set(fileName);
        isDataLoaded.set(true);
        isAnyCellClicked.set(false);
        selectedCellId.set("");
        selectedCellOriginalValue.set("");
        selectedCellLastVersion.set(0);
        currentSheetVersion.set(1);
    }

    public CellDto cellClicked(String cellPositionId) {
        CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellPositionId);
        CellDto cellDto = engine.getSheet(engine.getCurrentSheetVersion()).getCell(cellPositionInSheet);
        int lastCellValue = engine.getLastCellVersion(cellPositionInSheet.getRow(), cellPositionInSheet.getColumn());
        String originalValue = cellDto == null ? "" : cellDto.getOriginalValue();

        selectedCellId.set(cellPositionId);
        selectedCellLastVersion.set(lastCellValue);
        selectedCellOriginalValue.set(originalValue);

        if (!isAnyCellClicked.getValue()) {
            isAnyCellClicked.set(true);
        }

        return cellDto;
    }

    public CellDto updateCell(String cellNewOriginalValue) {
        CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(selectedCellId.getValue());
        CellDto cellDto = engine.updateSheetCell(cellPositionInSheet.getRow(), cellPositionInSheet.getColumn(), cellNewOriginalValue);
        SimpleStringProperty strProperty = cellPosition2displayedValue.get(cellPositionInSheet);
        strProperty.setValue(cellDto.getEffectiveValueForDisplay().toString());
        selectedCellOriginalValue.set(cellNewOriginalValue);
        selectedCellLastVersion.set(engine.getLastCellVersion(cellPositionInSheet.getRow(), cellPositionInSheet.getColumn()));
        // Update the visible affected cells
        cellDto.getInfluences().forEach(influencedPosition -> {
            SimpleStringProperty visibleValue = cellPosition2displayedValue.get(influencedPosition);
            CellDto influencedCell = engine.findCellInSheet(influencedPosition.getRow(), influencedPosition.getColumn(), engine.getCurrentSheetVersion());
            visibleValue.setValue(influencedCell.getEffectiveValueForDisplay().toString());
        });

        currentSheetVersion.set(engine.getCurrentSheetVersion());

        return cellDto;
    }

    public SheetDimension getSheetDimension() {
        return engine.getSheetDimension();
    }

    public int getCurrentSheetVersion() {
        return engine.getCurrentSheetVersion();
    }

    public SheetDto getSheet(int sheetVersion) {
        return engine.getSheet(sheetVersion);
    }
}
