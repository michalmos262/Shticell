package ui.impl.console;

import engine.api.Engine;
import engine.entity.dto.CellDto;
import engine.entity.cell.CellPositionInSheet;
import engine.exception.cell.NotExistsCellException;
import engine.exception.sheet.NoDataLoadedException;
import engine.impl.EngineImpl;
import engine.operation.Operation;
import ui.api.Ui;
import engine.exception.sheet.InvalidSheetVersionException;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static java.lang.System.exit;

public class ConsoleInteraction implements Ui {
    private final Engine engine;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleInteraction() {
        engine = new EngineImpl();
    }

    @Override
    public void loadFile() {
        try {
            System.out.println("Enter a " + Engine.SUPPORTED_FILE_TYPE.toUpperCase() + " file path:");
            String filename = scanner.nextLine().trim();
            engine.loadFile(filename);
            System.out.println("File was loaded successfully!");
        } catch (Exception e) {
            System.out.println("Error with loading file: " + e.getMessage());
        }
    }

    public void checkIfThereIsData() {
        if (!engine.isDataLoaded()) {
            throw new NoDataLoadedException();
        }
    }

    private void showSheetTable(int version) {
        try {
            int numOfRows = engine.getNumOfSheetRows();
            int numOfColumns = engine.getNumOfSheetColumns();
            int rowHeight = engine.getSheetRowHeight();
            int columnWidth = engine.getSheetColumnWidth();

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
                    CellDto cell = engine.findCellInSheet(row + 1, col + 1, version);
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

    private CellPositionInSheet getCellPositionFromUser() {
        String userInput;
        CellPositionInSheet cellPositionInSheet;

        System.out.println("Enter sheet cell position (for example 'A1' - means row 1, column A):");
        userInput = scanner.nextLine().trim();
        cellPositionInSheet = engine.getCellPositionInSheet(userInput);
        return cellPositionInSheet;
    }

    @Override
    public void showCurrentVersionSheet() {
        try {
            checkIfThereIsData();
            System.out.println("Sheet name: " + engine.getSheetName());
            System.out.println("Current sheet version: " + engine.getCurrentSheetVersion());
            showSheetTable(engine.getCurrentSheetVersion());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void printCellPreData(int row, int column) {
        System.out.println("Cell position in sheet: " + engine.getCellPositionInSheet(row, column));
        CellDto cell = engine.findCellInSheet(row, column, engine.getCurrentSheetVersion());
        System.out.println("Current original value: " + (cell == null ? " " : cell.getOriginalValue()));
        System.out.println("Current effective value: " + (cell == null ? " " : cell.getEffectiveValueForDisplay()));
    }

    private void printCellAdditionalData(int lastVersionUpdated, List<CellPositionInSheet> affectsCellsList, List<CellPositionInSheet> affectedByCellsList) {
        System.out.println("Last cell version: " + lastVersionUpdated);
        System.out.println("The cells that the required cell is affecting: " + (affectsCellsList.isEmpty() ? "None" : affectsCellsList));
        System.out.println("The cells that the required cell is affected by: " + (affectedByCellsList.isEmpty() ? "None" : affectedByCellsList));
    }

    @Override
    public void showCellFromSheet() {
        CellPositionInSheet cellPosition = null;
        int row, column;

        try {
            checkIfThereIsData();
            cellPosition = getCellPositionFromUser();
            row = cellPosition.getRow();
            column = cellPosition.getColumn();
            int cellUpdatedVersion = engine.getLastCellVersion(row, column);
            printCellPreData(row, column);
            List<CellPositionInSheet> affectsCellsList = engine.getInfluencesList(row, column, engine.getCurrentSheetVersion());
            List<CellPositionInSheet> affectedByCellsList = engine.getInfluencedByList(row, column, engine.getCurrentSheetVersion());
            printCellAdditionalData(cellUpdatedVersion, affectsCellsList, affectedByCellsList);
        } catch (NotExistsCellException e) {
            row = cellPosition.getRow();
            column = cellPosition.getColumn();
            printCellPreData(row, column);
            printCellAdditionalData(0, List.of(), List.of());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void printWhatCellCanUpdate() {
        System.out.println("You can enter any text you'd like, even an expression.");
        System.out.println("Functions documentation:");
        System.out.println("-----------------------");

        for(Operation operation : Operation.values()) {
            System.out.println(operation.getDocumentation());
        }
        System.out.println("Enter the value:");
    }

    @Override
    public void updateSheetCell() {
        try {
            checkIfThereIsData();
            CellPositionInSheet cellPosition = getCellPositionFromUser();
            int row = cellPosition.getRow();
            int column = cellPosition.getColumn();
            printCellPreData(row, column);
            printWhatCellCanUpdate();
            String newCellValue = scanner.nextLine().trim();
            engine.updateSheetCell(row, column, newCellValue);
            System.out.println("\nCell on position " + cellPosition + " was updated successfully!\n");
            showCurrentVersionSheet();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void printVersion2updatedCellsCountAsTable() {
        Map<Integer, Integer> version2updatedCellsCount = engine.getSheetVersions();

        System.out.printf("%-10s %-10s%n", "Version", "Updated cells amount");
        System.out.println("-------------------------------");
        for (Map.Entry<Integer, Integer> entry : version2updatedCellsCount.entrySet()) {
            System.out.printf("%-10d %-10d%n", entry.getKey(), entry.getValue());
        }
    }

    private int getSheetVersionFromUser() {
        String userInput = "";

        try {
            System.out.println("Enter the version you want to show its sheet from the table below:");
            userInput = scanner.nextLine().trim();
            int version = Integer.parseInt(userInput);
            engine.validateSheetVersionExists(version);
            return version;
        } catch (Exception e) {
            throw new InvalidSheetVersionException(userInput);
        }
    }

    @Override
    public void showSheetVersionsForDisplay() {
        try {
            checkIfThereIsData();
            System.out.println("The sheet versions available:");
            printVersion2updatedCellsCountAsTable();
            int version = getSheetVersionFromUser();
            showSheetTable(version);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void saveCurrentSheetVersionsToFile() {
        try {
            System.out.println("Enter a file name for saving the sheet:");
            String fileName = scanner.nextLine().trim();
            engine.writeSystemToFile(fileName);
            System.out.println("Sheet saved to file: " + fileName + "." + engine.SYSTEM_FILE_TYPE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void loadSystemFromFile() {
        try {
            System.out.println("Enter a file name for loading a system:");
            String fileName = scanner.nextLine().trim();
            engine.readSystemFromFile(fileName);
            System.out.println("System was loaded from file: " + fileName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void exitProgram() {
        System.out.println("Goodbye!");
        exit(200);
    }
}