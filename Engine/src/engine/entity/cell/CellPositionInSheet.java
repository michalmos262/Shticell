package engine.entity.cell;

import java.io.Serializable;
import java.util.Objects;

public class CellPositionInSheet implements Cloneable, Serializable {
    private int row;
    private int column;
    private String positionInSheet;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellPositionInSheet that = (CellPositionInSheet) o;
        return getRow() == that.getRow() && getColumn() == that.getColumn() && Objects.equals(positionInSheet, that.positionInSheet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getColumn(), positionInSheet);
    }

    @Override
    public CellPositionInSheet clone() {
        try {
            CellPositionInSheet cloned = (CellPositionInSheet) super.clone();
            cloned.row = row;
            cloned.column = column;
            cloned.positionInSheet = positionInSheet;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
