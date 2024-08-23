package engine.exception.operation;

public class InvokeOnInvalidValueException extends IllegalArgumentException {
    private final String actualInvalidValue;

    public InvokeOnInvalidValueException(String actualInvalidValue) {
        this.actualInvalidValue = actualInvalidValue;
    }

    @Override
    public String getMessage() {
        return "Cannot invoke an expression on an invalid value, value is " + actualInvalidValue;
    }
}
