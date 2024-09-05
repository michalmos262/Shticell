package engine.operation.function.booleanic;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.impl.UnaryExpression;
import engine.operation.Operation;

public class Not extends UnaryExpression implements Booleanic {

    public Not(Expression expression) {
        super(expression);
    }

    @Override
    public Operation getOperationSign() {
        return Operation.NOT;
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate) {
        try {
            boolean boolValue = evaluate.extractValueWithExpectation(Boolean.class);
            return new EffectiveValue(CellType.BOOLEAN, !boolValue);

        } catch (Exception e) {
            return new EffectiveValue(CellType.BOOLEAN, EffectiveValue.BOOLEAN_INVALID_VALUE);
        }
    }
}
