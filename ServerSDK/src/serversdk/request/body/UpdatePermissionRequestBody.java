package serversdk.request.body;

public class UpdatePermissionRequestBody {
    private final String requestUid;
    private final String sheetName;
    private final String newApprovalStatus;

    public UpdatePermissionRequestBody(String requestUid, String sheetName,
                                       String newApprovalStatus) {
        this.requestUid = requestUid;
        this.sheetName = sheetName;
        this.newApprovalStatus = newApprovalStatus;
    }

    public String getRequestUid() {
        return requestUid;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getNewApprovalStatus() {
        return newApprovalStatus;
    }
}
