package engine.entity.range;

import engine.entity.cell.CellPositionInSheet;
import engine.exception.range.CannotDeleteUsedRangeException;
import engine.exception.range.RangeAlreadyExistsException;
import engine.exception.range.RangeNotExistException;

import java.util.*;

public class RangesManager {
    Map<String, Range> name2Range;
    Map<String, Integer> name2usageCount;

    public RangesManager() {
        name2Range = new HashMap<>();
        name2usageCount = new HashMap<>();
    }

    public Map<String, Range> getName2Range() {
        return name2Range;
    }

    public Range getRangeByName(String name) {
        return name2Range.get(name);
    }

    public Range createRange(String name, CellPositionInSheet fromPosition, CellPositionInSheet toPosition) {
        if (name2Range.containsKey(name)) {
            throw new RangeAlreadyExistsException(name);
        }
        Range range = new Range(fromPosition, toPosition);
        name2Range.put(name, range);

        return range;
    }

    public void deleteRange(String name) {
        if (name2Range.get(name) == null) {
            throw new RangeNotExistException(name);
        }
        // if the range name is already used
        if (name2usageCount.get(name) != null && name2usageCount.get(name) != 0) {
            throw new CannotDeleteUsedRangeException(name);
        }
        name2Range.remove(name);
        name2usageCount.remove(name);
    }

    public Range getUnNamedRange(CellPositionInSheet fromPosition, CellPositionInSheet toPosition) {
        return new Range(fromPosition, toPosition);
    }

    public void useRange(String name) {
        name2usageCount.putIfAbsent(name, 0);
        name2usageCount.put(name, name2usageCount.get(name) + 1);
    }

    public void unUseRange(String name) {
        if (name2usageCount.get(name) > 0) {
            name2usageCount.put(name, name2usageCount.get(name) - 1);
        }
    }
}