package serversdk.response;

import java.io.Serializable;

public class CellPositionInSheet implements Cloneable, Serializable {
    private final int row;
    private final int column;
    private final String positionInSheet;

    public CellPositionInSheet(int row, int column) {
        this.row = row;
        this.column = column;
        this.positionInSheet = this.toString();
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public static String parseColumn(int column) {
        StringBuilder result = new StringBuilder();

        while (column > 0) {
            column--; // Adjust for zero-based indexing
            int remainder = column % 26;
            char letter = (char) (remainder + 'A');
            result.insert(0, letter); // Prepend the letter
            column /= 26;
        }

        return result.toString();
    }

    @Override
    public String toString() {
        return parseColumn(column) + (row);
    }
}