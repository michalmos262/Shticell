package dto.user;

import dto.sheet.FileMetadata;

import java.util.Map;
import java.util.Objects;

public class SheetNamesAndFileMetadatasDto {
    private final Map<String, FileMetadata> sheetName2fileMetadata;

    public SheetNamesAndFileMetadatasDto(Map<String, FileMetadata> sheetName2fileMetadata) {
        this.sheetName2fileMetadata = sheetName2fileMetadata;
    }

    public Map<String, FileMetadata> getSheetName2fileMetadata() {
        return sheetName2fileMetadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SheetNamesAndFileMetadatasDto that = (SheetNamesAndFileMetadatasDto) o;
        return Objects.equals(getSheetName2fileMetadata(), that.getSheetName2fileMetadata());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getSheetName2fileMetadata());
    }
}