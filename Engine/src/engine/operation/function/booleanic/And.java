package engine.operation.function.booleanic;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.impl.BinaryExpression;
import engine.operation.Operation;

public class And extends BinaryExpression implements Booleanic {

    public And(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2) {
        try {
            boolean result = evaluate1.extractValueWithExpectation(Boolean.class) &&
                    evaluate2.extractValueWithExpectation(Boolean.class);
            return new EffectiveValue(CellType.BOOLEAN, result);

        } catch (Exception e) {
            return new EffectiveValue(CellType.BOOLEAN, EffectiveValue.BOOLEAN_INVALID_VALUE);
        }
    }

    @Override
    public Operation getOperationSign() {
        return Operation.AND;
    }
}
