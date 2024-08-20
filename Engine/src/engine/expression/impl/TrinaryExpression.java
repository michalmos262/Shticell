package engine.expression.impl;

import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;

import java.lang.invoke.StringConcatException;

/**
 * Trinary expression
 */
public abstract class TrinaryExpression implements Expression {
    private final Expression expression1;
    private final Expression expression2;
    private final Expression expression3;

    public TrinaryExpression(Expression expression1, Expression expression2, Expression expression3) {
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.expression3 = expression3;
    }

    @Override
    public EffectiveValue invoke() throws Exception {
        return invoke(expression1.invoke(), expression2.invoke(), expression3.invoke());
    }

    @Override
    public String toString() {
        return "{" + getOperationSign() + "," + expression1 + "," + expression2 + "," + expression3 + "}";
    }

    abstract protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2, EffectiveValue evaluate3);
}
