package dto.cell;

public class CellPositionDto {
    private final int row;
    private final int column;
    private final String positionInSheet;

    public CellPositionDto(String positionStr) {
        this.row = Integer.parseInt(positionStr.substring(1));
        this.column = positionStr.charAt(0) - 'A' + 1;
        this.positionInSheet = positionStr;
    }

    public CellPositionDto(int row, int column) {
        this.row = row;
        this.column = column;
        this.positionInSheet = this.toString();
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public static String parseColumn(int column) {
        StringBuilder result = new StringBuilder();

        while (column > 0) {
            column--; // Adjust for zero-based indexing
            int remainder = column % 26;
            char letter = (char) (remainder + 'A');
            result.insert(0, letter); // Prepend the letter
            column /= 26;
        }

        return result.toString();
    }

    @Override
    public String toString() {
        return parseColumn(column) + (row);
    }
}