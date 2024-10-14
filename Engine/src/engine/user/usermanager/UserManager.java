package engine.user.usermanager;

import dto.sheet.FileMetadata;
import dto.user.SheetNamesAndFileMetadatasDto;
import engine.exception.user.UserAlreadyExistsException;
import engine.exception.user.UserDoesNotExistException;
import engine.user.permission.SheetNamesAndFileMetadatas;

import java.util.*;

public class UserManager {
    private final Map<String, SheetNamesAndFileMetadatas> username2sheetNamesAndFileMetadatas;

    public UserManager() {
        username2sheetNamesAndFileMetadatas = new HashMap<>();
    }

    public synchronized void addUser(String username) {
        if (isUserExists(username)) {
            throw new UserAlreadyExistsException(username);
        }
        username2sheetNamesAndFileMetadatas.put(username, new SheetNamesAndFileMetadatas());
    }

    public synchronized void removeUser(String username) {
        username2sheetNamesAndFileMetadatas.remove(username);
    }

    public synchronized SheetNamesAndFileMetadatas getUserSheetPermissions(String username) {
        if (!isUserExists(username)) {
            throw new UserDoesNotExistException(username);
        }
        return username2sheetNamesAndFileMetadatas.get(username);
    }

    public synchronized SheetNamesAndFileMetadatasDto getUserSheetPermissionsDto(String username) {
        SheetNamesAndFileMetadatas sheetPermissions = getUserSheetPermissions(username);
        return new SheetNamesAndFileMetadatasDto(sheetPermissions.getSheetName2fileMetadata());
    }

    public synchronized Map<String, SheetNamesAndFileMetadatas> getUsername2sheetNamesAndFileMetadatas() {
        return Collections.unmodifiableMap(username2sheetNamesAndFileMetadatas);
    }

    public synchronized void setUserSheetPermission(String username, String sheetName, String newPermission) {
        FileMetadata fileMetadata = getUserSheetPermissions(username).getSheetName2fileMetadata().get(sheetName);

        FileMetadata updatedFileMetadata = new FileMetadata(sheetName, fileMetadata.getOwner(),
                fileMetadata.getSheetSize(), newPermission);

        getUserSheetPermissions(username).setSheetNameAndFileMetadata(updatedFileMetadata);
    }

    public boolean isUserExists(String username) {
        boolean usernameExists = false;
        for (String key : username2sheetNamesAndFileMetadatas.keySet()) {
            if (key.equalsIgnoreCase(username)) {
                usernameExists = true;
                break;
            }
        }
        return usernameExists;
    }
}