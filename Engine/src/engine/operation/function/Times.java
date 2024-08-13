package engine.operation.function;

import engine.expression.api.Expression;
import engine.expression.impl.BinaryExpression;
import engine.operation.Operation;

public class Times extends BinaryExpression<Double, Double, Double> {

    public Times(Expression<Double> numberExpression1, Expression<Double> numberExpression2) {
        super(numberExpression1, numberExpression2);
    }

    @Override
    protected Double invoke(Double evaluate1, Double evaluate2) {
        return evaluate1 * evaluate2;
    }

    @Override
    public Operation getOperationSign() {
        return Operation.TIMES;
    }
}
