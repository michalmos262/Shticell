package engine.operation.function.systemic;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.EffectiveValue;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.CellDto;
import engine.entity.dto.SheetDto;
import engine.expression.api.Expression;
import engine.expression.impl.SystemExpression;

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