package engine.operation.function;

import engine.entity.cell.*;
import engine.entity.sheet.SheetDto;
import engine.expression.api.Expression;
import engine.expression.impl.SystemExpression;
import engine.expression.impl.UnaryExpression;
import engine.operation.Operation;

import java.util.List;

public class Ref extends SystemExpression {

    public Ref(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate, SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions) {
        String evaluateValue = evaluate.getValue().toString();
        CellPositionInSheet cellPosition = PositionFactory.createPosition(evaluateValue);
        influencingCellPositions.add(cellPosition);
        CellDto cell = sheetDto.getCellDto(cellPosition);
        return cell.getEffectiveValue();
    }
}