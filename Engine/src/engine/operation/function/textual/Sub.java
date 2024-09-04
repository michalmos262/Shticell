package engine.operation.function.textual;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.impl.TrinaryExpression;
import engine.operation.Operation;

import java.util.Objects;

public class Sub extends TrinaryExpression implements Textual {

    public Sub(Expression expression1, Expression expression2, Expression expression3) {
        super(expression1, expression2, expression3);
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2, EffectiveValue evaluate3) {
        try {
            String str = evaluate1.extractValueWithExpectation(String.class);
            double beginIndex = evaluate2.extractValueWithExpectation(Double.class);
            double endIndex = evaluate3.extractValueWithExpectation(Double.class);
            double subLen = endIndex - beginIndex;
            String result;

            if ((beginIndex < 0 || endIndex > str.length() || subLen < 0) ||
                    Objects.equals(str, EffectiveValue.STRING_INVALID_VALUE) ||
                    str.isEmpty()) {
                result = EffectiveValue.STRING_INVALID_VALUE;
            } else {
                result = str.substring((int)beginIndex, (int)endIndex);
            }

            return new EffectiveValue(CellType.STRING, result);

        } catch (Exception e) {
            return new EffectiveValue(CellType.STRING, EffectiveValue.STRING_INVALID_VALUE);
        }
    }

    @Override
    public Operation getOperationSign() {
        return Operation.SUB;
    }
}