package engine.operation.function.systemic;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.entity.range.Range;
import engine.entity.sheet.api.ReadOnlySheet;
import engine.expression.api.Expression;
import engine.expression.impl.SystemExpression;

import java.util.Set;

public class Sum extends SystemExpression implements Systemic {

    public Sum(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate, ReadOnlySheet roSheet,
                                    Set<CellPositionInSheet> influencingCellPositions, Set<String> usingRangesNames) {
        // taking the range
        String evaluateValue = evaluate.getValue().toString();
        Range range = roSheet.getRangeByName(evaluateValue);

        if (range != null) {
            double sum = 0;
            Set<CellPositionInSheet> cellPositions = range.getIncludedPositions();

            for (CellPositionInSheet cellPosition : cellPositions) {
                EffectiveValue currentCellEffectiveValue = roSheet.getCellEffectiveValue(cellPosition);
                if (currentCellEffectiveValue != null) {
                    CellType currentCellType = currentCellEffectiveValue.getCellType();
                    Object currentValue = currentCellEffectiveValue.getValue();

                    if (currentCellType == CellType.NUMERIC && !currentValue.equals(Double.NaN)) {
                        sum += Double.parseDouble(currentValue.toString());
                    }
                }
                influencingCellPositions.add(cellPosition);
            }
            usingRangesNames.add(evaluateValue);
            return new EffectiveValue(CellType.NUMERIC, sum);

        } else {
            usingRangesNames.add(evaluateValue);
            return new EffectiveValue(CellType.NUMERIC, Double.NaN);
        }
    }
}
