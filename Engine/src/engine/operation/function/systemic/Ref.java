package engine.operation.function.systemic;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.entity.cell.PositionFactory;
import engine.entity.sheet.api.ReadOnlySheet;
import engine.exception.cell.CellPositionOutOfSheetBoundsException;
import engine.expression.api.Expression;
import engine.expression.impl.SystemExpression;

import java.util.Set;

public class Ref extends SystemExpression implements Systemic {

    public Ref(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate, ReadOnlySheet roSheet,
                                    Set<CellPositionInSheet> influencingCellPositions, Set<String> usingRangesNames) {
        // taking the position
        String evaluateValue = evaluate.getValue().toString();
        CellPositionInSheet cellPosition = PositionFactory.createPosition(evaluateValue);

        try {
            EffectiveValue referencedEffectiveValue = roSheet.getCellEffectiveValue(cellPosition);
            influencingCellPositions.add(cellPosition);
            if (referencedEffectiveValue != null) { // cell with value
                return new EffectiveValue(referencedEffectiveValue.getCellType(), referencedEffectiveValue.getValue());
            } else { // empty cell
                return new EffectiveValue(CellType.UNKNOWN, "");
            }
        } catch (CellPositionOutOfSheetBoundsException e) {
            throw e;
        }
        catch (Exception e) {
            influencingCellPositions.add(cellPosition);
            return new EffectiveValue(CellType.UNKNOWN, EffectiveValue.STRING_INVALID_VALUE);
        }
    }
}