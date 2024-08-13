package engine.expression.impl;

import engine.expression.api.Expression;
import engine.operation.Operation;

public class StringExpression implements Expression<String> {

    private final String str;

    public StringExpression(String str) {
        this.str = str;
    }

    @Override
    public String invoke() {
        return str;
    }

    @Override
    public Operation getOperationSign() {
        return null;
    }

    @Override
    public String toString() {
        return str;
    }
}