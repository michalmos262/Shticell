package ui.api;

import engine.impl.entities.CellPositionInSheet;

public interface Ui {
    CellPositionInSheet getCellPositionFromUser();
    void showSheet();
    void showSheetCell();
    void updateSheetCell();
    void showSheetVersions();
    void exitProgram();
}