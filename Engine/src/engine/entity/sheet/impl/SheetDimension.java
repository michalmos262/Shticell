package engine.entity.sheet.impl;

import engine.exception.sheet.SheetMaxNumOfColumnsException;
import engine.exception.sheet.SheetMaxNumOfRowsException;

public class SheetDimension {
    public static final int MAX_NUM_OF_ROWS = 50;
    public static final int MAX_NUM_OF_COLUMNS = 20;
    private static int numOfRows;
    private static int numOfColumns;
    private static int rowHeight;
    private static int columnWidth;

    public SheetDimension(int numOfRows, int numOfColumns, int rowHeight, int columnWidth) {
        if (!(numOfRows >= 1 && numOfRows <= MAX_NUM_OF_ROWS)) {
            throw new SheetMaxNumOfRowsException(MAX_NUM_OF_ROWS, numOfRows);
        }
        if (!(numOfColumns >= 1 && numOfColumns <= MAX_NUM_OF_COLUMNS)) {
            throw new SheetMaxNumOfColumnsException(MAX_NUM_OF_COLUMNS, numOfColumns);
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
