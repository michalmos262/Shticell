package engine.exception.range;

public class CannotDeleteUsedRangeException extends IllegalStateException {
    private final String rangeName;

    public CannotDeleteUsedRangeException(String rangeName) {
        this.rangeName = rangeName;
    }

    @Override
    public String getMessage() {
        return "Cannot delete range " + rangeName + " because it's being used.";
    }
}
