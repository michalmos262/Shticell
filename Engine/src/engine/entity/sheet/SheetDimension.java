package engine.entity.sheet;

public class SheetDimension {
    public static final int MAX_NUM_OF_ROWS = 50;
    public static final int MAX_NUM_OF_COLUMNS = 20;
    private static int numOfRows;
    private static int numOfColumns;
    private static int rowHeight;
    private static int columnWidth;

    public SheetDimension(int numOfRows, int numOfColumns, int rowHeight, int columnWidth) {
        if (!(numOfRows >= 1 && numOfRows <= MAX_NUM_OF_ROWS)) {
            throw new IndexOutOfBoundsException("The number of sheet rows must be between 1 and " + MAX_NUM_OF_ROWS + ", but " + numOfRows + " was inserted.");
        }
        if (!(numOfColumns >= 1 && numOfColumns <= MAX_NUM_OF_COLUMNS)) {
            throw new IndexOutOfBoundsException("The number of sheet columns must be between 1 and " + MAX_NUM_OF_COLUMNS + ", but " + numOfColumns + " was inserted.");
        }
        SheetDimension.numOfRows = numOfRows;
        SheetDimension.numOfColumns = numOfColumns;
        SheetDimension.rowHeight = rowHeight;
        SheetDimension.columnWidth = columnWidth;
    }

    public static int getNumOfRows() {
        return numOfRows;
    }

    public static int getNumOfColumns() {
        return numOfColumns;
    }

    public static int getRowHeight() {
        return rowHeight;
    }

    public static int getColumnWidth() {
        return columnWidth;
    }
}
