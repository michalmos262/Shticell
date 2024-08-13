package engine.operation.function;

import engine.expression.api.Expression;
import engine.expression.impl.BinaryExpression;
import engine.operation.Operation;

public class Minus extends BinaryExpression<Double, Double, Double> {

    public Minus(Expression<Double> expression1, Expression<Double> expression2) {
        super(expression1, expression2);
    }

    @Override
    protected Double invoke(Double evaluate1, Double evaluate2) {
        return evaluate1 - evaluate2;
    }

    @Override
    public Operation getOperationSign() {
        return Operation.MINUS;
    }
}