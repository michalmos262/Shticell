package engine.impl.entities;

import engine.expressions.api.Expression;

public class ExpCell extends Cell {
    protected ExpCell(String originalValue) {
        super(originalValue);
    }

    @Override
    protected void setEffectiveValueByOriginalValue() {

    }

    @Override
    protected <T> T parseOriginalValue() {
        return null;
    }
}
