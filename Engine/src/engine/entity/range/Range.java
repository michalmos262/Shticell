package engine.entity.range;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.exception.range.RangeNotInRightFormatException;

import java.util.*;

public class Range {
    private final CellPositionInSheet fromPosition;
    private final CellPositionInSheet toPosition;
    private Set<CellPositionInSheet> includedPositions;

    public Range(CellPositionInSheet fromPosition, CellPositionInSheet toPosition) {
        validateRange(fromPosition, toPosition);
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        setIncludedPositions();
    }

    private void validateRange(CellPositionInSheet fromPosition, CellPositionInSheet toPosition) {
        if (!(fromPosition.getRow() <= toPosition.getRow() && fromPosition.getColumn() <= toPosition.getColumn())) {
            throw new RangeNotInRightFormatException(fromPosition, toPosition);
        }
    }

    public CellPositionInSheet getFromPosition() {
        return fromPosition;
    }

    public CellPositionInSheet getToPosition() {
        return toPosition;
    }

    private void setIncludedPositions() {
        int fromRow = fromPosition.getRow();
        int fromColumn = fromPosition.getColumn();
        int toRow = toPosition.getRow();
        int toColumn = toPosition.getColumn();
        includedPositions = new LinkedHashSet<>();
        for (int row = fromRow; row <= toRow; row++) {
            for (int column = fromColumn; column <= toColumn; column++) {
                includedPositions.add(PositionFactory.createPosition(row, column));
            }
        }
    }

    public LinkedHashSet<CellPositionInSheet> getIncludedPositions() {
        return new LinkedHashSet<>(includedPositions);
    }

    @Override
    public String toString() {
        return fromPosition + ".." + toPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range range = (Range) o;
        return Objects.equals(getFromPosition(), range.getFromPosition()) && Objects.equals(getToPosition(), range.getToPosition()) && Objects.equals(getIncludedPositions(), range.getIncludedPositions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFromPosition(), getToPosition(), getIncludedPositions());
    }
}