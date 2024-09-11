package engine.entity.sheet;

import engine.entity.cell.Cell;
import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;

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
            if (cell.getEffectiveValue().getCellType() == CellType.NUMERIC) {
                return true;
            }
        }
        return false;
    }

    public double compareTo(Row otherRow, String column) {
        Cell myCell = cells.get(column);
        Cell otherCell = otherRow.cells.get(column);

        if (areTwoCellsNumeric(myCell, otherCell)) {
            return myCell.getEffectiveValue().extractValueWithExpectation(Double.class) - otherCell.getEffectiveValue().extractValueWithExpectation(Double.class);
//            if (isMyCellInColumnBiggerThenOther(otherRow, column)) {
//                EffectiveValue tempEffectiveValue = myCell.getEffectiveValue();
//                myCell.setEffectiveValue(otherCell.getEffectiveValue());
//                otherCell.setEffectiveValue(tempEffectiveValue);
//            }
        }
        return 0;
    }

    private boolean areTwoCellsNumeric(Cell cell1, Cell cell2) {
        return cell1.getEffectiveValue().getCellType() == CellType.NUMERIC &&
                cell2.getEffectiveValue().getCellType() == CellType.NUMERIC;
    }

    private boolean isMyCellInColumnBiggerThenOther(Row otherRow, String column) {
        return cells.get(column).getEffectiveValue().extractValueWithExpectation(Double.class) >
                    otherRow.cells.get(column).getEffectiveValue().extractValueWithExpectation(Double.class);
    }

    public void addCell(String column, Cell cell) {
        cells.put(column, cell);
    }

    public int getRowNumber() {
        return rowNumber;
    }
}
