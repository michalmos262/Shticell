package engine.entity.cell;

import java.util.List;

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

    public List<Cell> getDependsOn() {
        return cellDto.getDependsOn();
    }

    public List<Cell> getInfluencingOn() {
        return cellDto.getInfluencingOn();
    }
}