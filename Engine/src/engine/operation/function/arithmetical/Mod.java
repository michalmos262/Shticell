package engine.operation.function.arithmetical;

import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.exception.operation.InvokeOnInvalidArgumentsTypesException;
import engine.expression.api.Expression;
import engine.expression.impl.BinaryExpression;
import engine.operation.Operation;

import java.util.ArrayList;

public class Mod extends BinaryExpression implements Arithmetical {

    public Mod(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public Operation getOperationSign() {
        return Operation.MOD;
    }

    @Override
    protected EffectiveValue invoke(EffectiveValue evaluate1, EffectiveValue evaluate2) {
        try {
            double result = evaluate1.extractValueWithExpectation(Double.class) % evaluate2.extractValueWithExpectation(Double.class);
            return new EffectiveValue(CellType.NUMERIC, result);
        } catch (Exception e) {
            ArrayList<EffectiveValue> arguments = new ArrayList<>() {{
                add(evaluate1);
                add(evaluate2);
            }};
            throw new InvokeOnInvalidArgumentsTypesException(getOperationSign(), arguments);
        }
    }
}