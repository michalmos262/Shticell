package engine.user.permission;

import java.util.HashMap;
import java.util.Map;

public class SheetNameAndPermission {
    private final Map<String, UserPermission> sheetName2permission;

    public SheetNameAndPermission() {
        sheetName2permission = new HashMap<>();
    }

    public void setSheetPermission(String sheetName, UserPermission permission) {
        sheetName2permission.put(sheetName, permission);
    }

    public UserPermission getSheetPermission(String sheetName) {
        return sheetName2permission.get(sheetName);
    }
}