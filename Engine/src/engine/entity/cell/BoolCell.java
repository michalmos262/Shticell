package engine.entity.cell;

import engine.entity.sheet.Sheet;

public class BoolCell extends Cell {
    public BoolCell(String originalValue) {
        super(originalValue);
    }

    @Override
    public void setEffectiveValueByOriginalValue(Sheet sheet) {
        this.effectiveValue = this.originalValue.toUpperCase();
    }

    @Override
    protected Boolean parseOriginalValue() {
        return this.originalValue.equalsIgnoreCase("true");
    }
}