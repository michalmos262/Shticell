package serversdk.response;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Range {
    private final CellPositionInSheet fromPosition;
    private final CellPositionInSheet toPosition;
    private final Set<CellPositionInSheet> includedPositions;

    public Range(CellPositionInSheet fromPosition, CellPositionInSheet toPosition, Set<CellPositionInSheet> includedPositions) {
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.includedPositions = includedPositions;
    }

    public CellPositionInSheet getFromPosition() {
        return fromPosition;
    }

    public CellPositionInSheet getToPosition() {
        return toPosition;
    }

    public Set<CellPositionInSheet> getIncludedPositions() {
        return new LinkedHashSet<>(includedPositions);
    }

    public Set<String> getIncludedColumns() {
        Set<String> includedColumns = new LinkedHashSet<>();

        for (CellPositionInSheet position : includedPositions) {
            includedColumns.add(CellPositionInSheet.parseColumn(position.getColumn()));
        }
        return includedColumns;
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