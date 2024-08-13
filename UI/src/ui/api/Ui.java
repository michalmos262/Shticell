package ui.api;

import engine.entity.cell.CellPositionInSheet;

public interface Ui {
    CellPositionInSheet getCellPositionFromUser();
    void showSheet(int version);
    void showSheetCell();
    void updateSheetCell();
    void showSheetVersions();
    void exitProgram();
}