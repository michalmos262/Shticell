package serversdk.request.body;

public class SheetPermissionBody {
    private final String sheetName;
    private final String username;
    private final String permission;

    public SheetPermissionBody(String sheetName, String username, String permission) {
        this.sheetName = sheetName;
        this.username = username;
        this.permission = permission;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getUsername() {
        return username;
    }

    public String getPermission() {
        return permission;
    }
}
