package engine.entity.dto;

import engine.entity.cell.Cell;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
        return new EffectiveValue(cellDto.getEffectiveValue().getCellType(),
                cellDto.getEffectiveValue().getValue());
    }

    public EffectiveValue getEffectiveValueForDisplay() {
        String effectiveValueStr = cellDto.getEffectiveValue().getValue().toString();
        if (effectiveValueStr.matches("-?\\d+(\\.\\d+)?")) {
            DecimalFormat formatter = new DecimalFormat("#,###.##");
            return new EffectiveValue(CellType.NUMERIC, formatter.format(new BigDecimal(effectiveValueStr)));
        }
        else if (effectiveValueStr.equalsIgnoreCase("true") || effectiveValueStr.equalsIgnoreCase("false")) {
            return new EffectiveValue(CellType.BOOLEAN, effectiveValueStr.toUpperCase());
        }
        return new EffectiveValue(CellType.STRING, effectiveValueStr);
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