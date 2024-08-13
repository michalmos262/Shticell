package engine.entity.cell;

import engine.entity.sheet.Sheet;

public class StringCell extends Cell {
    public StringCell(String originalValue) {
        super(originalValue);
    }

    @Override
    public void setEffectiveValueByOriginalValue(Sheet sheet) {
        this.effectiveValue = originalValue.trim();
    }

    @Override
    protected String parseOriginalValue() {
        return originalValue;
    }
}