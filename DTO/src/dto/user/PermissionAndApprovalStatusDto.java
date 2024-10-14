package dto.user;

public class PermissionAndApprovalStatusDto {
    UserPermissionDto permission;
    ApprovalStatusDto approvalStatus;

    public PermissionAndApprovalStatusDto(UserPermissionDto permission, ApprovalStatusDto approvalStatus) {
        this.permission = permission;
        this.approvalStatus = approvalStatus;
    }

    public UserPermissionDto getPermission() {
        return permission;
    }

    public ApprovalStatusDto getApprovalStatus() {
        return approvalStatus;
    }
}
