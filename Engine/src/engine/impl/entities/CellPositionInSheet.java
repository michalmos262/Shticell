package engine.impl.entities;

import java.util.Objects;

public class CellPositionInSheet {
    private char row;
    private char column;

    public CellPositionInSheet(char row, char column) {
        this.row = row;
        this.column = column;
    }

    public int getRowIndex() {
        return row - '1';
    }

    public int getColumnIndex() {
        return column - 'A';
    }

    @Override
    public String toString() {
        return "" + column + row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellPositionInSheet that = (CellPositionInSheet) o;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
