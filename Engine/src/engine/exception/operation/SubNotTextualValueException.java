package engine.exception.operation;

public class SubNotTextualValueException extends IllegalArgumentException {
    @Override
    public String getMessage() {
        return "Cannot invoke SUB on numeric or boolean values.";
    }
}