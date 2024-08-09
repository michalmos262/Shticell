package engine.impl.entities;

import java.util.*;

public class Sheet {
    private Map<Integer, Cell<?>>[][] version2cell;
    private int currVersion;
    private String name;
    private List<Integer> cellCountInVersions;
    private Map<Cell<?>, List<Cell<?>>> cell2affectingCells;
    private Map<Cell<?>, List<Cell<?>>> cell2affectedByCells;

    public Sheet(String name, int width, int height) {
        version2cell = new HashMap[width][height];
        this.name = name;
        currVersion = 1;
        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                version2cell[i][j] = new HashMap<>();
            }
        }
        cellCountInVersions = new LinkedList<>();
        cell2affectingCells = new HashMap<>();
        cell2affectedByCells = new HashMap<>();
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
