package dto.user;

import java.util.Objects;

public class PermissionAndApprovalStatusDto {
    UserPermission permission;
    ApprovalStatus approvalStatus;

    public PermissionAndApprovalStatusDto(UserPermission permission, ApprovalStatus approvalStatus) {
        this.permission = permission;
        this.approvalStatus = approvalStatus;
    }

    public UserPermission getPermission() {
        return permission;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionAndApprovalStatusDto that = (PermissionAndApprovalStatusDto) o;
        return getPermission() == that.getPermission() && getApprovalStatus() == that.getApprovalStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPermission(), getApprovalStatus());
    }
}
