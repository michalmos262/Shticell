package engine.user.permission;

import java.util.*;

public class SheetNameAndPermissionRequests {
    private final Map<String, List<PermissionRequest>> sheetName2permissionRequests;

    public SheetNameAndPermissionRequests() {
        sheetName2permissionRequests = new HashMap<>();
    }

    public void addPermissionRequest(String sheetName, PermissionRequest permissionRequest) {
        sheetName2permissionRequests.computeIfAbsent(sheetName, s -> new LinkedList<>()).add(permissionRequest);
    }

    public List<PermissionRequest> getPermissionRequests(String sheetName) {
        return Collections.unmodifiableList(sheetName2permissionRequests.get(sheetName));
    }

    public Map<String, List<PermissionRequest>> getSheetName2permissionRequests() {
        return Collections.unmodifiableMap(sheetName2permissionRequests);
    }
}