package engine.exception.range;

import engine.entity.range.Range;

public class ColumnIsNotPartOfRangeException extends IllegalArgumentException {
    private final String column;
    private final Range range;

    public ColumnIsNotPartOfRangeException(String column, Range range) {
        this.column = column;
        this.range = range;
    }

    @Override
    public String getMessage() {
        return "Column " + column + " is not part of range " + range;
    }
}
