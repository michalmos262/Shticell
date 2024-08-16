package engine.expression.impl;

import engine.entity.cell.EffectiveValue;
import engine.entity.sheet.SheetDto;
import engine.expression.api.Expression;

/**
 * Unary expression
 */
public abstract class UnaryExpression implements Expression {
    private final Expression expression;

    public UnaryExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public EffectiveValue invoke(SheetDto sheetDto) {
        return invoke(sheetDto, expression.invoke(sheetDto));
    }

    @Override
    public String toString() {
        return "{" + getOperationSign() + "," + expression + "}";
    }

    abstract protected EffectiveValue invoke(SheetDto sheetDto, EffectiveValue evaluate);
}