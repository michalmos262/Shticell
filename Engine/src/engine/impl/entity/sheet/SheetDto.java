package engine.impl.entity.sheet;

import engine.impl.entity.cell.Cell;
import engine.impl.entity.cell.CellPositionInSheet;

import java.util.*;

public class SheetDto {
    Cell[][] sheet;
    Map<CellPositionInSheet, List<CellPositionInSheet>> cellPos2affectingCellsPos;
    Map<CellPositionInSheet, List<CellPositionInSheet>> cellPos2affectedByCellsPos;
    Map<Integer, Integer> version2updatedCellsCount;

    public SheetDto(Sheet originalSheet, int version) {
        int numOfRows = originalSheet.getDimension().getNumOfRows();
        int numOfColumns = originalSheet.getDimension().getNumOfColumns();
        sheet = new Cell[numOfRows][numOfColumns];
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfColumns; j++) {
                CellPositionInSheet cellPosition = new CellPositionInSheet(i, j);
                sheet[i][j] = originalSheet.getCellByVersion(cellPosition, version).getValue().clone();
            }
        }
        this.cellPos2affectingCellsPos = cloneCellPosRelationships(originalSheet.getVersion2cellPos2affectingCellsPos().get(version));
        this.cellPos2affectedByCellsPos = cloneCellPosRelationships(originalSheet.getVersion2cellPos2affectedByCellsPos().get(version));
        this.version2updatedCellsCount = new HashMap<>(originalSheet.getVersion2updatedCellsCount());
    }

    public Cell[][] getCellTable() {
        return sheet;
    }

    public Map<CellPositionInSheet, List<CellPositionInSheet>> getCellPos2affectingCellsPos() {
        return cellPos2affectingCellsPos;
    }

    public Map<CellPositionInSheet, List<CellPositionInSheet>> getCellPos2affectedByCellsPos() {
        return cellPos2affectedByCellsPos;
    }

    public Map<Integer, Integer> getVersion2updatedCellsCount() {
        return version2updatedCellsCount;
    }

    private Map<CellPositionInSheet, List<CellPositionInSheet>> cloneCellPosRelationships(Map<CellPositionInSheet, List<CellPositionInSheet>> originalMap) {
        Map<CellPositionInSheet, List<CellPositionInSheet>> clonedRelMap = new HashMap<>();
        if (originalMap != null) {
            for (Map.Entry<CellPositionInSheet, List<CellPositionInSheet>> mapEntry : originalMap.entrySet()) {
                CellPositionInSheet cellPosition = mapEntry.getKey().clone();
                clonedRelMap.put(cellPosition, new LinkedList<>());
                for (CellPositionInSheet reliedCellPosition : mapEntry.getValue()) {
                    clonedRelMap.get(cellPosition).add(reliedCellPosition);
                }
            }
        }
        return clonedRelMap;
    }
}