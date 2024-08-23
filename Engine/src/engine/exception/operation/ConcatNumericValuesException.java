package engine.exception.operation;

public class ConcatNumericValuesException extends IllegalArgumentException {
    @Override
    public String getMessage() {
        return "Cannot concat numeric values.";
    }
}