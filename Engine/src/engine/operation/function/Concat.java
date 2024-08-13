package engine.operation.function;

import engine.expression.api.Expression;
import engine.expression.impl.BinaryExpression;
import engine.operation.Operation;

public class Concat extends BinaryExpression<String, String, String> {

    public Concat(Expression<String> expression1, Expression<String> expression2) {
        super(expression1, expression2);
    }

    @Override
    protected String invoke(String evaluate1, String evaluate2) {
        return evaluate1.concat(evaluate2);
    }

    @Override
    public Operation getOperationSign() {
        return Operation.CONCAT;
    }
}