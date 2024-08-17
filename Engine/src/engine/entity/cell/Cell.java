package engine.entity.cell;

import java.util.*;

public class Cell implements Cloneable {
    private String originalValue;
    private EffectiveValue effectiveValue;
    private final List<CellPositionInSheet> influencedBy;
    private final List<CellPositionInSheet> influences;
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

    public List<CellPositionInSheet> getInfluencedBy() {
        return influencedBy;
    }

    public List<CellPositionInSheet> getInfluences() {
        return influences;
    }

    public int getLastUpdatedInVersion() {
        return lastUpdatedInVersion;
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

    public void addInfluence(CellPositionInSheet influencedCellPosition) {
        if (!influences.contains(influencedCellPosition)) {
            influences.add(influencedCellPosition);
        }
    }

    public void removeInfluence(CellPositionInSheet influencedCellPosition) {
        influences.remove(influencedCellPosition);
    }

    public void addInfluencedBy(CellPositionInSheet influencingCellPosition) {
        if (!influencedBy.contains(influencingCellPosition)) {
            influencedBy.add(influencingCellPosition);
        }
    }

    public void removeInfluencedBy(CellPositionInSheet influencingCellPosition) {
        influencedBy.remove(influencingCellPosition);
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
            cloned.lastUpdatedInVersion = lastUpdatedInVersion;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}