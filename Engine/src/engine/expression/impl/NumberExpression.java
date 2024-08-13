package engine.expression.impl;

import engine.expression.api.Expression;
import engine.operation.Operation;

public class NumberExpression<T extends Number> implements Expression<T> {

    private final T num;

    public NumberExpression(T num) {
        this.num = num;
    }

    @Override
    public T invoke() {
        return num;
    }

    @Override
    public Operation getOperationSign() {
        return null;
    }

    @Override
    public String toString() {
        return num.toString();
    }
}