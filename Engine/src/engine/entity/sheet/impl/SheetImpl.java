package engine.entity.sheet.impl;

import engine.entity.cell.*;
import engine.entity.sheet.api.Sheet;
import engine.exception.sheet.CycleDetectedException;

import java.util.*;

public class SheetImpl implements Cloneable, Sheet {
    private Map<CellPositionInSheet, Cell> position2cell;
    private int updatedCellsCount;
    private int version = 1;

    public SheetImpl() {
        position2cell = new LinkedHashMap<>();
        updatedCellsCount = 0;
    }

    @Override
    public int getUpdatedCellsCount() {
        return updatedCellsCount;
    }

    @Override
    public EffectiveValue getCellEffectiveValue(CellPositionInSheet cellPosition) {
        return position2cell.get(cellPosition).getEffectiveValue();
    }

    @Override
    public void setUpdatedCellsCount(int updatedCellsCount) {
        this.updatedCellsCount = updatedCellsCount;
    }

    @Override
    public Map<CellPositionInSheet, Cell> getPosition2cell() {
        return position2cell;
    }

    @Override
    public void updateCell(CellPositionInSheet cellPosition, String originalValue, EffectiveValue effectiveValue) {
        Cell cell = position2cell.get(cellPosition);
        cell.setLastUpdatedInVersion(version);
        cell.setOriginalValue(originalValue);
        cell.setEffectiveValue(effectiveValue);
    }

    @Override
    public void addCellConnection(CellPositionInSheet from, CellPositionInSheet to) {
        Cell influencingCell = position2cell.get(from);
        Cell influencedCell = position2cell.get(to);

        // Temporarily add 'to' as a dependency of 'from'
        influencingCell.addInfluence(to);
        influencedCell.addInfluencedBy(from);

        // Check if there is a cycle starting from x2
        boolean cycleDetected = detectCycle(to, new HashSet<>());

        if (cycleDetected) {
            // Revert the temporary connection
            position2cell.get(from).getInfluences().remove(to);
            position2cell.get(to).getInfluencedBy().remove(from);

            throw new CycleDetectedException(from, to);
        }
    }

    @Override
    public void removeCellConnection(CellPositionInSheet from, CellPositionInSheet to) {
        Cell influencingCell = position2cell.get(from);
        Cell influencedCell = position2cell.get(to);

        if (influencingCell.getInfluences().contains(to)) {
            influencingCell.removeInfluence(to);
            influencedCell.removeInfluencedBy(from);
        }
    }

    private boolean detectCycle(CellPositionInSheet to, Set<CellPositionInSheet> visited) {
        if (visited.contains(to)) {
            return true; // Cycle detected
        }
        visited.add(to);

        for (CellPositionInSheet nextCell : position2cell.get(to).getInfluences()) {
            if (detectCycle(nextCell, visited)) {
                return true;
            }
        }

        visited.remove(to); // Remove from visited for other DFS paths
        return false;
    }

    @Override
    public void createNewCell(CellPositionInSheet cellPosition, String originalValue) {
        Cell newCell = new Cell(originalValue, null, version);
        position2cell.put(cellPosition, newCell);
    }

    @Override
    public Cell getCell(CellPositionInSheet cellPosition) {
        return position2cell.get(cellPosition);
    }

    @Override
    public SheetImpl clone() {
        try {
            SheetImpl cloned = (SheetImpl) super.clone();
            cloned.version = version + 1;
            cloned.updatedCellsCount = 0;
            // Make sure to create a new map for the cloned object
            cloned.position2cell = new HashMap<>();
            position2cell.forEach((k, v) -> {
                CellPositionInSheet cellPosition = k.clone(); // Assuming deep clone
                Cell cell = v.clone(); // Assuming deep clone
                cloned.position2cell.put(cellPosition, cell);
            });
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SheetImpl sheet = (SheetImpl) o;
        return updatedCellsCount == sheet.updatedCellsCount && version == sheet.version && Objects.equals(position2cell, sheet.position2cell);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position2cell, updatedCellsCount, version);
    }
}
