package engine.user.usermanager;

import dto.user.SheetNameAndFileMetadataDto;
import engine.exception.user.UserAlreadyExistsException;
import engine.exception.user.UserDoesNotExistException;
import engine.user.permission.SheetNameAndFileMetadata;

import java.util.*;

public class UserManager {
    private final Map<String, SheetNameAndFileMetadata> userName2sheetPermissions;

    public UserManager() {
        userName2sheetPermissions = new HashMap<>();
    }

    public synchronized void addUser(String username) {
        if (isUserExists(username)) {
            throw new UserAlreadyExistsException(username);
        }
        userName2sheetPermissions.put(username, new SheetNameAndFileMetadata());
    }

    public synchronized void removeUser(String username) {
        userName2sheetPermissions.remove(username);
    }

    public synchronized SheetNameAndFileMetadata getUserSheetPermissions(String username) {
        if (!isUserExists(username)) {
            throw new UserDoesNotExistException(username);
        }
        return userName2sheetPermissions.get(username);
    }

    public synchronized SheetNameAndFileMetadataDto getUserSheetPermissionsDto(String username) {
        SheetNameAndFileMetadata sheetPermissions = getUserSheetPermissions(username);
        return new SheetNameAndFileMetadataDto(sheetPermissions.getSheetName2fileMetadata());
    }

    public synchronized Map<String, SheetNameAndFileMetadata> getUserName2sheetPermissions() {
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