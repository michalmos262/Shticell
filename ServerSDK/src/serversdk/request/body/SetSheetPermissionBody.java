package serversdk.request.body;

public class SetSheetPermissionBody {
    private final String sheetName;
    private final String username;
    private final String approvalStatus;

    public SetSheetPermissionBody(String sheetName, String username, String approvalStatus) {
        this.sheetName = sheetName;
        this.username = username;
        this.approvalStatus = approvalStatus;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getUsername() {
        return username;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }
}
