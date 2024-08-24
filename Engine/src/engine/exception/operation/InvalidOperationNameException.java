package engine.exception.operation;

public class InvalidOperationNameException extends IllegalArgumentException {
    private final String name;

    public InvalidOperationNameException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Invalid operation name: " + name;
    }
}