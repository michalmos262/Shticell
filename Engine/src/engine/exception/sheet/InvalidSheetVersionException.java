package engine.exception.sheet;

public class InvalidSheetVersionException extends IllegalArgumentException {
    private final String userInput;

    public InvalidSheetVersionException(String userInput) {
        this.userInput = userInput;
    }

    @Override
    public String getMessage() {
        return "Invalid sheet version was inserted: " + userInput + ".";
    }
}
