package dto.sheet;

public class FileMetadata {
    private final String sheetName;
    private final String owner;
    private final String sheetSize;

    public FileMetadata(String sheetName, String owner, String sheetSize) {
        this.sheetName = sheetName;
        this.owner = owner;
        this.sheetSize = sheetSize;
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
}
