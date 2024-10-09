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
    void addCellConnection(CellPositionInSheet from, CellPositionInSheet to, String updatedByName);
    void removeCellConnection(CellPositionInSheet from, CellPositionInSheet to);
    Cell createNewCell(CellPositionInSheet cellPosition, String originalValue, String updatedByName);
    Cell getCell(CellPositionInSheet cellPosition);
    void useRange(String name);
    void unUseRange(String name);
    SheetImpl clone();
}
