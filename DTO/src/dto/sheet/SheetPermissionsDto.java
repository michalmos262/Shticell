package dto.sheet;

import dto.user.PermissionAndApprovalStatusDto;

import java.util.Map;

public class SheetPermissionsDto {
    Map<String, PermissionAndApprovalStatusDto> username2permissionAndApprovalStatus;

    public SheetPermissionsDto(Map<String, PermissionAndApprovalStatusDto> username2permissionAndApprovalStatus) {
        this.username2permissionAndApprovalStatus = username2permissionAndApprovalStatus;
    }

    public Map<String, PermissionAndApprovalStatusDto> getUsername2permissionAndApprovalStatus() {
        return username2permissionAndApprovalStatus;
    }
}
