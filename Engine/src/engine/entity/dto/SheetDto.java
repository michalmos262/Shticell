package engine.entity.dto;

import engine.entity.cell.CellPositionInSheet;

import java.util.Map;

public class SheetDto {
    private final Map<CellPositionInSheet, CellDto> position2cell;
    private final int numOfRows;

    public SheetDto(Map<CellPositionInSheet, CellDto> position2cell, int numOfRows) {
        this.position2cell = position2cell;
        this.numOfRows = numOfRows;
    }

    public CellDto getCell(CellPositionInSheet position) {
        return position2cell.get(position);
    }

    public int getNumOfRows() {
        return numOfRows;
    }
}