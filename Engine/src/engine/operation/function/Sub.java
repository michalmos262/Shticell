package engine.operation.function;

import engine.expression.api.Expression;
import engine.expression.impl.TrinaryExpression;
import engine.operation.Operation;

public class Sub extends TrinaryExpression<String, Integer, Integer, String> {

    public Sub(Expression<String> expression1, Expression<Integer> expression2, Expression<Integer> expression3) {
        super(expression1, expression2, expression3);
    }

    @Override
    protected String invoke(String str, Integer beginIndex, Integer endIndex) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex.toString());
        }
        if (endIndex > str.length()) {
            throw new StringIndexOutOfBoundsException(endIndex.toString());
        }
        int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        return str.substring(beginIndex, endIndex);
    }

    @Override
    public Operation getOperationSign() {
        return Operation.SUB;
    }
}
