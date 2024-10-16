package engine.user.permission;

import java.util.Objects;

public class PermissionRequest {
    private final String sendDate;
    private final String asker;
    private final UserPermission permission;
    private ApprovalStatus currentApprovalStatus;

    public PermissionRequest(String sendDate, String asker, UserPermission permission, ApprovalStatus currentApprovalStatus) {
        this.sendDate = sendDate;
        this.asker = asker;
        this.permission = permission;
        this.currentApprovalStatus = currentApprovalStatus;
    }

    public String getSendDate() {
        return sendDate;
    }

    public String getAsker() {
        return asker;
    }

    public UserPermission getPermission() {
        return permission;
    }

    public ApprovalStatus getCurrentApprovalStatus() {
        return currentApprovalStatus;
    }

    public void setCurrentApprovalStatus(ApprovalStatus currentApprovalStatus) {
        this.currentApprovalStatus = currentApprovalStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionRequest that = (PermissionRequest) o;
        return Objects.equals(getSendDate(), that.getSendDate()) && Objects.equals(getAsker(), that.getAsker()) && getPermission() == that.getPermission() && getCurrentApprovalStatus() == that.getCurrentApprovalStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSendDate(), getAsker(), getPermission(), getCurrentApprovalStatus());
    }
}