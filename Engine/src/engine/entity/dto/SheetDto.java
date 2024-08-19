package engine.entity.dto;

import engine.entity.cell.Cell;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.sheet.Sheet;

import java.util.HashMap;
import java.util.Map;

public class SheetDto {
    private final Map<CellPositionInSheet, CellDto> position2cell;

    public SheetDto(Sheet sheet) {
        position2cell = new HashMap<>();
        for (Map.Entry<CellPositionInSheet, Cell> entry: sheet.getPosition2cell().entrySet()) {
            position2cell.put(entry.getKey(), new CellDto(entry.getValue()));
        }
    }

    public CellDto getCell(CellPositionInSheet position) {
        return position2cell.get(position);
    }

    public CellDto getCellDto(CellPositionInSheet cellPosition) {
        return getCell(cellPosition);
    }
}