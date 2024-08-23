package engine.exception.sheet;

public class SheetMaxNumOfColumnsException extends IndexOutOfBoundsException {
    private final int maxNumOfColumns;
    private final int actualNumOfColumns;

    public SheetMaxNumOfColumnsException(int maxNumOfColumns, int actualNumOfColumns) {
        this.maxNumOfColumns = maxNumOfColumns;
        this.actualNumOfColumns = actualNumOfColumns;
    }

    @Override
    public String getMessage() {
        return "The number of sheet columns must be between 1 and " + maxNumOfColumns + ", but got " + actualNumOfColumns + ".";
    }
}
