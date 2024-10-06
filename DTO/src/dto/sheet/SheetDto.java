package dto.sheet;

import dto.cell.CellDto;
import dto.cell.CellPositionDto;

import java.util.Map;

public class SheetDto {
    private final Map<CellPositionDto, CellDto> position2cell;

    public SheetDto(Map<CellPositionDto, CellDto> position2cell) {
        this.position2cell = position2cell;
    }

    public CellDto getCell(CellPositionDto position) {
        return position2cell.get(position);
    }
}