package engine.exception.sheet;

public class SheetVersionDoesNotExistException extends NullPointerException {
    private final int version;

    public SheetVersionDoesNotExistException(int version) {
        this.version = version;
    }

    @Override
    public String getMessage() {
        return "Sheet version " + version + " does not exist";
    }
}