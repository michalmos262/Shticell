package engine.exception.cell;

import engine.entity.cell.CellPositionInSheet;

public class EmptyCellException extends NullPointerException {
    private final CellPositionInSheet cellPositionInSheet;

    public EmptyCellException(CellPositionInSheet cellPositionInSheet) {
        this.cellPositionInSheet = cellPositionInSheet;
    }

    @Override
    public String getMessage() {
        return "Position " + cellPositionInSheet + " is empty.";
    }
}
