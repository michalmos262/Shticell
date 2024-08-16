package engine.expression.impl;

import engine.entity.cell.EffectiveValue;
import engine.entity.sheet.Sheet;
import engine.entity.sheet.SheetDto;
import engine.expression.api.Expression;

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
    public EffectiveValue invoke(SheetDto sheetDto) {
        return invoke(sheetDto, expression1.invoke(sheetDto), expression2.invoke(sheetDto), expression3.invoke(sheetDto));
    }

    @Override
    public String toString() {
        return "{" + getOperationSign() + "," + expression1 + "," + expression2 + "," + expression3 + "}";
    }

    abstract protected EffectiveValue invoke(SheetDto sheetDto, EffectiveValue evaluate1, EffectiveValue evaluate2, EffectiveValue evaluate3);
}
