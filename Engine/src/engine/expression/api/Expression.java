package engine.expression.api;

import engine.entity.cell.EffectiveValue;
import engine.operation.Operation;

public interface Expression {
    /**
     * invokes the expression and returns the result
     *
     * @return the results of the expression
     */
    EffectiveValue invoke() throws Exception;

    Operation getOperationSign();

    @Override
    String toString();
}