package engine.impl.entity.cell;

public class BoolCell extends Cell {
    public BoolCell(String originalValue) {
        super(originalValue);
    }

    @Override
    public void setEffectiveValueByOriginalValue() {
        this.effectiveValue = this.originalValue.toUpperCase();
    }

    @Override
    protected Boolean parseOriginalValue() {
        return this.originalValue.equalsIgnoreCase("true");
    }
}