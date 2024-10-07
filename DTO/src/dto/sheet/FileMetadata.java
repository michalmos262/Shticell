package dto.sheet;

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
}
