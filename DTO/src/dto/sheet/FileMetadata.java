package dto.sheet;

import java.util.Objects;

public class FileMetadata {
    private final String sheetName;
    private final String owner;
    private final String sheetSize;
    private final String yourPermission;

    public FileMetadata(String sheetName, String owner, String sheetSize, String yourPermission) {
        this.sheetName = sheetName;
        this.owner = owner;
        this.sheetSize = sheetSize;
        this.yourPermission = yourPermission;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getOwner() {
        return owner;
    }

    public String getSheetSize() {
        return sheetSize;
    }

    public String getYourPermission() {
        return yourPermission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileMetadata that = (FileMetadata) o;
        return Objects.equals(getSheetName(), that.getSheetName()) && Objects.equals(getOwner(), that.getOwner()) && Objects.equals(getSheetSize(), that.getSheetSize()) && Objects.equals(getYourPermission(), that.getYourPermission());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSheetName(), getOwner(), getSheetSize(), getYourPermission());
    }
}
