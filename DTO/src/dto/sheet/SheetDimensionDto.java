package dto.sheet;

public class SheetDimensionDto {
    private final int numOfRows;
    private final int numOfColumns;
    private final int rowHeight;
    private final int columnWidth;

    public SheetDimensionDto(int numOfRows, int numOfColumns, int rowHeight, int columnWidth) {
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
