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
            EffectiveValue evaluateCloned = new EffectiveValue(CellType.NUMERIC, evaluate.getValue());
            double result = Math.abs(evaluateCloned.extractValueWithExpectation(Double.class));
            return new EffectiveValue(CellType.NUMERIC, result);
        } catch (Exception e) {
            return handleEvaluationsTypesError(getOperationSign(), CellType.NUMERIC, evaluate);
        }
    }

    @Override
    public Operation getOperationSign() {
        return Operation.ABS;
    }
}