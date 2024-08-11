package engine.impl.entities;

public class BoolCell extends Cell {
    protected BoolCell(String originalValue) {
        super(originalValue);
    }

    @Override
    protected void setEffectiveValueByOriginalValue() {
        this.effectiveValue = this.originalValue.toLowerCase();
    }

    @Override
    protected Boolean parseOriginalValue() {
        return this.originalValue.equalsIgnoreCase("true");
    }
}