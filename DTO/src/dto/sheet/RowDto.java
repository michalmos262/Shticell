package dto.sheet;

import dto.cell.CellDto;

import java.util.Map;

public class RowDto {
    private final int rowNumber;
    private final Map<String, CellDto> cells;

    public RowDto(int rowNumber, Map<String, CellDto> cells) {
        this.rowNumber = rowNumber;
        this.cells = cells;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public Map<String, CellDto> getCells() {
        return cells;
    }
}
