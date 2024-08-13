package engine.expression.impl;

import engine.expression.api.Expression;

/**
 * Trinary expression
 * @param <E1> First expression type
 * @param <E2> Second expression type
 * @param <E3> Third expression type
 * @param <R> Return type
 */
public abstract class TrinaryExpression<E1, E2, E3, R> implements Expression<R> {
    private final Expression<E1> expression1;
    private final Expression<E2> expression2;
    private final Expression<E3> expression3;

    public TrinaryExpression(Expression<E1> expression1, Expression<E2> expression2, Expression<E3> expression3) {
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.expression3 = expression3;
    }

    @Override
    public R invoke() {
        return invoke(expression1.invoke(), expression2.invoke(), expression3.invoke());
    }

    @Override
    public String toString() {
        return "{" + getOperationSign() + "," + expression1 + "," + expression2 + "," + expression3 + "}";
    }

    abstract protected R invoke(E1 evaluate1, E2 evaluate2, E3 evaluate3);
}
