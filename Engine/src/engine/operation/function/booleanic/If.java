package engine.operation.function.booleanic;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.impl.TrinaryExpression;
import engine.operation.Operation;

public class If extends TrinaryExpression implements Booleanic {

    public If(Expression expression1, Expression expression2, Expression expression3) {
        super(expression1, expression2, expression3);
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2, EffectiveValue evaluate3) {
        try {
            boolean condition = evaluate1.extractValueWithExpectation(Boolean.class);
            // if the 'then' and 'else' are not from the same type
            if (evaluate2.getCellType() != evaluate3.getCellType()) {
                return new EffectiveValue(CellType.BOOLEAN, EffectiveValue.BOOLEAN_INVALID_VALUE);
            }
            if (condition) {
                return new EffectiveValue(evaluate2.getCellType(), evaluate2.getValue());
            } else {
                return new EffectiveValue(evaluate3.getCellType(), evaluate3.getValue());
            }
        } catch (Exception e) {
            return new EffectiveValue(CellType.BOOLEAN, EffectiveValue.BOOLEAN_INVALID_VALUE);
        }
    }

    @Override
    public Operation getOperationSign() {
        return Operation.IF;
    }
}
