package engine.exception.operation;

public class InvalidOperationName extends IllegalArgumentException {
    private final String name;

    public InvalidOperationName(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Invalid operation name: " + name;
    }
}