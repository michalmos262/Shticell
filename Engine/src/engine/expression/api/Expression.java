package engine.expression.api;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.exception.operation.InvokeOnInvalidArgumentsTypesException;
import engine.operation.Operation;

import java.util.ArrayList;
import java.util.Arrays;

public interface Expression {
    /**
     * invokes the expression and returns the result
     *
     * @return the results of the expression
     */
    EffectiveValue invoke();

    Operation getOperationSign();

    @Override
    String toString();

    default EffectiveValue handleEvaluationsTypesError(Operation operationSign, CellType expectedCellType, EffectiveValue... effectiveValues) {
        for(EffectiveValue effectiveValue : effectiveValues) {
            if (!(effectiveValue.getCellType() == CellType.UNKNOWN || effectiveValue.getCellType() == expectedCellType)) {
                ArrayList<EffectiveValue> arguments = new ArrayList<>(Arrays.asList(effectiveValues));
                throw new InvokeOnInvalidArgumentsTypesException(operationSign, arguments);
            }
        }
        if (expectedCellType == CellType.NUMERIC) {
            return new EffectiveValue(expectedCellType, Double.NaN);
        }
        return new EffectiveValue(expectedCellType, EffectiveValue.STRING_INVALID_VALUE);
    }
}