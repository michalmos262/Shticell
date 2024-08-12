package engine.impl.entity.cell;

import java.util.Objects;

public abstract class Cell implements Cloneable {
    protected String originalValue;
    protected String effectiveValue = "";

    protected Cell(String originalValue) {
        this.originalValue = originalValue;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public String getEffectiveValue() {
        return effectiveValue;
    }

    public abstract void setEffectiveValueByOriginalValue();

    abstract protected <T> T parseOriginalValue();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return Objects.equals(getOriginalValue(), cell.getOriginalValue()) && Objects.equals(getEffectiveValue(), cell.getEffectiveValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOriginalValue(), getEffectiveValue());
    }

    @Override
    public Cell clone() {
        try {
            Cell clone = (Cell) super.clone();
            clone.originalValue = originalValue;
            clone.effectiveValue = effectiveValue;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}