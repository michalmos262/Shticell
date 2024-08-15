package engine.entity.sheet;

import engine.entity.cell.Cell;
import engine.entity.cell.CellPositionInSheet;

import java.util.*;

public class SheetDto {
    Cell[][] sheetDto;
    Map<Integer, Integer> version2updatedCellsCount;

    public SheetDto(Sheet originalSheet, int version) {
        int numOfRows = originalSheet.getDimension().getNumOfRows();
        int numOfColumns = originalSheet.getDimension().getNumOfColumns();
        sheetDto = new Cell[numOfRows][numOfColumns];
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfColumns; j++) {
                CellPositionInSheet cellPosition = new CellPositionInSheet(i, j);
                sheetDto[i][j] = originalSheet.getCellByVersion(cellPosition, version).getValue().clone();
            }
        }
        this.version2updatedCellsCount = new HashMap<>(originalSheet.getVersion2updatedCellsCount());
    }

    public Cell[][] getCellTable() {
        return sheetDto;
    }

    public Map<Integer, Integer> getVersion2updatedCellsCount() {
        return version2updatedCellsCount;
    }
}