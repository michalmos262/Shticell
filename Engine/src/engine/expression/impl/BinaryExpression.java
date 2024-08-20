package engine.expression.impl;

import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;

import java.lang.invoke.StringConcatException;

/**
 * Binary expression
 */
public abstract class BinaryExpression implements Expression {
    protected final Expression expression1;
    protected final Expression expression2;

    public BinaryExpression(Expression expression1, Expression expression2) {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    public EffectiveValue invoke() throws Exception {
        return invoke(expression1.invoke(), expression2.invoke());
    }

    @Override
    public String toString() {
        return "{" + getOperationSign() + "," + expression1 + "," + expression2 + "}";
    }

    abstract protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2) throws StringConcatException;
}