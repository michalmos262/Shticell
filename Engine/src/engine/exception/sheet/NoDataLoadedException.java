package engine.exception.sheet;

import java.util.NoSuchElementException;

public class NoDataLoadedException extends NoSuchElementException {
    @Override
    public String getMessage() {
        return "There is no data loaded in the system. Please load a file or a system first.";
    }
}