package engine.entity.cell;

import java.util.Objects;

public class CellPositionInSheet implements Cloneable {
    private int row;
    private int column;

    public CellPositionInSheet(int row, int column) {
        this.row = row;
        this.column = column;
    }

//    public CellPositionInSheet(String position) {
//        this.row = parseRow(position);
//        this.column = parseColumn(position);
//    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        char columnChar = (char) ('A' + column);
        return "" + columnChar + (row + 1);
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

    @Override
    public CellPositionInSheet clone() {
        try {
            CellPositionInSheet clone = (CellPositionInSheet) super.clone();
            clone.row = this.row;
            clone.column = this.column;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
