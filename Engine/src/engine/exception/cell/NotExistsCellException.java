package engine.exception.cell;

import engine.entity.cell.CellPositionInSheet;

public class NotExistsCellException extends NullPointerException {
    private final CellPositionInSheet cellPositionInSheet;

    public NotExistsCellException(CellPositionInSheet cellPositionInSheet) {
        this.cellPositionInSheet = cellPositionInSheet;
    }

    @Override
    public String getMessage() {
        return "Position " + cellPositionInSheet + " is empty.";
    }
}
