package engine.exception.operation;

import engine.entity.cell.EffectiveValue;
import engine.operation.Operation;

import java.util.ArrayList;

public class InvokeOnInvalidArgumentsTypesException extends IllegalArgumentException {
    private final Operation operationName;
    private final ArrayList<EffectiveValue> arguments;

    public InvokeOnInvalidArgumentsTypesException(Operation operationName, ArrayList<EffectiveValue> arguments) {
        this.operationName = operationName;
        this.arguments = arguments;
    }

    @Override
    public String getMessage() {
        return "Cannot invoke operation " + operationName + " on arguments: " + arguments;
    }
}