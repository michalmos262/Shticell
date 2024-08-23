package engine.exception.cell;

public class CellPositionOutOfSheetBoundsException extends IndexOutOfBoundsException {
    private final int sheetNumOfRows;
    private final String sheetNumOfColumns;

    public CellPositionOutOfSheetBoundsException(int sheetNumOfRows, String sheetNumOfColumns) {
        this.sheetNumOfRows = sheetNumOfRows;
        this.sheetNumOfColumns = sheetNumOfColumns;
    }

    @Override
    public String getMessage() {
        return "Cell position is out of sheet bounds. Row should be between 1 to " + sheetNumOfRows + " and column between A to " + sheetNumOfColumns;
    }
}