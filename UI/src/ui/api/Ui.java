package ui.api;

public interface Ui {
    void showSheet();
    void showSheetCell(String cellLocation);
    void updateSheetCell(String cellLocation);
    void showSheetVersions();
    void exitProgram();
}