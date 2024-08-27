package ui.api;

public interface Ui {
    void checkIfThereIsData();
    void showCurrentVersionSheet();
    void showCellFromSheet();
    void updateSheetCell();
    void showSheetVersionsForDisplay();
    void loadFile();
    void saveCurrentSheetVersionsToFile();
    void loadSystemFromFile();
    void exitProgram();
}