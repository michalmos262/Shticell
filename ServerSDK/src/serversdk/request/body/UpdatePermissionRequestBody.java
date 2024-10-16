package serversdk.request.body;

public class UpdatePermissionRequestBody {
    private final String requestSendDate;
    private final String requestAsker;
    private final String sheetName;
    private final String newApprovalStatus;

    public UpdatePermissionRequestBody(String requestSendDate, String requestAsker, String sheetName,
                                       String newApprovalStatus) {
        this.requestSendDate = requestSendDate;
        this.requestAsker = requestAsker;
        this.sheetName = sheetName;
        this.newApprovalStatus = newApprovalStatus;
    }

    public String getRequestSendDate() {
        return requestSendDate;
    }

    public String getRequestAsker() {
        return requestAsker;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getNewApprovalStatus() {
        return newApprovalStatus;
    }
}
