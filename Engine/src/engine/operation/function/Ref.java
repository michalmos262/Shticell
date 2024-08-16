package engine.operation.function;

import engine.entity.cell.*;
import engine.entity.sheet.SheetDto;
import engine.expression.api.Expression;
import engine.expression.impl.UnaryExpression;
import engine.operation.Operation;

public class Ref extends UnaryExpression {

    public Ref(Expression expression) {
        super(expression);
    }

    @Override
    public Operation getOperationSign() {
        return Operation.REF;
    }

    @Override
    protected EffectiveValue invoke(SheetDto sheetDto, EffectiveValue evaluate) {
        String evaluateValue = evaluate.getValue().toString();
        CellPositionInSheet cellPosition = PositionFactory.createPosition(evaluateValue);
        CellDto cell = sheetDto.getCellDto(cellPosition);
        return cell.getEffectiveValue();
    }
}