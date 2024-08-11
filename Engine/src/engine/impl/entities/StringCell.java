package engine.impl.entities;

public class StringCell extends Cell {
    protected StringCell(String originalValue) {
        super(originalValue);
    }

    @Override
    protected void setEffectiveValueByOriginalValue() {
        this.effectiveValue = originalValue;
    }

    @Override
    protected String parseOriginalValue() {
        return originalValue;
    }
}
