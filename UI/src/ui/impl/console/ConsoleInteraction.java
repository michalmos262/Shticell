package ui.impl.console;

import engine.impl.entities.Cell;
import engine.impl.entities.Sheet;
import ui.api.Ui;
import engine.impl.ShticellEngine;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ConsoleInteraction implements Ui {
    private final ShticellEngine engine;

    public ConsoleInteraction() {
        engine = new ShticellEngine("Some name", 3, 5, 3, 15);
    }

    private void showSheetTable() {
        Sheet sheet = engine.getSheet();
        int numOfRows = sheet.getNumOfRows();
        int numOfColumns = sheet.getNumOfColumns();
        int rowHeight = sheet.getRowHeight();
        int columnWidth = sheet.getColumnWidth();

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
                String text = sheet.getVersion2cell()[row][col].get(sheet.getCurrVersion()) == null ? "" : sheet.getVersion2cell()[row][col].get(sheet.getCurrVersion()).getEffectiveValue();
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
    }

    @Override
    public void showSheet() {
        Sheet sheet = engine.getSheet();
        System.out.println("Sheet name: " + sheet.getName());
        System.out.println("Current sheet version: " + sheet.getCurrVersion());
        showSheetTable();
    }

    public Integer getLastVersionOfCell(Map<Integer, Cell<?>> version2Cell) {
        Integer lastKey = null;
        for (Map.Entry<Integer, Cell<?>> entry : version2Cell.entrySet()) {
            lastKey = entry.getKey();
        }
        return lastKey;
    }

    @Override
    public void showSheetCell(String cellLocation) {
        Sheet sheet = engine.getSheet();
        int columnIndex = cellLocation.charAt(0) - 'A';
        int rowIndex = cellLocation.charAt(1) - '1';
        System.out.println("Cell location: " + cellLocation);
        Set<Integer> keys = sheet.getVersion2cell()[rowIndex][columnIndex].keySet();
        System.out.println("Cell original value: " + sheet.getVersion2cell()[rowIndex][columnIndex].get(sheet.getCurrVersion()));
    }

    @Override
    public void updateSheetCell(String cellLocation) {

    }

    @Override
    public void showSheetVersions() {

    }

    @Override
    public void exitProgram() {

    }
}