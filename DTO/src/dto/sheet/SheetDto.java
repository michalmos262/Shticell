package dto.sheet;

import dto.cell.CellDto;
import dto.cell.CellPositionDto;

import java.util.Map;

public class SheetDto {
    private final Map<CellPositionDto, CellDto> position2cell;
    private final int version;

    public SheetDto(Map<CellPositionDto, CellDto> position2cell, int version) {
        this.position2cell = position2cell;
        this.version = version;
    }

    public CellDto getCell(CellPositionDto position) {
        return position2cell.get(position);
    }

    public int getVersion() {
        return version;
    }
}