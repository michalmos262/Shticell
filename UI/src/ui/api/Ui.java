package ui.api;

public interface Ui {
    void showCurrentVersionSheet();
    void showCellFromSheet();
    void updateSheetCell();
    void showSheetVersionsForDisplay();
    void loadFile();
    void saveCurrentSheetVersionsToFile();
    void loadSystemFromFile();
    void exitProgram();
}