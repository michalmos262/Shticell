package engine.exception.sheet;

public class SheetNameDoesNotExistException extends NullPointerException {
    private final String name;

    public SheetNameDoesNotExistException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Sheet with name " + name + " does not exist";
    }
}
