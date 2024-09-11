package engine.exception.range;

import engine.entity.cell.CellPositionInSheet;

public class RangeNotInRightFormatException extends IllegalArgumentException {
    private final CellPositionInSheet fromPosition;
    private final CellPositionInSheet toPosition;

    public RangeNotInRightFormatException(CellPositionInSheet fromPosition, CellPositionInSheet toPosition) {
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
    }

    @Override
    public String getMessage() {
        return "Range not in right format: " + fromPosition + " is bigger than " + toPosition;
    }
}
