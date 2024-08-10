package engine.impl.entities;

import java.util.*;

public class Sheet {
    private Map<Integer, Cell<?>>[][] version2cell;
    private int currVersion;
    private String name;
    private List<Integer> cellCountInVersions;
    private Map<Cell<?>, List<Cell<?>>> cell2affectingCells;
    private Map<Cell<?>, List<Cell<?>>> cell2affectedByCells;
    private int numOfRows, numOfColumns, rowHeight, columnWidth;

    public Sheet(String name, int numOfRows, int numOfColumns, int rowHeight, int columnWidth) {
        this.numOfColumns = numOfColumns;
        this.numOfRows = numOfRows;
        this.rowHeight = rowHeight;
        this.columnWidth = columnWidth;
        version2cell = new LinkedHashMap[numOfRows][numOfColumns];
        this.name = name;
        currVersion = 1;
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfColumns ; j++) {
                version2cell[i][j] = new LinkedHashMap<>();
            }
        }
        cellCountInVersions = new LinkedList<>();
        cell2affectingCells = new LinkedHashMap<>();
        cell2affectedByCells = new LinkedHashMap<>();
    }

    public Map<Integer, Cell<?>>[][] getVersion2cell() {
        return version2cell;
    }

    public void setVersion2cell(Map<Integer, Cell<?>>[][] version2cell) {
        this.version2cell = version2cell;
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

    public Map<Cell<?>, List<Cell<?>>> getCell2affectingCells() {
        return cell2affectingCells;
    }

    public void setCell2affectingCells(Map<Cell<?>, List<Cell<?>>> cell2affectingCells) {
        this.cell2affectingCells = cell2affectingCells;
    }

    public Map<Cell<?>, List<Cell<?>>> getCell2affectedByCells() {
        return cell2affectedByCells;
    }

    public void setCell2affectedByCells(Map<Cell<?>, List<Cell<?>>> cell2affectedByCells) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sheet sheet = (Sheet) o;
        return currVersion == sheet.currVersion && Objects.deepEquals(version2cell, sheet.version2cell) && Objects.equals(name, sheet.name) && Objects.equals(cellCountInVersions, sheet.cellCountInVersions) && Objects.equals(cell2affectingCells, sheet.cell2affectingCells) && Objects.equals(cell2affectedByCells, sheet.cell2affectedByCells);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(version2cell), currVersion, name, cellCountInVersions, cell2affectingCells, cell2affectedByCells);
    }
}
