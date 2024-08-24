package engine.entity.cell;

import engine.exception.cell.CellPositionFormatException;

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

    private static void checkPositionFormat(String position) {
        if (!position.matches("^[A-Za-z]\\d+$")) {
            throw new CellPositionFormatException(position);
        }
    }

    public static CellPositionInSheet createPosition(String position) {
        checkPositionFormat(position);
        return createPosition(subRowFromPosition(position), subColumnFromPosition(position));
    }

    public static CellPositionInSheet createPosition(int row, String column) {
        checkPositionFormat(column + row);
        return createPosition(row, subColumnFromPosition(column));
    }

    public static int subRowFromPosition(String position) {
        // Initialize a variable to accumulate the number
        StringBuilder numberPart = new StringBuilder();

        // Iterate through the input string to extract numbers
        for (int i = 0; i < position.length(); i++) {
            char ch = position.charAt(i);

            if (Character.isDigit(ch)) {
                numberPart.append(ch);
            }
        }

        // If there's no number part, return 0 or handle it as needed
        if (numberPart.isEmpty()) {
            return 0; // Or throw an exception if appropriate
        }

        // Convert the accumulated number part to an integer
        return Integer.parseInt(numberPart.toString());
    }

    public static int subColumnFromPosition(String position) {
        StringBuilder capitalLetters = new StringBuilder();

        // Iterate through each character of the input string
        for (int i = 0; i < position.length(); i++) {
            char ch = position.charAt(i);

            // If the character is an alphabetic letter, append it as uppercase to the result
            if (Character.isAlphabetic(ch)) {
                capitalLetters.append(Character.toUpperCase(ch));
            } else {
                // Stop when we encounter a non-uppercase letter
                break;
            }
        }

        int result = 0;
        // Iterate through each character of the input string
        for (int i = 0; i < capitalLetters.length(); i++) {
            char ch = capitalLetters.charAt(i);

            // Convert the character to its corresponding integer value (A = 1, B = 2, ..., Z = 26)
            int value = ch - 'A' + 1;

            // Accumulate the result using base-26 logic
            result = result * 26 + value;
        }

        return result;
    }
}
