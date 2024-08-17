package engine.operation.function;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.entity.sheet.SheetDto;
import engine.expression.api.Expression;
import engine.expression.impl.BinaryExpression;
import engine.operation.Operation;

public class Concat extends BinaryExpression {

    public Concat(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public Operation getOperationSign() {
        return Operation.CONCAT;
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2) {
        String left = evaluate1.toString();
        String right = evaluate2.toString();
        String result = left.concat(right);
        return new EffectiveValue(CellType.STRING, result);
    }
}