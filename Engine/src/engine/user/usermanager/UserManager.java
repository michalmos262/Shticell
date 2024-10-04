package engine.user.usermanager;

import engine.exception.user.UserAlreadyExistsException;
import engine.exception.user.UserDoesNotExistException;
import engine.user.permission.SheetNameAndPermission;

import java.util.*;

public class UserManager {
    private final Map<String, SheetNameAndPermission> userName2sheetPermissions;

    public UserManager() {
        userName2sheetPermissions = new HashMap<>();
    }

    public synchronized void addUser(String username) {
        if (isUserExists(username)) {
            throw new UserAlreadyExistsException(username);
        }
        userName2sheetPermissions.put(username, new SheetNameAndPermission());
    }

    public synchronized void removeUser(String username) {
        userName2sheetPermissions.remove(username);
    }

    public synchronized SheetNameAndPermission getUserSheetPermissions(String username) {
        if (!isUserExists(username)) {
            throw new UserDoesNotExistException(username);
        }
        return userName2sheetPermissions.get(username);
    }

    public synchronized Map<String, SheetNameAndPermission> getUserName2sheetPermissions() {
        return Collections.unmodifiableMap(userName2sheetPermissions);
    }

    public boolean isUserExists(String username) {
        boolean usernameExists = false;
        for (String key : userName2sheetPermissions.keySet()) {
            if (key.equalsIgnoreCase(username)) {
                usernameExists = true;
                break;
            }
        }
        return usernameExists;
    }
}