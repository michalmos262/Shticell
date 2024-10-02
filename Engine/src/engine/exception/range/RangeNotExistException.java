package engine.exception.range;

public class RangeNotExistException extends NullPointerException {
    private final String rangeName;

    public RangeNotExistException(String rangeName) {
        this.rangeName = rangeName;
    }

    @Override
    public String getMessage() {
        return "Range with name " + rangeName + " not exists.";
    }
}
