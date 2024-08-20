package engine.operation.function.numeric;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.impl.UnaryExpression;
import engine.operation.Operation;

public class Abs extends UnaryExpression {

    public Abs(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate) {
        double result = Math.abs(evaluate.extractValueWithExpectation(Double.class));
        return new EffectiveValue(CellType.NUMERIC, result);
    }

    @Override
    public Operation getOperationSign() {
        return Operation.ABS;
    }
}