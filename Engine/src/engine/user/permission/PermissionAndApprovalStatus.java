package engine.user.permission;

import dto.user.ApprovalStatus;
import dto.user.UserPermission;

public class PermissionAndApprovalStatus {
    UserPermission permission;
    ApprovalStatus approvalStatus;

    public PermissionAndApprovalStatus(UserPermission permission, ApprovalStatus approvalStatus) {
        setPermissionAndApprovalStatus(permission, approvalStatus);
    }

    public UserPermission getPermission() {
        return permission;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setPermissionAndApprovalStatus(UserPermission permission, ApprovalStatus approvalStatus) {
        this.permission = permission;
        this.approvalStatus = approvalStatus;
    }
}