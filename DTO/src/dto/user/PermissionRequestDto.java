package dto.user;

import java.util.Objects;

public class PermissionRequestDto {
    private final String requestUsername;
    private final UserPermission permission;
    private final ApprovalStatus currentApprovalStatus;
    private final String requestUid;

    public PermissionRequestDto(String requestUsername, UserPermission permission, ApprovalStatus currentApprovalStatus, String requestUid) {
        this.requestUsername = requestUsername;
        this.permission = permission;
        this.currentApprovalStatus = currentApprovalStatus;
        this.requestUid = requestUid;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionRequestDto that = (PermissionRequestDto) o;
        return Objects.equals(getRequestUsername(), that.getRequestUsername()) && getPermission() == that.getPermission() && getCurrentApprovalStatus() == that.getCurrentApprovalStatus() && Objects.equals(getRequestUid(), that.getRequestUid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRequestUsername(), getPermission(), getCurrentApprovalStatus(), getRequestUid());
    }
}
