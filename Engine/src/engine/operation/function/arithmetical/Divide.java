package engine.operation.function.arithmetical;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.impl.BinaryExpression;
import engine.operation.Operation;

public class Divide extends BinaryExpression implements Arithmetical {

    public Divide(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2) {
        try {
            double left = evaluate1.extractValueWithExpectation(Double.class);
            double right = evaluate2.extractValueWithExpectation(Double.class);
            if (right == 0) {
                return new EffectiveValue(CellType.NUMERIC, Double.NaN);
            }
            double result = left / right;
            return new EffectiveValue(CellType.NUMERIC, result);
        } catch (Exception e) {
            return new EffectiveValue(CellType.NUMERIC, Double.NaN);
        }
    }

    @Override
    public Operation getOperationSign() {
        return Operation.DIVIDE;
    }
}