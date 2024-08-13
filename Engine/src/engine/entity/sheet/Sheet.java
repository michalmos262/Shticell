package engine.entity.sheet;

import engine.entity.cell.*;
import engine.expression.api.Expression;

import java.util.*;

import static engine.expression.impl.ExpressionEvaluator.evaluateExpression;

public class Sheet {
    private final Map<Integer, Cell>[][] version2cellTable;
    private int currVersion;
    private final String name;
    private final Dimension dimension;
    private final Map<Integer, Integer> version2updatedCellsCount;
    private final Map<Integer, Map<CellPositionInSheet, List<CellPositionInSheet>>> version2cellPos2affectingCellsPos;
    private final Map<Integer, Map<CellPositionInSheet, List<CellPositionInSheet>>> version2cellPos2affectedByCellsPos;

    public Sheet(String name, Dimension dimension) {
        this.dimension = dimension;
        version2cellTable = new LinkedHashMap[dimension.getNumOfRows()][dimension.getNumOfColumns()];
        this.name = name;
        currVersion = 1;
        for (int i = 0; i < dimension.getNumOfRows(); i++) {
            for (int j = 0; j < dimension.getNumOfColumns() ; j++) {
                version2cellTable[i][j] = new LinkedHashMap<>();
                version2cellTable[i][j].put(1, new StringCell(" "));
            }
        }
        version2updatedCellsCount = new HashMap<>();
        version2updatedCellsCount.put(1, 0);
        version2cellPos2affectingCellsPos = new HashMap<>();
        version2cellPos2affectedByCellsPos = new HashMap<>();
    }

    public Map<Integer, Cell>[][] getVersion2cellTable() {
        return version2cellTable;
    }

    public int getCurrVersion() {
        return currVersion;
    }

    public String getName() {
        return name;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public Map<Integer, Integer> getVersion2updatedCellsCount() {
        return version2updatedCellsCount;
    }

    public Map<Integer, Map<CellPositionInSheet, List<CellPositionInSheet>>> getVersion2cellPos2affectingCellsPos() {
        return version2cellPos2affectingCellsPos;
    }

    public Map<Integer, Map<CellPositionInSheet, List<CellPositionInSheet>>> getVersion2cellPos2affectedByCellsPos() {
        return version2cellPos2affectedByCellsPos;
    }

    public Map.Entry<Integer, Cell> getCellByVersion(CellPositionInSheet cellPosition, int version) {
        Map.Entry<Integer, Cell> lastEntry = null;
        Map<Integer, Cell> version2Cell = version2cellTable[cellPosition.getRow()][cellPosition.getColumn()];
        for (Map.Entry<Integer, Cell> entry : version2Cell.entrySet()) {
            if (entry.getKey() <= version) {
                lastEntry = entry;
            } else {
                break; // Since the map is sorted, we can break the loop early
            }
        }
        return lastEntry;
    }

    public void updateCell(CellPositionInSheet cellPosition, String newValue) {
        int columnIndex = cellPosition.getColumn();
        int rowIndex = cellPosition.getRow();
        Cell cell = new StringCell(newValue);
        currVersion++;
        // if no cell is affecting/affected by any other cell in the current version
        version2cellPos2affectingCellsPos.computeIfAbsent(currVersion, k -> new LinkedHashMap<>());
        version2cellPos2affectedByCellsPos.computeIfAbsent(currVersion, k -> new LinkedHashMap<>());
        version2updatedCellsCount.putIfAbsent(currVersion, 0);

        if (newValue.matches("-?\\d+(\\.\\d+)?")) {
            cell = new NumberCell(newValue);
        }
        else if (newValue.equalsIgnoreCase("true") || newValue.equalsIgnoreCase("false")) {
            cell = new BoolCell(newValue);
        }
        else if (newValue.charAt(0) == '{' && newValue.charAt(newValue.length() - 1) == '}') {
            cell = new ExpCell(newValue);
//            // if the desired cell is not affecting any cell in the current version
//            if (version2cellPos2affectingCellsPos.get(currVersion).get(cellPosition) == null) {
//
//            }
        }
        cell.setEffectiveValueByOriginalValue();
        version2cellTable[rowIndex][columnIndex].put(currVersion, cell);
        version2updatedCellsCount.put(currVersion, version2updatedCellsCount.get(currVersion) + 1);
    }

    public static class Dimension implements Cloneable {
        private int numOfRows;
        private int numOfColumns;
        private int rowHeight;
        private int columnWidth;

        public Dimension(int numOfRows, int numOfColumns, int rowHeight, int columnWidth) {
            this.numOfRows = numOfRows;
            this.numOfColumns = numOfColumns;
            this.rowHeight = rowHeight;
            this.columnWidth = columnWidth;
        }

        public int getNumOfRows() {
            return numOfRows;
        }

        public int getNumOfColumns() {
            return numOfColumns;
        }

        public int getRowHeight() {
            return rowHeight;
        }

        public int getColumnWidth() {
            return columnWidth;
        }

        @Override
        public Dimension clone() {
            try {
                Dimension clone = (Dimension) super.clone();
                clone.numOfRows = numOfRows;
                clone.numOfColumns = numOfColumns;
                clone.rowHeight = rowHeight;
                clone.columnWidth = columnWidth;
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
