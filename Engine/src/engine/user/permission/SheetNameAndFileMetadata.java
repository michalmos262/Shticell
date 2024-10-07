package engine.user.permission;

import dto.sheet.FileMetadata;

import java.util.HashMap;
import java.util.Map;

public class SheetNameAndFileMetadata {
    private final Map<String, FileMetadata> sheetName2fileMetadata;

    public SheetNameAndFileMetadata() {
        sheetName2fileMetadata = new HashMap<>();
    }

    public void setSheetNameAndFileMetadata(FileMetadata fileMetadata) {
        sheetName2fileMetadata.put(fileMetadata.getSheetName(), fileMetadata);
    }

    public FileMetadata getFileMetadata(String sheetName) {
        return sheetName2fileMetadata.get(sheetName);
    }
}