package engine.entity.sheet.api;

import engine.entity.cell.Cell;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.EffectiveValue;
import engine.entity.sheet.impl.SheetImpl;

import java.util.Map;

public interface GetterSetterSheet {
    void setUpdatedCellsCount(int updatedCellsCount);
    Map<CellPositionInSheet, Cell> getPosition2cell();
    void updateCell(CellPositionInSheet cellPosition, String originalValue, EffectiveValue effectiveValue);
    void addCellConnection(CellPositionInSheet from, CellPositionInSheet to);
    void removeCellConnection(CellPositionInSheet from, CellPositionInSheet to);
    void createNewCell(CellPositionInSheet cellPosition, String originalValue);
    Cell getCell(CellPositionInSheet cellPosition);

    public SheetImpl clone();
}
