package ui.impl.console;

import engine.impl.entities.Cell;
import engine.impl.entities.CellPositionInSheet;
import engine.impl.entities.Sheet;
import ui.api.Ui;
import engine.impl.ShticellEngine;

import java.util.Map;

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
                String text = sheet.getVersion2cellTable()[row][col].get(sheet.getCurrVersion()) == null ? "" : sheet.getVersion2cellTable()[row][col].get(sheet.getCurrVersion()).getEffectiveValue();
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

    @Override
    public void showSheetCell(String cellLocation) {
        Sheet sheet = engine.getSheet();
        CellPositionInSheet cellPosition = new CellPositionInSheet(cellLocation.charAt(1), cellLocation.charAt(0));
        int columnIndex = cellPosition.getColumnIndex();
        int rowIndex = cellPosition.getRowIndex();
        Map<Integer, Cell<?>> cell = sheet.getVersion2cellTable()[rowIndex][columnIndex];
        System.out.println("Cell location: " + cellLocation);
        int version = sheet.getLastVersionOfCell(cell);
        System.out.println("The original value: " + cell.get(version).getOriginalValue());
        System.out.println("The effective value: " + cell.get(version).getEffectiveValue());
        System.out.println("The last cell version: " + version);
        System.out.println("The cells that it's affecting: " + sheet.getCell2affectingCells().get(cellPosition));
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