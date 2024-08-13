package engine.entity.cell;

public class CellDto {
    private final Cell cellDto;

    public CellDto(Cell originalCell) {
        this.cellDto = originalCell.clone();
    }

    public String getOriginalValue() {
        return cellDto.getOriginalValue();
    }

    public String getEffectiveValue() {
        return cellDto.getEffectiveValue();
    }
}