package dto.user;

import dto.sheet.FileMetadata;

import java.util.Map;
import java.util.Objects;

public class SheetNameAndFileMetadataDto {
    private final Map<String, FileMetadata> sheetName2fileMetadata;

    public SheetNameAndFileMetadataDto(Map<String, FileMetadata> sheetName2fileMetadata) {
        this.sheetName2fileMetadata = sheetName2fileMetadata;
    }

    public Map<String, FileMetadata> getSheetName2fileMetadata() {
        return sheetName2fileMetadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SheetNameAndFileMetadataDto that = (SheetNameAndFileMetadataDto) o;
        return Objects.equals(getSheetName2fileMetadata(), that.getSheetName2fileMetadata());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getSheetName2fileMetadata());
    }
}