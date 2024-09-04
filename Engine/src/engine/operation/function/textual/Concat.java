package engine.operation.function.textual;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.impl.BinaryExpression;
import engine.operation.Operation;

import java.util.Objects;

public class Concat extends BinaryExpression implements Textual {

    public Concat(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public Operation getOperationSign() {
        return Operation.CONCAT;
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2) {
        try {
            String left = evaluate1.extractValueWithExpectation(String.class);
            String right = evaluate2.extractValueWithExpectation(String.class);
            String result;

            if (Objects.equals(left, EffectiveValue.STRING_INVALID_VALUE) ||
                    Objects.equals(right, EffectiveValue.STRING_INVALID_VALUE) ||
                    left.isEmpty() || right.isEmpty()) {
                result = EffectiveValue.STRING_INVALID_VALUE;
            } else {
                result = left.concat(right);
            }

            return new EffectiveValue(CellType.STRING, result);

        } catch (Exception e) {
            return new EffectiveValue(CellType.STRING, EffectiveValue.STRING_INVALID_VALUE);
        }
    }
}