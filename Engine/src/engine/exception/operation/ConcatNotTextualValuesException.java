package engine.exception.operation;

public class ConcatNotTextualValuesException extends IllegalArgumentException {
    @Override
    public String getMessage() {
        return "Cannot invoke CONCAT on numeric or boolean values.";
    }
}