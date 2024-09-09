package engine.entity.sheet.impl;

import engine.entity.cell.*;
import engine.entity.range.Range;
import engine.entity.sheet.SheetManager;
import engine.entity.sheet.api.Sheet;
import engine.exception.cell.CellPositionOutOfSheetBoundsException;
import engine.exception.cell.NotExistsCellException;
import engine.exception.sheet.CycleDetectedException;

import java.util.*;

import static engine.entity.cell.CellPositionInSheet.parseColumn;

public class SheetImpl implements Cloneable, Sheet {
    private SheetManager sheetManager;
    private Map<CellPositionInSheet, Cell> position2cell;
    private int updatedCellsCount;
    private int version = 1;

    public SheetImpl(SheetManager sheetManager) {
        position2cell = new LinkedHashMap<>();
        updatedCellsCount = 0;
        this.sheetManager = sheetManager;
    }

    @Override
    public int getUpdatedCellsCount() {
        return updatedCellsCount;
    }

    @Override
    public EffectiveValue getCellEffectiveValue(CellPositionInSheet cellPosition) {
        sheetManager.validatePositionInSheetBounds(cellPosition);
        if (position2cell.get(cellPosition) == null || position2cell.get(cellPosition).getEffectiveValue() == null) {
            throw new NotExistsCellException(cellPosition);
        }
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
        Cell cell = getCell(cellPosition);
        cell.setLastUpdatedInVersion(version);
        cell.setOriginalValue(originalValue);
        cell.setEffectiveValue(effectiveValue);
    }

    @Override
    public void addCellConnection(CellPositionInSheet from, CellPositionInSheet to) {
        sheetManager.validatePositionInSheetBounds(from);
        sheetManager.validatePositionInSheetBounds(to);

        Cell influencingCell = position2cell.get(from);
        Cell influencedCell = position2cell.get(to);

        if (influencingCell == null) {
            Cell cell = createNewCell(from, "");
            cell.setLastUpdatedInVersion(0);
            influencingCell = position2cell.get(from);
        }

        // Temporarily add 'to' as a dependency of 'from'
        influencingCell.addInfluence(to);
        influencedCell.addInfluencedBy(from);

        // Check if there is a cycle starting from x2
        boolean cycleDetected = detectCycle(to, new HashSet<>());

        if (cycleDetected) {
            // Revert the temporary connection
            influencingCell.getInfluences().remove(to);
            influencedCell.getInfluencedBy().remove(from);

            throw new CycleDetectedException(from, to);
        }
    }

    @Override
    public void removeCellConnection(CellPositionInSheet from, CellPositionInSheet to) {
        sheetManager.validatePositionInSheetBounds(from);
        sheetManager.validatePositionInSheetBounds(to);

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
    public Cell createNewCell(CellPositionInSheet cellPosition, String originalValue) {
        sheetManager.validatePositionInSheetBounds(cellPosition);
        Cell newCell = new Cell(originalValue, null, version);
        position2cell.put(cellPosition, newCell);
        return newCell;
    }

    @Override
    public Cell getCell(CellPositionInSheet cellPosition) {
        sheetManager.validatePositionInSheetBounds(cellPosition);
        return position2cell.get(cellPosition);
    }

    @Override
    public Range getRangeByName(String name) {
        return sheetManager.getRangeByName(name);
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
            cloned.sheetManager = sheetManager;
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
        return getUpdatedCellsCount() == sheet.getUpdatedCellsCount() && version == sheet.version && Objects.equals(sheetManager, sheet.sheetManager) && Objects.equals(getPosition2cell(), sheet.getPosition2cell());
    }

    @Override
    public int hashCode() {
        return Objects.hash(sheetManager, getPosition2cell(), getUpdatedCellsCount(), version);
    }
}
