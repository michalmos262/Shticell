package engine.user.usermanager;

import dto.user.SheetNamesAndFileMetadatasDto;
import engine.exception.user.UserAlreadyExistsException;
import engine.exception.user.UserDoesNotExistException;
import engine.user.permission.SheetNamesAndFileMetadatas;

import java.util.*;

public class UserManager {
    private final Map<String, SheetNamesAndFileMetadatas> userName2sheetPermissions;

    public UserManager() {
        userName2sheetPermissions = new HashMap<>();
    }

    public synchronized void addUser(String username) {
        if (isUserExists(username)) {
            throw new UserAlreadyExistsException(username);
        }
        userName2sheetPermissions.put(username, new SheetNamesAndFileMetadatas());
    }

    public synchronized void removeUser(String username) {
        userName2sheetPermissions.remove(username);
    }

    public synchronized SheetNamesAndFileMetadatas getUserSheetPermissions(String username) {
        if (!isUserExists(username)) {
            throw new UserDoesNotExistException(username);
        }
        return userName2sheetPermissions.get(username);
    }

    public synchronized SheetNamesAndFileMetadatasDto getUserSheetPermissionsDto(String username) {
        SheetNamesAndFileMetadatas sheetPermissions = getUserSheetPermissions(username);
        return new SheetNamesAndFileMetadatasDto(sheetPermissions.getSheetName2fileMetadata());
    }

    public synchronized Map<String, SheetNamesAndFileMetadatas> getUserName2sheetPermissions() {
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