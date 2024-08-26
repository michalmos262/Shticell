package engine.operation.function.textual;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.exception.operation.ConcatNotTextualValuesException;
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
            if (evaluate1.getCellType() == CellType.NUMERIC || evaluate2.getCellType() == CellType.NUMERIC ||
            evaluate1.getCellType() == CellType.BOOLEAN || evaluate2.getCellType() == CellType.BOOLEAN) {
                throw new ConcatNotTextualValuesException();
            }

            EffectiveValue evaluate1Cloned = new EffectiveValue(CellType.STRING, evaluate1.getValue());
            EffectiveValue evaluate2Cloned = new EffectiveValue(CellType.STRING, evaluate2.getValue());

            String left = evaluate1Cloned.extractValueWithExpectation(String.class);
            String right = evaluate2Cloned.extractValueWithExpectation(String.class);
            String result;

            if (Objects.equals(left, EffectiveValue.STRING_INVALID_VALUE) || Objects.equals(right, EffectiveValue.STRING_INVALID_VALUE)) {
                result = EffectiveValue.STRING_INVALID_VALUE;
            } else {
                result = left.concat(right);
            }

            return new EffectiveValue(CellType.STRING, result);
        } catch (ConcatNotTextualValuesException e) {
            throw e;
        } catch (Exception e) {
            return handleEvaluationsTypesError(getOperationSign(), CellType.STRING, evaluate1, evaluate2);
        }
    }
}