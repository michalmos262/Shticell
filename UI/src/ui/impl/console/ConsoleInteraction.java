package ui.impl.console;

import engine.api.Engine;
import engine.entity.dto.CellDto;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.sheet.SheetDimension;
import engine.impl.ShticellEngine;
import jakarta.xml.bind.JAXBException;
import ui.api.Ui;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static java.lang.System.exit;

public class ConsoleInteraction implements Ui {
    private Engine engine = null;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleInteraction() {
        try {
            engine = new ShticellEngine("C:\\Users\\asafl\\Downloads\\basic.xml");
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private void showSheetTable(int version) {
        SheetDimension sheetDimension = engine.getSheetDimension();
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
                CellDto cell = engine.findCellInSheet(row + 1, col, version);
                String text = cell == null ? "" : cell.getEffectiveValue().toString();
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
    public String getCellPositionFromUser() {
        System.out.println("Enter sheet cell position (for example 'A1' - means row 1, column A): ");
        return scanner.nextLine();
    }

    @Override
    public void showCurrentVersionSheet() {
        System.out.println("Sheet name: " + engine.getSheetName());
        System.out.println("Current sheet version: " + engine.getCurrentSheetVersion());
        showSheetTable(engine.getCurrentSheetVersion());
    }

    private void printSomeCellData(int row, int column) {
        System.out.println("Cell position in sheet: " + engine.getCellPositionInSheet(row, column));
        CellDto cell = engine.findCellInSheet(row, column, engine.getCurrentSheetVersion());
        System.out.println("Current original value: " + (cell == null ? " " : cell.getOriginalValue()));
        System.out.println("Current effective value: " + (cell == null ? " " : cell.getEffectiveValue()));
    }

    @Override
    public void showSheetCell() {
        String cellPosition = getCellPositionFromUser();
        int row = engine.parseRowFromPosition(cellPosition);
        int column = engine.parseColumnFromPosition(cellPosition);
        printSomeCellData(row, column);
        System.out.println("Last cell version: " + engine.getLastCellVersion(row, column));
        List<CellPositionInSheet> affectedCellsList = engine.getInfluencesList(row, column, engine.getCurrentSheetVersion());
        List<CellPositionInSheet> affectedByCellsList = engine.getInfluencedByList(row, column, engine.getCurrentSheetVersion());
        System.out.println("The cells that the required cell is affecting: " + (affectedCellsList.isEmpty() ? "None" : affectedCellsList));
        System.out.println("The cells that the required cell is affected by: " + (affectedByCellsList.isEmpty() ? "None" : affectedByCellsList));
    }

    @Override
    public void updateSheetCell() {
        String cellPosition = getCellPositionFromUser();
        int row = engine.parseRowFromPosition(cellPosition);
        int column = engine.parseColumnFromPosition(cellPosition);
        printSomeCellData(row, column);
        System.out.println("Enter new cell value:");
        String newCellValue = scanner.nextLine();
        engine.updateSheetCell(row, column, newCellValue);
        showSheetTable(engine.getCurrentSheetVersion());
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
        System.out.println("Goodbye!");
        exit(200);
    }
}