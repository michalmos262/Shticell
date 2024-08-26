package engine.operation.function.arithmetical;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.impl.BinaryExpression;
import engine.operation.Operation;

public class Times extends BinaryExpression implements Arithmetical {

    public Times(Expression numberExpression1, Expression numberExpression2) {
        super(numberExpression1, numberExpression2);
    }

    @Override
    public Operation getOperationSign() {
        return Operation.TIMES;
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2) {
        try {
            EffectiveValue evaluate1Cloned = new EffectiveValue(CellType.NUMERIC, evaluate1.getValue());
            EffectiveValue evaluate2Cloned = new EffectiveValue(CellType.NUMERIC, evaluate2.getValue());
            double result = evaluate1Cloned.extractValueWithExpectation(Double.class) *
                    evaluate2Cloned.extractValueWithExpectation(Double.class);
            return new EffectiveValue(CellType.NUMERIC, result);
        } catch (Exception e) {
            return handleEvaluationsTypesError(getOperationSign(), CellType.NUMERIC, evaluate1, evaluate2);
        }
    }
}
