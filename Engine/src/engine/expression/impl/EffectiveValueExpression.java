package engine.expression.impl;

import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;
import engine.operation.Operation;

public class EffectiveValueExpression implements Expression {

    private final EffectiveValue value;

    public EffectiveValueExpression(EffectiveValue value) {
        if (value.getValue() == EffectiveValue.NAN_VALUE || value.getValue() == EffectiveValue.UNDEFINED_VALUE) {
            throw new IllegalArgumentException("Cannot invoke an expression on an invalid value, value is " + value.getValue());
        }
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
