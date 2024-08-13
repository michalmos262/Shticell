package engine.entity.cell;

public class StringCell extends Cell {
    public StringCell(String originalValue) {
        super(originalValue);
    }

    @Override
    public void setEffectiveValueByOriginalValue() {
        this.effectiveValue = originalValue.trim();
    }

    @Override
    protected String parseOriginalValue() {
        return originalValue;
    }
}