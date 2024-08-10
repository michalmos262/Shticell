package engine.impl;

import engine.api.Engine;
import engine.impl.entities.Sheet;

public class ShticellEngine implements Engine {
    Sheet sheet;

    public ShticellEngine(String sheetName, int numOfRows, int numOfColumns, int rowHeight, int columnWidth) {
        sheet = new Sheet(sheetName, numOfRows, numOfColumns, rowHeight, columnWidth);
    }

    @Override
    public Sheet getSheet() {
        return sheet;
    }
}