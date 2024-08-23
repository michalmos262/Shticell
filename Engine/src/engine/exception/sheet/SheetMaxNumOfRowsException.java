package engine.exception.sheet;

public class SheetMaxNumOfRowsException extends IndexOutOfBoundsException {
    private final int maxNumOfRows;
    private final int actualNumOfRows;

    public SheetMaxNumOfRowsException(int maxNumOfRows, int actualNumOfRows) {
        this.maxNumOfRows = maxNumOfRows;
        this.actualNumOfRows = actualNumOfRows;
    }

    @Override
    public String getMessage() {
        return "The number of sheet rows must be between 1 and " + maxNumOfRows + ", but got " + actualNumOfRows + ".";
    }
}
