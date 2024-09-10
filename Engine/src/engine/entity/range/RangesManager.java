package engine.entity.range;

import engine.entity.cell.CellPositionInSheet;
import engine.exception.range.CannotDeleteUsedRangeException;
import engine.exception.range.RangeAlreadyExistsException;

import java.util.*;

public class RangesManager {
    Map<String, Range> name2Range;
    Map<String, List<CellPositionInSheet>> rangeName2influencedPositions;

    public RangesManager() {
        name2Range = new HashMap<>();
        rangeName2influencedPositions = new HashMap<>();
    }

    public Map<String, Range> getName2Range() {
        return name2Range;
    }

    public Range getRangeByName(String name) {
        return name2Range.get(name);
    }

    public void createRange(String name, CellPositionInSheet fromPosition, CellPositionInSheet toPosition) {
        if (name2Range.containsKey(name)) {
            throw new RangeAlreadyExistsException(name);
        }
        Range range = new Range(fromPosition, toPosition);
        name2Range.put(name, range);
    }

    public void deleteRange(String name) {
        // if the range name is used
        if (rangeName2influencedPositions.get(name) != null && !rangeName2influencedPositions.get(name).isEmpty()) {
            throw new CannotDeleteUsedRangeException(name);
        }
        name2Range.remove(name);
        rangeName2influencedPositions.remove(name);
    }

    public void useRange(String name) {
        //TODO: delete range
//        rangeName2influencedPositions.computeIfAbsent(name, k -> new LinkedList<>()).add(cellPositionInSheet);
    }
}