package engine.expression.impl;

import engine.expression.api.Expression;

/**
 * Unary expression
 * @param <E> Expression type
 * @param <R> Return type
 */
public abstract class UnaryExpression<E, R> implements Expression<R> {
    private final Expression<E> expression;

    public UnaryExpression(Expression<E> expression) {
        this.expression = expression;
    }

    @Override
    public R invoke() {
        return invoke(expression.invoke());
    }

    @Override
    public String toString() {
        return "{" + getOperationSign() + "," + expression + "}";
    }

    abstract protected R invoke(E evaluate);
}