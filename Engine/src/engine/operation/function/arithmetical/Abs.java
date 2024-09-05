package engine.operation.function.arithmetical;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.impl.UnaryExpression;
import engine.operation.Operation;

public class Abs extends UnaryExpression implements Arithmetical {

    public Abs(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate) {
        try {
            double result = Math.abs(evaluate.extractValueWithExpectation(Double.class));
            return new EffectiveValue(CellType.NUMERIC, result);
        } catch (Exception e) {
            return new EffectiveValue(CellType.NUMERIC, Double.NaN);
        }
    }

    @Override
    public Operation getOperationSign() {
        return Operation.ABS;
    }
}