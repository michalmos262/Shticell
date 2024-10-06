package dto.cell;

public class EffectiveValueDto {
    private final CellTypeDto cellType;
    private final Object value;

    public EffectiveValueDto(CellTypeDto cellType, Object value) {
        this.cellType = cellType;
        this.value = value;
    }

    public CellTypeDto getCellType() {
        return cellType;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}