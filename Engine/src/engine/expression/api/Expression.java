package engine.expression.api;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.EffectiveValue;
import engine.entity.sheet.SheetDto;
import engine.operation.Operation;

import java.util.List;

public interface Expression {
    /**
     * invokes the expression and returns the result
     *
     * @return the results of the expression
     */
    EffectiveValue invoke();
    Operation getOperationSign();
}