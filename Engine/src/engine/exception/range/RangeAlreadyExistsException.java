package engine.exception.range;

public class RangeAlreadyExistsException extends IllegalStateException {
    private final String rangeName;

    public RangeAlreadyExistsException(String rangeName) {
        this.rangeName = rangeName;
    }

    @Override
    public String getMessage() {
        return "Range with name " + rangeName + " already exists.";
    }
}
