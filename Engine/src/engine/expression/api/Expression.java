package engine.expression.api;

import engine.operation.Operation;

public interface Expression<T> {
    /**
     * invokes the expression and returns the result
     *
     * @return the results of the expression
     */
    T invoke();
    Operation getOperationSign();
}