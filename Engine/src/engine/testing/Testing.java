package engine.testing;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.CellDto;
import engine.entity.dto.SheetDto;
import engine.entity.range.Range;
import engine.impl.EngineImpl;

import java.util.*;

public class Testing {

    private static void verifyOriginalValueAssertion(String originalValue, String actualValue, String expectedValue) {
        if (!Objects.equals(actualValue, expectedValue)) {
            throw new AssertionError(originalValue + " equals " + actualValue + " and not " + expectedValue);
        }
    }

    private static void checkPlus(Engine engine) {
        String originalValue = "{PLUS,1,2}";
        engine.updateSheetCell(1, 1, originalValue);
        String actualValue = engine.findCellInSheet(1, 1, engine.getCurrentSheetVersion()).getEffectiveValue().getValue().toString();
        String expectedValue = Double.toString(3);

        verifyOriginalValueAssertion(originalValue, actualValue, expectedValue);
    }

    private static void checkAddRange(Engine engine) {
        String originalValue = "{SUM,effective-grades}";
        engine.updateSheetCell(1, 1, originalValue);
    }

    private static void checkDeleteRange(Engine engine) {
        String rangeName = "weights";
        String originalValue = "{sum,"+rangeName+"}";
        engine.updateSheetCell(1, 1, originalValue);
        engine.deleteRange(rangeName);
        engine.updateSheetCell(1, 1, "blabla");
        engine.deleteRange(rangeName);
    }

    private static void checkSort(Engine engine) {
        CellPositionInSheet fromPosition = PositionFactory.createPosition("B2");
        CellPositionInSheet toPosition = PositionFactory.createPosition("E6");
        Range range = new Range(fromPosition, toPosition);
        LinkedHashSet<String> columns = new LinkedHashSet<>();
        columns.add("B");

        SheetDto sheetDto = engine.getSortedRowsSheet(range, columns);
        showSheetTable(sheetDto);
    }

    private static void checkFilter(Engine engine) {
        CellPositionInSheet fromPosition = PositionFactory.createPosition("B3");
        CellPositionInSheet toPosition = PositionFactory.createPosition("E6");
        Range range = new Range(fromPosition, toPosition);

//        Map<String, Set<EffectiveValue>> allUniqueValuesInColumns = engine.getUniqueColumnValuesByRange(range, columns);
        //System.out.println(allUniqueValuesInColumns);

        Map<String, Set<EffectiveValue>> allUniqueValuesInColumns = new HashMap<>();

        Set<EffectiveValue> bSet = new LinkedHashSet<>();
        bSet.add(new EffectiveValue(CellType.STRING, "ex 2"));
        bSet.add(new EffectiveValue(CellType.STRING, "ex 3"));

        Set<EffectiveValue> cSet = new LinkedHashSet<>();
        cSet.add(new EffectiveValue(CellType.NUMERIC, 80.0));

        allUniqueValuesInColumns.put("B", bSet);
        allUniqueValuesInColumns.put("C", cSet);

        SheetDto sheetDto = engine.getFilteredRowsSheet(range, allUniqueValuesInColumns);
        showSheetTable(sheetDto);
    }

    private static void showSheetTable(SheetDto sheetDto) {
        try {
            int numOfRows = sheetDto.getNumOfRows();
            int numOfColumns = 10;
            int rowHeight = 1;
            int columnWidth = 10;

            // Print the column headers
            System.out.print("   |"); // Space for row numbers
            for (int col = 0; col < numOfColumns; col++) {
                int padding = columnWidth - 1; // Space after the letter
                System.out.print((char) ('A' + col) + " ".repeat(padding) + "|");
            }
            System.out.println();

            // Print the table
            for (int row = 0; row < numOfRows; row++) {
                // Print row number
                if (row + 1 < 10) System.out.print("0");
                System.out.print((row + 1) + " ");

                // Print each cell in the row
                for (int col = 0; col < numOfColumns; col++) {
                    CellPositionInSheet cellPositionInSheet = new CellPositionInSheet(row+1, col+1);
                    CellDto cell = sheetDto.getCell(cellPositionInSheet);
                    String text = cell == null ? "" : cell.getEffectiveValueForDisplay().toString();
                    text = text.length() > columnWidth ? text.substring(0, columnWidth) : text;
                    int paddingRight = columnWidth - text.length();
                    System.out.print("|" + text + " ".repeat(paddingRight));
                }
                System.out.println("|");

                // Print the remaining cell rows (without row number)
                for (int h = 1; h < rowHeight; h++) {
                    System.out.print("   "); // Space for row numbers
                    for (int i = 0; i < numOfColumns; i++) {
                        System.out.print("|" + " ".repeat(columnWidth));
                    }
                    System.out.println("|");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        Engine engine = new EngineImpl();
        String filename = "C:\\Users\\asafl\\Downloads\\grades.xml";
        engine.loadFile(filename);
        checkFilter(engine);
    }
}
