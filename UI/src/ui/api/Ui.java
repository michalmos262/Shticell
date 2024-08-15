package ui.api;

public interface Ui {
    String getCellPositionFromUser();
    void showSheet(int version);
    void showSheetCell();
    void updateSheetCell();
    void showSheetVersions();
    void exitProgram();
}