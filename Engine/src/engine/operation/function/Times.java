package engine.operation.function;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.impl.BinaryExpression;
import engine.operation.Operation;

public class Times extends BinaryExpression {

    public Times(Expression numberExpression1, Expression numberExpression2) {
        super(numberExpression1, numberExpression2);
    }

    @Override
    public Operation getOperationSign() {
        return Operation.TIMES;
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2) {
        double result = evaluate1.extractValueWithExpectation(Double.class) * evaluate2.extractValueWithExpectation(Double.class);
        return new EffectiveValue(CellType.NUMERIC, result);
    }
}
