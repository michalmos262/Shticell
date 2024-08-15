package engine.operation.function;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.impl.TrinaryExpression;
import engine.operation.Operation;

public class Sub extends TrinaryExpression {

    public Sub(Expression expression1, Expression expression2, Expression expression3) {
        super(expression1, expression2, expression3);
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2, EffectiveValue evaluate3) {
        String str = evaluate1.toString();
        int beginIndex = evaluate2.extractValueWithExpectation(Integer.class);
        int endIndex = evaluate3.extractValueWithExpectation(Integer.class);
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        if (endIndex > str.length()) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        String result = str.substring(beginIndex, endIndex);
        return new EffectiveValue(CellType.STRING, result);
    }

    @Override
    public Operation getOperationSign() {
        return Operation.SUB;
    }
}
