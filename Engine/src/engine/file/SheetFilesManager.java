package engine.file;

import dto.sheet.FileMetadata;
import engine.entity.sheet.SheetManager;
import engine.exception.sheet.SheetNameAlreadyExistsException;
import engine.exception.sheet.SheetNameDoesNotExistException;

import java.util.*;

public class SheetFilesManager {
    Map<String, SheetManager> name2sheetManager;
    List<FileMetadata> fileMetadataList;

    public SheetFilesManager() {
        name2sheetManager = new HashMap<>();
        fileMetadataList = new LinkedList<>();
    }

    public boolean isSheetNameExists(String sheetName) {
        boolean fileExists = false;
        for (String key : name2sheetManager.keySet()) {
            if (key.equalsIgnoreCase(sheetName)) {
                fileExists = true;
                break;
            }
        }
        return fileExists;
    }

    public void addSheetManager(String sheetName, SheetManager sheetManager) {
        if (isSheetNameExists(sheetName)) {
            throw new SheetNameAlreadyExistsException(sheetName);
        }
        name2sheetManager.put(sheetName, sheetManager);
    }

    public void addFileMetadata(FileMetadata fileMetadata) {
        fileMetadataList.add(fileMetadata);
    }

    public SheetManager getSheetManager(String sheetName) {
        if (!name2sheetManager.containsKey(sheetName)) {
            throw new SheetNameDoesNotExistException(sheetName);
        }
        return name2sheetManager.get(sheetName);
    }

    public List<FileMetadata> getFileMetadataList() {
        return Collections.unmodifiableList(fileMetadataList);
    }
}