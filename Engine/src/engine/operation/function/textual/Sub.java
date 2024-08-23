package engine.operation.function.textual;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.exception.operation.InvokeOnInvalidArgumentsTypesException;
import engine.expression.api.Expression;
import engine.expression.impl.TrinaryExpression;
import engine.operation.Operation;

import java.util.ArrayList;

public class Sub extends TrinaryExpression implements Textual {

    public Sub(Expression expression1, Expression expression2, Expression expression3) {
        super(expression1, expression2, expression3);
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2, EffectiveValue evaluate3) {
        try {
            String str = evaluate1.toString();
            double beginIndex = evaluate2.extractValueWithExpectation(Double.class);
            double endIndex = evaluate3.extractValueWithExpectation(Double.class);
            double subLen = endIndex - beginIndex;

            if (beginIndex < 0 || endIndex > str.length() || subLen < 0) {
                return new EffectiveValue(CellType.STRING, EffectiveValue.STRING_INVALID_VALUE);
            }
            String result = str.substring((int)beginIndex, (int)endIndex);
            return new EffectiveValue(CellType.STRING, result);
        } catch (Exception e) {
            ArrayList<EffectiveValue> arguments = new ArrayList<>() {{
                add(evaluate1);
                add(evaluate2);
                add(evaluate2);
            }};
            throw new InvokeOnInvalidArgumentsTypesException(getOperationSign(), arguments);
        }
    }

    @Override
    public Operation getOperationSign() {
        return Operation.SUB;
    }
}