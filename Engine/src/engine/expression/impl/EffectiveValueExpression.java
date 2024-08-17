package engine.expression.impl;

import engine.entity.cell.EffectiveValue;
import engine.entity.sheet.SheetDto;
import engine.expression.api.Expression;
import engine.operation.Operation;

public class EffectiveValueExpression implements Expression {

    private final EffectiveValue value;

    public EffectiveValueExpression(EffectiveValue value) {
        this.value = value;
    }

    @Override
    public EffectiveValue invoke() {
        return value;
    }

    @Override
    public Operation getOperationSign() {
        return null;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
