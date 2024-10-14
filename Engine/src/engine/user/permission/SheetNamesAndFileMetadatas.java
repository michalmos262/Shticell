package engine.user.permission;

import dto.sheet.FileMetadata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SheetNamesAndFileMetadatas {
    private final Map<String, FileMetadata> sheetName2fileMetadata;

    public SheetNamesAndFileMetadatas() {
        sheetName2fileMetadata = new HashMap<>();
    }

    public void setSheetNameAndFileMetadata(FileMetadata fileMetadata) {
        sheetName2fileMetadata.put(fileMetadata.getSheetName(), fileMetadata);
    }

    public Map<String, FileMetadata> getSheetName2fileMetadata() {
        return Collections.unmodifiableMap(sheetName2fileMetadata);
    }
}