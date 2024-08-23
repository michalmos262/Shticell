package engine.exception.cell;

public class CellPositionFormatException extends IllegalArgumentException {
    private final String cellPosition;

    public CellPositionFormatException(String cellPosition) {
        this.cellPosition = cellPosition;
    }

    @Override
    public String getMessage() {
        return "Invalid cell position format: " + cellPosition + ". Position format should be for example 'A1' which means row 1, column A.";
    }
}