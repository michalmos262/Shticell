package engine.operation.function;

import engine.entity.cell.Cell;
import engine.expression.api.Expression;
import engine.expression.impl.UnaryExpression;
import engine.operation.Operation;

public class Ref extends UnaryExpression<Cell, String> {

    public Ref(Expression<Cell> expression) {
        super(expression);
    }

    @Override
    protected String invoke(Cell cell) {
        return cell.getEffectiveValue();
    }

    @Override
    public Operation getOperationSign() {
        return Operation.REF;
    }
}