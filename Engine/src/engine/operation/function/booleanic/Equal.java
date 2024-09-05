package engine.operation.function.booleanic;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.impl.BinaryExpression;
import engine.operation.Operation;

public class Equal extends BinaryExpression implements Booleanic {

    public Equal(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public Operation getOperationSign() {
        return Operation.EQUAL;
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2) {
        try {
            boolean result = false;

            if (evaluate1.getCellType() == evaluate2.getCellType() &&
                    evaluate1.getValue().equals(evaluate2.getValue())) {
                result = true;
            }
            return new EffectiveValue(CellType.BOOLEAN, result);

        } catch (Exception e) {
            return new EffectiveValue(CellType.BOOLEAN, EffectiveValue.BOOLEAN_INVALID_VALUE);
        }
    }
}
