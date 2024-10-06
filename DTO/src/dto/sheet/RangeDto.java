package dto.sheet;

import dto.cell.CellPositionDto;
import engine.entity.cell.PositionFactory;

import java.util.LinkedHashSet;
import java.util.Set;

public class RangeDto {
    private final CellPositionDto fromPosition;
    private final CellPositionDto toPosition;
    private Set<CellPositionDto> includedPositions;

    public RangeDto(CellPositionDto fromPosition, CellPositionDto toPosition, Set<CellPositionDto> includedPositions) {
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.includedPositions = includedPositions;
    }

    public RangeDto(CellPositionDto fromPosition, CellPositionDto toPosition) {
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        setIncludedPositions();
    }

    public CellPositionDto getFromPosition() {
        return fromPosition;
    }

    public CellPositionDto getToPosition() {
        return toPosition;
    }

    public Set<CellPositionDto> getIncludedPositions() {
        return new LinkedHashSet<>(includedPositions);
    }

    public Set<String> getIncludedColumns() {
        Set<String> includedColumns = new LinkedHashSet<>();

        for (CellPositionDto position : includedPositions) {
            includedColumns.add(CellPositionDto.parseColumn(position.getColumn()));
        }
        return includedColumns;
    }

    private void setIncludedPositions() {
        int fromRow = fromPosition.getRow();
        int fromColumn = fromPosition.getColumn();
        int toRow = toPosition.getRow();
        int toColumn = toPosition.getColumn();
        includedPositions = new LinkedHashSet<>();
        for (int row = fromRow; row <= toRow; row++) {
            for (int column = fromColumn; column <= toColumn; column++) {
                includedPositions.add(new CellPositionDto(row, column));
            }
        }
    }

    @Override
    public String toString() {
        return fromPosition + ".." + toPosition;
    }
}