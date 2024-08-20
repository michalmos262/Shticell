package engine.operation.function.textual;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.impl.BinaryExpression;
import engine.operation.Operation;

import java.lang.invoke.StringConcatException;

public class Concat extends BinaryExpression {

    public Concat(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public Operation getOperationSign() {
        return Operation.CONCAT;
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2) throws StringConcatException {
        if (evaluate1.getCellType() == CellType.NUMERIC || evaluate2.getCellType() == CellType.NUMERIC) {
            throw new StringConcatException("Cannot concat numeric values");
        }
        String left = evaluate1.toString();
        String right = evaluate2.toString();
        String result = left.concat(right);

        return new EffectiveValue(CellType.STRING, result);
    }
}