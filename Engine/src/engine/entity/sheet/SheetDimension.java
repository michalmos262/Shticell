package engine.entity.sheet;

public class SheetDimension implements Cloneable {
    private int numOfRows;
    private int numOfColumns;
    private int rowHeight;
    private int columnWidth;

    public SheetDimension(int numOfRows, int numOfColumns, int rowHeight, int columnWidth) {
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

    @Override
    public SheetDimension clone() {
        try {
            SheetDimension clone = (SheetDimension) super.clone();
            clone.numOfRows = numOfRows;
            clone.numOfColumns = numOfColumns;
            clone.rowHeight = rowHeight;
            clone.columnWidth = columnWidth;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
