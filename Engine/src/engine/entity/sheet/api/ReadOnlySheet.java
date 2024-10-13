package engine.entity.sheet.api;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.EffectiveValue;
import engine.entity.range.Range;

public interface ReadOnlySheet {
    int getUpdatedCellsCount();
    EffectiveValue getCellEffectiveValue(CellPositionInSheet cellPosition);
    Range getRangeByName(String name);
    int getVersion();
}
