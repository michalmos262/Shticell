package serversdk.request.body;

public class CreatePermissionRequestBody {
    private final String sheetName;
    private final String permission;

    public CreatePermissionRequestBody(String sheetName, String permission) {
        this.sheetName = sheetName;
        this.permission = permission;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getPermission() {
        return permission;
    }
}
