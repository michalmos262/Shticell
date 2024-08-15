package engine.entity.cell;

import java.util.Objects;

public class EffectiveValue {
    private final CellType cellType;
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
            return type.cast(value);
        }
        throw new ClassCastException("Could not cast value to " + type);
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
        return getCellType() == that.getCellType() && Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCellType(), getValue());
    }
}