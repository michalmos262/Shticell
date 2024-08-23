package engine.expression.impl;

import engine.entity.cell.EffectiveValue;
import engine.exception.operation.InvokeOnInvalidValueException;
import engine.expression.api.Expression;
import engine.operation.Operation;

public class EffectiveValueExpression implements Expression {

    private final EffectiveValue value;

    public EffectiveValueExpression(EffectiveValue value) {
        if (value.getValue() == EffectiveValue.NUMBER_INVALID_VALUE
                || value.getValue() == EffectiveValue.STRING_INVALID_VALUE) {
            throw new InvokeOnInvalidValueException(value.getValue().toString());
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
