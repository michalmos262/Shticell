package engine.operation.function.textual;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.exception.operation.InvokeOnInvalidArgumentsTypesException;
import engine.exception.operation.SubNotTextualValueException;
import engine.expression.api.Expression;
import engine.expression.impl.TrinaryExpression;
import engine.operation.Operation;

import java.util.ArrayList;
import java.util.Objects;

public class Sub extends TrinaryExpression implements Textual {

    public Sub(Expression expression1, Expression expression2, Expression expression3) {
        super(expression1, expression2, expression3);
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2, EffectiveValue evaluate3) {
        try {
            if (evaluate1.getCellType() == CellType.NUMERIC || evaluate1.getCellType() == CellType.BOOLEAN) {
                throw new SubNotTextualValueException();
            }
            EffectiveValue evaluate1Cloned = new EffectiveValue(CellType.STRING, evaluate1.getValue());
            EffectiveValue evaluate2Cloned = new EffectiveValue(CellType.NUMERIC, evaluate2.getValue());
            EffectiveValue evaluate3Cloned = new EffectiveValue(CellType.NUMERIC, evaluate3.getValue());

            String str = evaluate1Cloned.extractValueWithExpectation(String.class);
            double beginIndex = evaluate2Cloned.extractValueWithExpectation(Double.class);
            double endIndex = evaluate3Cloned.extractValueWithExpectation(Double.class);
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
        } catch (SubNotTextualValueException e) {
            throw e;
        } catch (Exception e) {
            if ((evaluate1.getCellType() != CellType.UNKNOWN && evaluate1.getCellType() != CellType.STRING) ||
                    (evaluate2.getCellType() != CellType.UNKNOWN && evaluate2.getCellType() != CellType.NUMERIC) ||
                    (evaluate3.getCellType() != CellType.UNKNOWN && evaluate3.getCellType() != CellType.NUMERIC)) {
                ArrayList<EffectiveValue> arguments = new ArrayList<>() {{
                    add(evaluate1);
                    add(evaluate2);
                    add(evaluate2);
                }};

                throw new InvokeOnInvalidArgumentsTypesException(getOperationSign(), arguments);
            }
            return new EffectiveValue(CellType.STRING, EffectiveValue.STRING_INVALID_VALUE);
        }
    }

    @Override
    public Operation getOperationSign() {
        return Operation.SUB;
    }
}