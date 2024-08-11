package engine.impl.entities;

import java.math.BigDecimal;

public class NumberCell extends Cell {
    protected NumberCell(String originalValue) {
        super(originalValue);
    }

    @Override
    protected void setEffectiveValueByOriginalValue() {
        this.effectiveValue = originalValue;
    }

    @Override
    public Number parseOriginalValue() {
        return new BigDecimal(originalValue);
    }
}