package ui.impl.console;

import engine.entity.cell.CellDto;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.sheet.Sheet;
import engine.impl.ShticellEngine;
import engine.entity.sheet.SheetDto;
import ui.api.Ui;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static java.lang.System.exit;

public class ConsoleInteraction implements Ui {
    public static final int EXIT = -1;
    private final ShticellEngine engine;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleInteraction() {
        engine = new ShticellEngine("Some name", 3, 5, 3, 15);
    }

    public ShticellEngine getEngine() {
        return engine;
    }

    private void showSheetTable(int version) {
        SheetDto sheetDto = engine.getSheetDto(version);
        Sheet.Dimension sheetDimension = engine.getSheetDimension();
        int numOfRows = sheetDimension.getNumOfRows();
        int numOfColumns = sheetDimension.getNumOfColumns();
        int rowHeight = sheetDimension.getRowHeight();
        int columnWidth = sheetDimension.getColumnWidth();

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
                CellDto cell = engine.findCellInSheet(new CellPositionInSheet(row, col), version);
                String text = cell == null ? "" : cell.getEffectiveValue();
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
    public CellPositionInSheet getCellPositionFromUser() {
        System.out.println("Enter sheet cell position (for example 'A1' - means row 1, column A): ");
        String cellPositionStr = scanner.nextLine();
        return new CellPositionInSheet(cellPositionStr);
    }

    @Override
    public void showSheet(int version) {
        System.out.println("Sheet name: " + engine.getSheetName());
        System.out.println("Current sheet version: " + engine.getCurrentSheetVersion());
        showSheetTable(version);
    }

    private void printSomeCellData(CellPositionInSheet cellPosition) {
        System.out.println("Cell position in sheet: " + cellPosition);
        CellDto cell = engine.findCellInSheet(cellPosition, engine.getCurrentSheetVersion());
        System.out.println("Current original value: " + cell.getOriginalValue());
        System.out.println("Current effective value: " + cell.getEffectiveValue());
    }

    @Override
    public void showSheetCell() {
        CellPositionInSheet cellPosition = getCellPositionFromUser();
        printSomeCellData(cellPosition);
        System.out.println("Last cell version: " + engine.getLastCellVersion(cellPosition));
        List<CellPositionInSheet> affectedCellsList = engine.getAffectedCellsList(cellPosition, engine.getCurrentSheetVersion());
        List<CellPositionInSheet> affectedByCellsList = engine.getAffectedByCellsList(cellPosition, engine.getCurrentSheetVersion());
        System.out.println("The cells that the required cell is affecting: " + (affectedCellsList == null ? "None" : affectedCellsList));
        System.out.println("The cells that the required cell is affected by: " + (affectedByCellsList == null ? "None" : affectedByCellsList));
    }

    @Override
    public void updateSheetCell() {
        CellPositionInSheet cellPosition = getCellPositionFromUser();
        printSomeCellData(cellPosition);
        System.out.println("Enter new cell value:");
        String newCellValue = scanner.nextLine();
        engine.updateSheetCell(cellPosition, newCellValue);
    }

    private void printVersion2updatedCellsCountAsTable(Map<Integer, Integer> map) {
        System.out.printf("%-10s %-10s%n", "Version", "Updated cells amount");
        System.out.println("----------------------");
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            System.out.printf("%-10d %-10d%n", entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void showSheetVersions() {
        System.out.println("The sheet versions available:");
        printVersion2updatedCellsCountAsTable(engine.getSheetVersions());
        System.out.println("Enter the version you want to show its sheet:");
        String versionStr = scanner.nextLine();
        showSheetTable(Integer.parseInt(versionStr));
    }

    @Override
    public void exitProgram() {
        exit(200);
    }
}