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

    public void updateCell(CellPositionInSheet cellPosition, String newValue) {
        updatedCellsCount++;
        position2cell.put(cellPosition, new Cell(newValue, version));
    }

    public Cell getCell(CellPositionInSheet cellPosition) {
        return position2cell.get(cellPosition);
    }

    @Override
    public Sheet clone() {
        try {
            Sheet cloned = (Sheet) super.clone();
            cloned.version = version;
            cloned.updatedCellsCount = 0;
            position2cell.forEach((k,v) -> cloned.position2cell.put(k.clone(), v.clone()));
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
