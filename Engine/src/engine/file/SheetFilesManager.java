package engine.file;

import engine.entity.sheet.SheetManager;
import engine.exception.sheet.SheetNameAlreadyExistsException;
import engine.exception.sheet.SheetNameDoesNotExistException;

import java.util.HashMap;
import java.util.Map;

public class SheetFilesManager {
    Map<String, SheetManager> name2sheetManager;

    public SheetFilesManager() {
        name2sheetManager = new HashMap<>();
    }

    public void addSheetManager(String sheetName, SheetManager sheetManager) {
        if (name2sheetManager.containsKey(sheetName)) {
            throw new SheetNameAlreadyExistsException(sheetName);
        }
        name2sheetManager.put(sheetName, sheetManager);
    }

    public SheetManager getSheetManager(String sheetName) {
        if (!name2sheetManager.containsKey(sheetName)) {
            throw new SheetNameDoesNotExistException(sheetName);
        }
        return name2sheetManager.get(sheetName);
    }

    public int getSheetManagersCount() {
        return name2sheetManager.size();
    }
}