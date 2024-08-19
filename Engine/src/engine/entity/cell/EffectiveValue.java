package engine.entity.cell;

import java.util.Objects;

public class EffectiveValue implements Cloneable {
    private CellType cellType;
    private Object value;

    public EffectiveValue(CellType cellType, Object value) {
        this.cellType = cellType;
        this.value = value;
    }

    public CellType getCellType() {
        return cellType;
    }

    public Object getValue() {
        return value;
    }

    public <T> T extractValueWithExpectation(Class<T> type) {
        if (cellType.isAssignableFrom(type)) {
            if (value instanceof EffectiveValue) {
                    value = ((EffectiveValue) value).extractValueWithExpectation(type);
            }
            if (type == Double.class) {
                return type.cast(Double.parseDouble(value.toString()));
            }
            return type.cast(value);
        }
        throw new ClassCastException("Could not cast value type " + value.getClass() + " to " + type);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EffectiveValue that = (EffectiveValue) o;
        return cellType == that.cellType && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellType, value);
    }

    @Override
    public EffectiveValue clone() {
        try {
            EffectiveValue cloned = (EffectiveValue) super.clone();
            cloned.value = value;
            cloned.cellType = cellType;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}