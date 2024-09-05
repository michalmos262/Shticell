package engine.operation.function.arithmetical;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.impl.BinaryExpression;
import engine.operation.Operation;

public class Pow extends BinaryExpression implements Arithmetical {

    public Pow(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public Operation getOperationSign() {
        return Operation.POW;
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2) {
        try {
            double result = Math.pow(evaluate1.extractValueWithExpectation(Double.class),
                    evaluate2.extractValueWithExpectation(Double.class));
            return new EffectiveValue(CellType.NUMERIC, result);
        } catch (Exception e) {
            return new EffectiveValue(CellType.NUMERIC, Double.NaN);
        }
    }
}