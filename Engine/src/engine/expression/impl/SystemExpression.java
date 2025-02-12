package engine.expression.impl;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.EffectiveValue;
import engine.entity.sheet.api.ReadOnlySheet;
import engine.expression.api.Expression;
import engine.operation.Operation;

import java.util.Set;

public abstract class SystemExpression {
    private final Expression expression;

    public SystemExpression(Expression expression) {
        this.expression = expression;
    }

    public EffectiveValue invoke(ReadOnlySheet roSheet, Set<CellPositionInSheet> influencingCellPositions,
                                 Set<String> usingRangesNames) {
        return invoke(expression.invoke(), roSheet, influencingCellPositions, usingRangesNames);
    }

    public Operation getOperationSign() {
        return Operation.REF;
    }

    @Override
    public String toString() {
        return "{" + getOperationSign() + "," + expression + "}";
    }

    abstract protected EffectiveValue invoke(EffectiveValue evaluate, ReadOnlySheet roSheet,
                                             Set<CellPositionInSheet> influencingCellPositions,
                                             Set<String> usingRangesNames);
}
