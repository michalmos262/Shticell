package engine.entity.cell;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;

public class CellPositionInSheet implements Cloneable {
    private int row;
    private int column;
    private final Map.Entry<Integer, Cell> defaultInitVersionByCell;

    public CellPositionInSheet(int row, int column) {
        this.row = row;
        this.column = column;
        defaultInitVersionByCell = new AbstractMap.SimpleEntry<>(1, new Cell(" ", 1));
    }

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
        return getRow() == that.getRow() && getColumn() == that.getColumn() && Objects.equals(defaultInitVersionByCell, that.defaultInitVersionByCell);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getColumn(), defaultInitVersionByCell);
    }

    @Override
    public CellPositionInSheet clone() {
        try {
            CellPositionInSheet cloned = (CellPositionInSheet) super.clone();
            cloned.row = row;
            cloned.column = column;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
