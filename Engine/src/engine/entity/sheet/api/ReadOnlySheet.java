package engine.entity.sheet.api;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.EffectiveValue;

public interface ReadOnlySheet {
    int getUpdatedCellsCount();
    EffectiveValue getCellEffectiveValue(CellPositionInSheet cellPosition);
}
