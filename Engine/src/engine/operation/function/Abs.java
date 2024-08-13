package engine.operation.function;

import engine.expression.api.Expression;
import engine.expression.impl.UnaryExpression;
import engine.operation.Operation;

public class Abs extends UnaryExpression<Double, Double> {

    public Abs(Expression<Double> expression) {
        super(expression);
    }

    @Override
    protected Double invoke(Double evaluate) {
        return Math.abs(evaluate);
    }

    @Override
    public Operation getOperationSign() {
        return Operation.ABS;
    }
}