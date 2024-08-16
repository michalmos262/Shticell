package engine.entity.cell;

import java.util.*;

public class Cell implements Cloneable {
    private String originalValue;
    private EffectiveValue effectiveValue;
    private final List<Cell> influencedBy;
    private final List<Cell> influences;
    private int lastUpdatedInVersion;

    public Cell(String originalValue, EffectiveValue effectiveValue, int lastUpdatedInVersion) {
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        influencedBy = new ArrayList<>();
        influences = new ArrayList<>();
        this.lastUpdatedInVersion = lastUpdatedInVersion;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    public List<Cell> getInfluencedBy() {
        return influencedBy;
    }

    public List<Cell> getInfluences() {
        return influences;
    }

    public int getLastUpdatedInVersion() {
        return lastUpdatedInVersion;
    }

    private void addInfluence(Cell otherCell) {
        influences.add(otherCell);
        otherCell.influencedBy.add(this);
    }

    public void setEffectiveValue(EffectiveValue effectiveValue) {
        this.effectiveValue = effectiveValue;
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    public void setLastUpdatedInVersion(int lastUpdatedInVersion) {
        this.lastUpdatedInVersion = lastUpdatedInVersion;
    }

    public boolean addConnectionTo(Cell otherCell) {
        // Temporarily add x2 as a dependency of x1
        addInfluence(otherCell);

        // Check if there is a cycle starting from x2
        boolean cycleDetected = detectCycle(otherCell, new HashSet<>());

        if (cycleDetected) {
            // Revert the temporary connection
            influences.remove(otherCell);
            otherCell.getInfluencedBy().remove(this);
            //TODO: add throw cycle detected
        }
        return cycleDetected;
    }

    private boolean detectCycle(Cell cell, Set<Cell> visited) {
        if (visited.contains(cell)) {
            return true; // Cycle detected
        }
        visited.add(cell);

        for (Cell nextCell : cell.getInfluences()) {
            if (detectCycle(nextCell, visited)) {
                return true;
            }
        }

        visited.remove(cell); // Remove from visited for other DFS paths
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return lastUpdatedInVersion == cell.lastUpdatedInVersion && Objects.equals(originalValue, cell.originalValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalValue, lastUpdatedInVersion);
    }

    @Override
    public Cell clone() {
        try {
            Cell cloned = (Cell) super.clone();
            cloned.originalValue = originalValue;
            cloned.effectiveValue = effectiveValue;
            influencedBy.forEach((cell) -> cloned.influencedBy.add(cell.clone()));
            influences.forEach((cell) -> cloned.influences.add(cell.clone()));
            cloned.lastUpdatedInVersion = lastUpdatedInVersion;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}