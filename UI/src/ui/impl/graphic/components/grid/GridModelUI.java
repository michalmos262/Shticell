package ui.impl.graphic.components.grid;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.dto.SheetDto;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;

import java.util.HashMap;
import java.util.Map;

public class GridModelUI {
    private final Map<CellPositionInSheet, SimpleStringProperty> cellPosition2displayedValue;

    public GridModelUI() {
        cellPosition2displayedValue = new HashMap<>();
    }

    public Map<CellPositionInSheet, SimpleStringProperty> getCellPosition2displayedValue() {
        return cellPosition2displayedValue;
    }

    public void setCellLabelBinding(Label label, SheetDto sheetDto, CellPositionInSheet cellPositionInSheet) {
        SimpleStringProperty strProperty = sheetDto.getCell(cellPositionInSheet) == null
                ? new SimpleStringProperty("")
                : new SimpleStringProperty(sheetDto.getCell(cellPositionInSheet)
                    .getEffectiveValueForDisplay().toString());
        cellPosition2displayedValue.put(cellPositionInSheet, strProperty);
        label.textProperty().bind(cellPosition2displayedValue.get(cellPositionInSheet));
    }
}
