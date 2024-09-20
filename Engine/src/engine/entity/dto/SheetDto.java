package engine.entity.dto;

import engine.entity.cell.CellPositionInSheet;

import java.util.Map;

public class SheetDto {
    private final Map<CellPositionInSheet, CellDto> position2cell;

    public SheetDto(Map<CellPositionInSheet, CellDto> position2cell) {
        this.position2cell = position2cell;
    }

    public CellDto getCell(CellPositionInSheet position) {
        return position2cell.get(position);
    }
}