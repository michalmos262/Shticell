package engine.entity.sheet;

public class SheetDto {
    Sheet sheetDto;

    public SheetDto(SheetManager sheetManager, int version) {
        sheetDto = sheetManager.getVersion2sheet().get(version).clone();
    }
}