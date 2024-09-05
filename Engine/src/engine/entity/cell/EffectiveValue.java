package engine.entity.cell;

import engine.exception.cell.EffectiveValueCastingException;

import java.io.Serializable;
import java.util.Objects;

public class EffectiveValue implements Cloneable, Serializable {
    public static final String STRING_INVALID_VALUE = "!UNDEFINED!";
    public static final String BOOLEAN_INVALID_VALUE = "UNKNOWN";
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

    public void validateValueIsExactString() {
        // Check if the string is a boolean value
        if ("true".equalsIgnoreCase(value.toString()) || "false".equalsIgnoreCase(value.toString())) {
            throw new IllegalArgumentException("Error: The string is a boolean.");
        }
        // Check if the string is a number
        try {
            Double.parseDouble(value.toString());
            throw new IllegalArgumentException("Error: The string is a number.");
        } catch (NumberFormatException ignored) {
            // Ignored because it's expected when the string is not a number
        }
    }

    public <T> T extractValueWithExpectation(Class<T> type) {
        if (cellType.isAssignableFrom(type) || cellType == CellType.UNKNOWN) {
            String valueStr = value.toString();
            String valueStrTrimmed = valueStr.trim();

            if (value instanceof EffectiveValue) {
                    value = ((EffectiveValue) value).extractValueWithExpectation(type);
            }
            if (type == Double.class && value.toString().equals(value.toString().trim())) {
                return type.cast(Double.parseDouble(value.toString()));
            }

            if (type == Boolean.class &&
                    valueStr.equals(valueStrTrimmed) &&
                    (valueStrTrimmed.equalsIgnoreCase("true") ||
                            valueStrTrimmed.equalsIgnoreCase("false"))) {
                return type.cast(Boolean.parseBoolean(value.toString()));
            }

            if (type == String.class) {
                validateValueIsExactString();
                return type.cast(value);
            }
        }
        throw new EffectiveValueCastingException(value.getClass(), type);
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