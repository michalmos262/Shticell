package engine.entity.sheet;

import engine.entity.cell.CellDto;
import engine.entity.cell.CellPositionInSheet;

public class SheetDto {
    Sheet sheetDto;

    public SheetDto(Sheet sheet) {
        if (sheet == null) {
            sheetDto = new Sheet();
        } else {
            sheetDto = sheet.clone();
        }
    }

    public CellDto getCellDto(CellPositionInSheet cellPosition) {
        return new CellDto(sheetDto.getCell(cellPosition));
    }
}