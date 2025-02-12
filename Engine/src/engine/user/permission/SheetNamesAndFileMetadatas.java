package engine.user.permission;

import dto.sheet.FileMetadata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SheetNamesAndFileMetadatas that = (SheetNamesAndFileMetadatas) o;
        return Objects.equals(getSheetName2fileMetadata(), that.getSheetName2fileMetadata());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getSheetName2fileMetadata());
    }
}