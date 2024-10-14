package engine.user.permission;

public class PermissionAndApprovalStatus {
    UserPermission permission;
    ApprovalStatus approvalStatus;

    public PermissionAndApprovalStatus(UserPermission permission, ApprovalStatus approvalStatus) {
        this.permission = permission;
        this.approvalStatus = approvalStatus;
    }

    public UserPermission getPermission() {
        return permission;
    }

    public void setPermission(UserPermission permission) {
        this.permission = permission;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}
