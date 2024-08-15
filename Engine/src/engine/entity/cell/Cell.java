package engine.entity.cell;

import engine.entity.sheet.Sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Cell implements Cloneable {
    protected String originalValue;
    protected String effectiveValue = "";
    protected final List<Cell> dependsOn;
    protected final List<Cell> influencingOn;

    protected Cell(String originalValue) {
        this.originalValue = originalValue;
        dependsOn = new ArrayList<>();
        influencingOn = new ArrayList<>();
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public String getEffectiveValue() {
        return effectiveValue;
    }

    public List<Cell> getDependsOn() {
        return dependsOn;
    }

    public List<Cell> getInfluencingOn() {
        return influencingOn;
    }

    public abstract void setEffectiveValueByOriginalValue(Sheet sheet);

    protected abstract <T> T parseOriginalValue();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return Objects.equals(getOriginalValue(), cell.getOriginalValue()) && Objects.equals(getEffectiveValue(), cell.getEffectiveValue()) && Objects.equals(getDependsOn(), cell.getDependsOn()) && Objects.equals(getInfluencingOn(), cell.getInfluencingOn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOriginalValue(), getEffectiveValue(), getDependsOn(), getInfluencingOn());
    }

    @Override
    public Cell clone() {
        try {
            Cell cloned = (Cell) super.clone();
            cloned.originalValue = originalValue;
            cloned.effectiveValue = effectiveValue;
            dependsOn.forEach((cell) -> cloned.dependsOn.add(cell.clone()));
            influencingOn.forEach((cell) -> cloned.influencingOn.add(cell.clone()));
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}