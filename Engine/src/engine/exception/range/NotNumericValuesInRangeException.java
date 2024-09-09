package engine.exception.range;

import engine.operation.Operation;

public class NotNumericValuesInRangeException extends IllegalArgumentException {
    private final Operation operationName;
    private final String rangeName;

    public NotNumericValuesInRangeException(Operation operationName, String rangeName) {
        this.operationName = operationName;
        this.rangeName = rangeName;
    }

    @Override
    public String getMessage() {
        return "Cannot invoke operation " + operationName + " on range " + rangeName + " because it doesn't include numeric values.";
    }
}