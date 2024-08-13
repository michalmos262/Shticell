package engine.expression.impl;

import engine.expression.api.Expression;

/**
 * Binary expression
 * @param <E1> First expression type
 * @param <E2> Second expression type
 * @param <R> Return type
 */
public abstract class BinaryExpression<E1, E2, R> implements Expression<R> {
    private final Expression<E1> expression1;
    private final Expression<E2> expression2;

    public BinaryExpression(Expression<E1> expression1, Expression<E2> expression2) {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    public R invoke() {
        return invoke(expression1.invoke(), expression2.invoke());
    }

    @Override
    public String toString() {
        return "{" + getOperationSign() + "," + expression1 + "," + expression2 + "}";
    }

    abstract protected R invoke(E1 evaluate1, E2 evaluate2);
}