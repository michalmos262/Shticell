package engine.expression.impl;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.EffectiveValue;
import engine.entity.dto.SheetDto;
import engine.entity.sheet.api.ReadOnlySheet;
import engine.expression.api.Expression;
import engine.operation.Operation;

import java.util.List;

public abstract class SystemExpression {
    private final Expression expression;

    public SystemExpression(Expression expression) {
        this.expression = expression;
    }

    public EffectiveValue invoke(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions) throws Exception {
        return invoke(expression.invoke(), roSheet, influencingCellPositions);
    }

    public Operation getOperationSign() {
        return Operation.REF;
    }

    @Override
    public String toString() {
        return "{" + getOperationSign() + "," + expression + "}";
    }

    abstract protected EffectiveValue invoke(EffectiveValue evaluate, ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions);
}
