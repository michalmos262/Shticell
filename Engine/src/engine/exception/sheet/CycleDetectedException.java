package engine.exception.sheet;

import engine.entity.cell.CellPositionInSheet;

public class CycleDetectedException extends RuntimeException {
    private final CellPositionInSheet from;
    private final CellPositionInSheet to;

    public CycleDetectedException(CellPositionInSheet from, CellPositionInSheet to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String getMessage() {
        return "Cycle detected when trying to reference position " + from + " to position " + to + ".";
    }
}