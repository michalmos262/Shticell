package engine.entity.sheet;

import engine.user.permission.PermissionAndApprovalStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SheetPermissions {
    Map<String, PermissionAndApprovalStatus> username2permissionAndApprovalStatus;
    
    public SheetPermissions() {
        username2permissionAndApprovalStatus = new HashMap<>();
    }
    
    public void addUserPermission(String username, PermissionAndApprovalStatus permissionAndApprovalStatus) {
        username2permissionAndApprovalStatus.put(username, permissionAndApprovalStatus);
    }
    
    public void setUserApprovalStatus(String username, PermissionAndApprovalStatus permissionAndApprovalStatus) {
        username2permissionAndApprovalStatus.get(username).setPermissionAndApprovalStatus(
                permissionAndApprovalStatus.getPermission(), permissionAndApprovalStatus.getApprovalStatus());
    }

    public Map<String, PermissionAndApprovalStatus> getUsername2permissionAndApprovalStatus() {
        return Collections.unmodifiableMap(username2permissionAndApprovalStatus);
    }
}