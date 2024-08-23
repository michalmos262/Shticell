package engine.operation.function.systemic;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.EffectiveValue;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.CellDto;
import engine.entity.dto.SheetDto;
import engine.entity.sheet.api.ReadOnlySheet;
import engine.exception.operation.InvokeOnInvalidArgumentsTypesException;
import engine.expression.api.Expression;
import engine.expression.impl.SystemExpression;

import java.util.ArrayList;
import java.util.List;

public class Ref extends SystemExpression  implements Systemic {

    public Ref(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate, ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions) {
        try {
            String evaluateValue = evaluate.getValue().toString();
            CellPositionInSheet cellPosition = PositionFactory.createPosition(evaluateValue);
            influencingCellPositions.add(cellPosition);
            return roSheet.getCellEffectiveValue(cellPosition);
        } catch (Exception e) {
            ArrayList<EffectiveValue> arguments = new ArrayList<>() {{
                add(evaluate);
            }};
            throw new InvokeOnInvalidArgumentsTypesException(getOperationSign(), arguments);
        }
    }
}