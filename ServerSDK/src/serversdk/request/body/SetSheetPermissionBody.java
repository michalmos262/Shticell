package serversdk.request.body;

public class SetSheetPermissionBody {
    private final String sheetName;
    private final String username;
    private final String permission;
    private final String approvalStatus;

    public SetSheetPermissionBody(String sheetName, String username, String permission, String approvalStatus) {
        this.sheetName = sheetName;
        this.username = username;
        this.permission = permission;
        this.approvalStatus = approvalStatus;
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

    public String getApprovalStatus() {
        return approvalStatus;
    }
}
