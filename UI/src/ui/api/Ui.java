package ui.api;

import java.io.IOException;

public interface Ui {
    void checkIfThereIsData();
    void showCurrentVersionSheet();
    void showSheetCell();
    void updateSheetCell() throws Exception;
    void showSheetVersionsForDisplay();
    void loadFile();
    void saveCurrentSheetVersionsToFile() throws IOException;
    void loadSheetVersionsFromFile();
    void exitProgram();
}