package engine.expression.impl;

import engine.entity.cell.Cell;
import engine.expression.api.Expression;
import engine.operation.Operation;

public class CellExpression implements Expression<Cell> {

    private final Cell cell;

    public CellExpression(Cell cell) {
        this.cell = cell;
    }

    @Override
    public Cell invoke() {
        return cell;
    }

    @Override
    public Operation getOperationSign() {
        return null;
    }

    @Override
    public String toString() {
        return cell.getEffectiveValue();
    }
}