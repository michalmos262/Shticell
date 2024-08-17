package engine.entity.cell;

import java.util.List;
import java.util.Objects;

public class CellDto {
    private final Cell cellDto;

    public CellDto(Cell originalCell) {
        if (originalCell == null) {
            this.cellDto = new Cell(" ",
                    new EffectiveValue(CellType.STRING, " "),
                    1);
        }
        else {
            this.cellDto = originalCell.clone();
        }
    }

    public String getOriginalValue() {
        return cellDto.getOriginalValue();
    }

    public EffectiveValue getEffectiveValue() {
        return cellDto.getEffectiveValue();
    }

    public List<CellPositionInSheet> getInfluencedBy() {
        return cellDto.getInfluencedBy();
    }

    public List<CellPositionInSheet> getInfluences() {
        return cellDto.getInfluences();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellDto cellDto1 = (CellDto) o;
        return Objects.equals(cellDto, cellDto1.cellDto);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cellDto);
    }
}