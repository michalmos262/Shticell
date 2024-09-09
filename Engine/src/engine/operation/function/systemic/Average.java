package engine.operation.function.systemic;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.entity.range.Range;
import engine.entity.sheet.api.ReadOnlySheet;
import engine.exception.range.NotNumericValuesInRangeException;
import engine.exception.range.RangeNotExistException;
import engine.expression.api.Expression;
import engine.expression.impl.SystemExpression;
import engine.operation.Operation;

import java.util.List;

public class Average extends SystemExpression implements Systemic {

    public Average(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate, ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions) {
        // taking the range
        String evaluateValue = evaluate.getValue().toString();
        Range range = roSheet.getRangeByName(evaluateValue);

        if (range == null) {
            throw new RangeNotExistException(evaluateValue);
        }

        double sum = 0;
        int numbersCount = 0;

        List<CellPositionInSheet> cellPositions = range.getIncludedPositions();
        for (CellPositionInSheet cellPosition : cellPositions) {
            EffectiveValue currentCellEffectiveValue = roSheet.getCellEffectiveValue(cellPosition);
            CellType currentCellType = currentCellEffectiveValue.getCellType();
            Object currentValue = currentCellEffectiveValue.getValue();
            if (currentCellType == CellType.NUMERIC) {
                numbersCount++;
                if (!currentValue.equals(Double.NaN)) {
                    sum += Double.parseDouble(currentValue.toString());
                }
            }
        }

        if (numbersCount == 0) {
            throw new NotNumericValuesInRangeException(Operation.AVERAGE, evaluateValue);
        }

        return new EffectiveValue(CellType.NUMERIC, sum / numbersCount);
    }
}
