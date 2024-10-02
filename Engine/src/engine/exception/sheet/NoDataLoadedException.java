package engine.exception.sheet;

public class NoDataLoadedException extends NullPointerException {
    @Override
    public String getMessage() {
        return "There is no data loaded in the system. Please load a file or a system first.";
    }
}