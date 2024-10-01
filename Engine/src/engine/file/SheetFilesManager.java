package engine.file;

import engine.entity.sheet.SheetManager;

import java.util.HashMap;
import java.util.Map;

public class SheetFilesManager {
    Map<String, SheetManager> name2sheetManager;

    public SheetFilesManager() {
        name2sheetManager = new HashMap<>();
    }

    public void addSheetManager(String sheetName, SheetManager sheetManager) {
        if (name2sheetManager.containsKey(sheetName)) {
            throw new IllegalArgumentException("Sheet name already exists");
        }
        name2sheetManager.put(sheetName, sheetManager);
    }

    public SheetManager getSheetManager(String sheetName) {
        return name2sheetManager.get(sheetName);
    }

    public int getSheetManagersCount() {
        return name2sheetManager.size();
    }
}