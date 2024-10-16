package dto.sheet;

import dto.user.PermissionAndApprovalStatusDto;

import java.util.Map;
import java.util.Objects;

public class SheetPermissionsDto {
    Map<String, PermissionAndApprovalStatusDto> username2permissionAndApprovalStatus;

    public SheetPermissionsDto(Map<String, PermissionAndApprovalStatusDto> username2permissionAndApprovalStatus) {
        this.username2permissionAndApprovalStatus = username2permissionAndApprovalStatus;
    }

    public Map<String, PermissionAndApprovalStatusDto> getUsername2permissionAndApprovalStatus() {
        return username2permissionAndApprovalStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SheetPermissionsDto that = (SheetPermissionsDto) o;
        return Objects.equals(getUsername2permissionAndApprovalStatus(), that.getUsername2permissionAndApprovalStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUsername2permissionAndApprovalStatus());
    }
}
