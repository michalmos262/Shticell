package engine.entity.sheet;

import engine.entity.cell.Cell;
import engine.entity.cell.CellType;

import java.util.HashMap;
import java.util.Map;

public class Row {
    private final int rowNumber;
    private final Map<String, Cell> cells;

    // Constructor, getters, setters and methods
    public Row(int rowNumber) {
        this.rowNumber = rowNumber;
        this.cells = new HashMap<>();
    }

    public boolean hasNumericValues() {
        for (Cell cell : cells.values()) {
            if (cell != null && cell.getEffectiveValue().getCellType() == CellType.NUMERIC) {
                return true;
            }
        }
        return false;
    }

    public int compareTo(Row otherRow, String column) {
        Cell myCell = cells.get(column);
        Cell otherCell = otherRow.cells.get(column);

        if (areTwoCellsNumeric(myCell, otherCell)) {
            return Double.compare(myCell.getEffectiveValue().extractValueWithExpectation(Double.class),
                    otherCell.getEffectiveValue().extractValueWithExpectation(Double.class));
        }
        return 0;
    }

    private boolean areTwoCellsNumeric(Cell cell1, Cell cell2) {
        return cell1 != null && cell2 != null &
                cell1.getEffectiveValue().getCellType() == CellType.NUMERIC &&
                cell2.getEffectiveValue().getCellType() == CellType.NUMERIC;
    }

    public Map<String, Cell> getCells() {
        return cells;
    }

    public void addCell(String column, Cell cell) {
        cells.put(column, cell);
    }

    public int getRowNumber() {
        return rowNumber;
    }
}
