package engine.impl.entities;

import java.util.Objects;

public abstract class Cell<T> {
    protected String originalValue;
    protected String effectiveValue = "";

    public String getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    public String getEffectiveValue() {
        return effectiveValue;
    }

    public void setEffectiveValue(String effectiveValue) {
        this.effectiveValue = effectiveValue;
    }

//    abstract protected T parseOriginalValue();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell<?> cell = (Cell<?>) o;
        return Objects.equals(getOriginalValue(), cell.getOriginalValue()) && Objects.equals(getEffectiveValue(), cell.getEffectiveValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOriginalValue(), getEffectiveValue());
    }
}