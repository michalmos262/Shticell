package engine.entity.sheet;

import engine.entity.cell.*;

import java.util.*;

public class Sheet implements Cloneable {
    private final Map<CellPositionInSheet, Cell> position2cell;
    private int updatedCellsCount;
    private int version;

    public Sheet() {
        position2cell = new LinkedHashMap<>();
        updatedCellsCount = 0;
    }

    public int getUpdatedCellsCount() {
        return updatedCellsCount;
    }

    public void updateCell(Cell cell, String originalValue, EffectiveValue effectiveValue) {
        updatedCellsCount++;
        cell.setLastUpdatedInVersion(version);
        cell.setOriginalValue(originalValue);
        cell.setEffectiveValue(effectiveValue);
    }

    public Cell getNewDefaultCell() {
        return new Cell(" ", new EffectiveValue(CellType.STRING, " "), version);
    }

    public void createNewCell(CellPositionInSheet cellPosition, String originalValue, EffectiveValue effectiveValue) {
        position2cell.put(cellPosition, new Cell(originalValue, effectiveValue, version));
        updatedCellsCount++;
    }

    public Cell getCell(CellPositionInSheet cellPosition) {
        return position2cell.get(cellPosition);
    }

    @Override
    public Sheet clone() {
        try {
            Sheet cloned = (Sheet) super.clone();
            cloned.version = version + 1;
            cloned.updatedCellsCount = 0;
            position2cell.forEach((k,v) -> cloned.position2cell.put(k.clone(), v.clone()));
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
