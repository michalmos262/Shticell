package engine.entity.cell;

import java.util.HashMap;
import java.util.Map;

public class PositionFactory {
    private static final Map<String, CellPositionInSheet> cachedPositions = new HashMap<>();

    public static CellPositionInSheet createPosition(int row, int column) {
        String key = row + ":" + column;
        if (cachedPositions.containsKey(key)) {
            return cachedPositions.get(key);
        }
        CellPositionInSheet cellPosition = new CellPositionInSheet(row, column);
        cachedPositions.put(key, cellPosition);
        return cellPosition;
    }

    public static CellPositionInSheet createPosition(String position) {
        return createPosition(parseRow(position), parseColumn(position));
    }

    public static CellPositionInSheet createPosition(int row, String column) {
        return createPosition(row, parseColumn(column));
    }

    public static int parseRow(String position) {
        String numberPart = position.substring(1);
        return Integer.parseInt(numberPart);
    }

    public static int parseColumn(String position) {
        char letter = position.charAt(0);
        return letter - 'A';
    }
}
