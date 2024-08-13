package engine.operation.function;

import engine.expression.api.Expression;
import engine.expression.impl.BinaryExpression;
import engine.operation.Operation;

public class Divide extends BinaryExpression<Double, Double, Double> {

    public Divide(Expression<Double> expression1, Expression<Double> expression2) {
        super(expression1, expression2);
    }

    @Override
    protected Double invoke(Double evaluate1, Double evaluate2) {
        if (evaluate2 == 0) {
            throw new ArithmeticException("Divide by zero");
        }
        return evaluate1 / evaluate2;
    }

    @Override
    public Operation getOperationSign() {
        return Operation.DIVIDE;
    }
}