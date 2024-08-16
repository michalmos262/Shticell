package engine.expression.api;

import engine.entity.cell.EffectiveValue;
import engine.entity.sheet.SheetDto;
import engine.operation.Operation;

public interface Expression {
    /**
     * invokes the expression and returns the result
     *
     * @return the results of the expression
     */
    EffectiveValue invoke(SheetDto sheetDto);
    Operation getOperationSign();
}