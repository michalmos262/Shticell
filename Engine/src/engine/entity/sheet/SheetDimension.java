package engine.entity.sheet;

import engine.exception.sheet.SheetMaxNumOfColumnsException;
import engine.exception.sheet.SheetMaxNumOfRowsException;

import java.io.Serializable;

public class SheetDimension implements Serializable {
    public static final int MAX_NUM_OF_ROWS = 50;
    public static final int MAX_NUM_OF_COLUMNS = 20;
    private final int numOfRows;
    private final int numOfColumns;
    private final int rowHeight;
    private final int columnWidth;

    public SheetDimension(int numOfRows, int numOfColumns, int rowHeight, int columnWidth) {
        if (!(numOfRows >= 1 && numOfRows <= MAX_NUM_OF_ROWS)) {
            throw new SheetMaxNumOfRowsException(MAX_NUM_OF_ROWS, numOfRows);
        }
        if (!(numOfColumns >= 1 && numOfColumns <= MAX_NUM_OF_COLUMNS)) {
            throw new SheetMaxNumOfColumnsException(MAX_NUM_OF_COLUMNS, numOfColumns);
        }
        this.numOfRows = numOfRows;
        this.numOfColumns = numOfColumns;
        this.rowHeight = rowHeight;
        this.columnWidth = columnWidth;
    }

    public int getNumOfRows() {
        return numOfRows;
    }

    public int getNumOfColumns() {
        return numOfColumns;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public int getColumnWidth() {
        return columnWidth;
    }
}