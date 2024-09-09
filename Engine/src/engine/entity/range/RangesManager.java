package engine.entity.range;

import engine.entity.cell.CellPositionInSheet;
import engine.exception.range.RangeAlreadyExistsException;

import java.util.HashMap;
import java.util.Map;

public class RangesManager {
    Map<String, Range> name2Range;

    public RangesManager() {
        name2Range = new HashMap<>();
    }

    public void createRange(String name, CellPositionInSheet fromPosition, CellPositionInSheet toPosition) {
        if (name2Range.containsKey(name)) {
            throw new RangeAlreadyExistsException(name);
        }
        Range range = new Range(fromPosition, toPosition);
        name2Range.put(name, range);
    }

    public Range getRangeByName(String name) {
        return name2Range.get(name);
    }
}