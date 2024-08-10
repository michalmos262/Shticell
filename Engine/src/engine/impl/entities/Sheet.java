package engine.impl.entities;

import java.util.*;

public class Sheet {
    private Map<Integer, Cell<?>>[][] version2cellTable;
    private int currVersion;
    private String name;
    private List<Integer> cellCountInVersions;
    private Map<CellPositionInSheet, List<CellPositionInSheet>> cell2affectingCells;
    private Map<CellPositionInSheet, List<CellPositionInSheet>> cell2affectedByCells;
    private int numOfRows, numOfColumns, rowHeight, columnWidth;

    public Sheet(String name, int numOfRows, int numOfColumns, int rowHeight, int columnWidth) {
        this.numOfColumns = numOfColumns;
        this.numOfRows = numOfRows;
        this.rowHeight = rowHeight;
        this.columnWidth = columnWidth;
        version2cellTable = new LinkedHashMap[numOfRows][numOfColumns];
        this.name = name;
        currVersion = 1;
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfColumns ; j++) {
                version2cellTable[i][j] = new LinkedHashMap<>();
            }
        }
        cellCountInVersions = new LinkedList<>();
        cell2affectingCells = new LinkedHashMap<>();
        cell2affectedByCells = new LinkedHashMap<>();
    }

    public Map<Integer, Cell<?>>[][] getVersion2cellTable() {
        return version2cellTable;
    }

    public void setVersion2cellTable(Map<Integer, Cell<?>>[][] version2cellTable) {
        this.version2cellTable = version2cellTable;
    }

    public int getCurrVersion() {
        return currVersion;
    }

    public void setCurrVersion(int currVersion) {
        this.currVersion = currVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getCellCountInVersions() {
        return cellCountInVersions;
    }

    public void setCellCountInVersions(List<Integer> cellCountInVersions) {
        this.cellCountInVersions = cellCountInVersions;
    }

    public Map<CellPositionInSheet, List<CellPositionInSheet>> getCell2affectingCells() {
        return cell2affectingCells;
    }

    public void setCell2affectingCells(Map<CellPositionInSheet, List<CellPositionInSheet>> cell2affectingCells) {
        this.cell2affectingCells = cell2affectingCells;
    }

    public Map<CellPositionInSheet, List<CellPositionInSheet>> getCell2affectedByCells() {
        return cell2affectedByCells;
    }

    public void setCell2affectedByCells(Map<CellPositionInSheet, List<CellPositionInSheet>> cell2affectedByCells) {
        this.cell2affectedByCells = cell2affectedByCells;
    }

    public int getNumOfRows() {
        return numOfRows;
    }

    public void setNumOfRows(int numOfRows) {
        this.numOfRows = numOfRows;
    }

    public int getNumOfColumns() {
        return numOfColumns;
    }

    public void setNumOfColumns(int numOfColumns) {
        this.numOfColumns = numOfColumns;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
    }

    public Integer getLastVersionOfCell(Map<Integer, Cell<?>> version2cell) {
        Integer lastKey = null;
        for (Map.Entry<Integer, Cell<?>> entry : version2cell.entrySet()) {
            lastKey = entry.getKey();
        }
        return lastKey;
    }
}
