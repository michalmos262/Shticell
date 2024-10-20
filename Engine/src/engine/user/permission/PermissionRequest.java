package engine.user.permission;

import dto.user.ApprovalStatus;
import dto.user.UserPermission;

import java.util.Objects;
import java.util.UUID;

public class PermissionRequest {
    private final String requestUsername;
    private final UserPermission permission;
    private ApprovalStatus currentApprovalStatus;
    private final String requestUid;

    public PermissionRequest(String requestUsername, UserPermission permission, ApprovalStatus currentApprovalStatus) {
        this.requestUsername = requestUsername;
        this.permission = permission;
        this.currentApprovalStatus = currentApprovalStatus;
        this.requestUid = UUID.randomUUID().toString();
    }

    public String getRequestUsername() {
        return requestUsername;
    }

    public UserPermission getPermission() {
        return permission;
    }

    public ApprovalStatus getCurrentApprovalStatus() {
        return currentApprovalStatus;
    }

    public String getRequestUid() {
        return requestUid;
    }

    public void setCurrentApprovalStatus(ApprovalStatus currentApprovalStatus) {
        this.currentApprovalStatus = currentApprovalStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionRequest that = (PermissionRequest) o;
        return Objects.equals(requestUsername, that.requestUsername) && getPermission() == that.getPermission() && getCurrentApprovalStatus() == that.getCurrentApprovalStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestUsername, getPermission(), getCurrentApprovalStatus());
    }
}