package engine.operation.function.arithmetical;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.exception.operation.InvokeOnInvalidArgumentsTypesException;
import engine.expression.api.Expression;
import engine.expression.impl.UnaryExpression;
import engine.operation.Operation;

import java.util.ArrayList;

public class Abs extends UnaryExpression implements Arithmetical {

    public Abs(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate) {
        try {
            double result = Math.abs(evaluate.extractValueWithExpectation(Double.class));
            return new EffectiveValue(CellType.NUMERIC, result);
        } catch (Exception e) {
            ArrayList<EffectiveValue> arguments = new ArrayList<>() {{
                add(evaluate);
            }};
            throw new InvokeOnInvalidArgumentsTypesException(getOperationSign(), arguments);
        }
    }

    @Override
    public Operation getOperationSign() {
        return Operation.ABS;
    }
}