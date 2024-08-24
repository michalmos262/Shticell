package ui.api;

public interface Ui {
    void checkIfThereIsData();
    void showCurrentVersionSheet();
    void showSheetCell();
    void updateSheetCell() throws Exception;
    void showSheetVersionsForDisplay();
    void loadFile();
    void saveCurrentSheetVersionsToFile();
    void loadSheetVersionsFromFile();
    void exitProgram();
}