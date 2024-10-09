package engine.entity.cell;

import java.io.Serializable;
import java.util.*;

public class Cell implements Cloneable, Serializable {
    private String originalValue;
    private EffectiveValue effectiveValue;
    private final Set<CellPositionInSheet> influencedBy;
    private final Set<CellPositionInSheet> influences;
    private final Set<String> rangeNamesUsed;
    private int lastUpdatedInVersion;
    private String updatedByName;

    public Cell(String originalValue, EffectiveValue effectiveValue, int lastUpdatedInVersion, String updatedByName) {
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.influencedBy = new LinkedHashSet<>();
        this.influences = new LinkedHashSet<>();
        this.lastUpdatedInVersion = lastUpdatedInVersion;
        this.rangeNamesUsed = new HashSet<>();
        this.updatedByName = updatedByName;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    public Set<CellPositionInSheet> getInfluencedBy() {
        return influencedBy;
    }

    public Set<CellPositionInSheet> getInfluences() {
        return influences;
    }

    public Set<String> getRangeNamesUsed() {
        return rangeNamesUsed;
    }

    public int getLastUpdatedInVersion() {
        return lastUpdatedInVersion;
    }

    public String getUpdatedByName() {
        return updatedByName;
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

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    public void addInfluence(CellPositionInSheet influencedCellPosition) {
        influences.add(influencedCellPosition);
    }

    public void removeInfluence(CellPositionInSheet influencedCellPosition) {
        influences.remove(influencedCellPosition);
    }

    public void addInfluencedBy(CellPositionInSheet influencingCellPosition) {
        influencedBy.add(influencingCellPosition);
    }

    public void removeInfluencedBy(CellPositionInSheet influencingCellPosition) {
        influencedBy.remove(influencingCellPosition);
    }

    public void addRangeNameUsed(String rangeName) {
        rangeNamesUsed.add(rangeName);
    }

    public void removeRangeNameUsed(String rangeName) {
        rangeNamesUsed.remove(rangeName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return lastUpdatedInVersion == cell.lastUpdatedInVersion && Objects.equals(originalValue, cell.originalValue)
                && Objects.equals(updatedByName, cell.updatedByName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalValue, lastUpdatedInVersion, updatedByName);
    }

    @Override
    public Cell clone() {
        try {
            Cell cloned = (Cell) super.clone();
            cloned.originalValue = originalValue;
            cloned.effectiveValue = effectiveValue;
            cloned.lastUpdatedInVersion = lastUpdatedInVersion;
            cloned.updatedByName = updatedByName;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}