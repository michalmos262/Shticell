package engine.user.usermanager;

import dto.sheet.FileMetadata;
import dto.user.PermissionRequestDto;
import dto.user.SheetNamesAndFileMetadatasDto;
import engine.exception.user.UserAlreadyExistsException;
import engine.exception.user.UserDoesNotExistException;
import dto.user.ApprovalStatus;
import engine.user.permission.PermissionRequest;
import engine.user.permission.SheetNameAndPermissionRequests;
import engine.user.permission.SheetNamesAndFileMetadatas;

import java.util.*;

public class UserManager {
    private final Map<String, SheetNamesAndFileMetadatas> username2sheetNamesAndFileMetadatas;
    private final Map<String, SheetNameAndPermissionRequests> owner2sheetNameAndPermissionRequests;
    private final Map<String, Boolean> username2isLoggedIn;

    public UserManager() {
        username2sheetNamesAndFileMetadatas = new HashMap<>();
        owner2sheetNameAndPermissionRequests = new HashMap<>();
        username2isLoggedIn = new HashMap<>();
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

    public synchronized void addUser(String username) {
        // user exists, check if you can log in
        if (isUserExists(username)) {
            throw new UserAlreadyExistsException(username);
        }
        username2sheetNamesAndFileMetadatas.put(username, new SheetNamesAndFileMetadatas());
        username2isLoggedIn.put(username, true);
    }

    public synchronized String loginUserAndGetOriginalUsername(String username) {
        // username already logged in
        for (String usernameKey : username2isLoggedIn.keySet()) {
            if (usernameKey.equalsIgnoreCase(username)) {
                if (username2isLoggedIn.get(usernameKey)) {
                    throw new UserAlreadyExistsException(username);
                } else {
                    // username is not logged in anymore, can log in again
                    username2isLoggedIn.put(usernameKey, true);
                    return usernameKey;
                }
            }
        }
        return null;
    }

    public synchronized void logoutUser(String username) {
        if (!isUserExists(username)) {
            throw new UserDoesNotExistException(username);
        }
        username2isLoggedIn.put(username, false);
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

    public synchronized void addPermissionRequestToOwner(String owner, String sheetName, PermissionRequest permissionRequest) {
        if (!owner2sheetNameAndPermissionRequests.containsKey(owner)) {
            owner2sheetNameAndPermissionRequests.put(owner, new SheetNameAndPermissionRequests());
        }
        owner2sheetNameAndPermissionRequests.get(owner).addPermissionRequest(sheetName, permissionRequest);
    }

    public synchronized PermissionRequest setPermissionRequestApprovalStatus(String owner, String requestUid, String sheetName, ApprovalStatus newApprovalStatus) {
        if (!owner2sheetNameAndPermissionRequests.containsKey(owner)) {
            throw new UserDoesNotExistException(owner);
        }

        Optional<PermissionRequest> result = owner2sheetNameAndPermissionRequests.get(owner).getPermissionRequests(sheetName).stream()
                .filter(permissionRequest -> permissionRequest.getRequestUid().equals(requestUid))
                .findFirst();

        result.ifPresent(permissionRequest -> permissionRequest.setCurrentApprovalStatus(newApprovalStatus));

        return result.orElse(null);
    }

    public synchronized List<PermissionRequestDto> getPermissionRequestsFromOwner(String owner, String sheetName) {
        SheetNameAndPermissionRequests sheetNameAndPermissionRequests = owner2sheetNameAndPermissionRequests.get(owner);
        if (sheetNameAndPermissionRequests != null) {
            List<PermissionRequest> permissionRequests = sheetNameAndPermissionRequests.getPermissionRequests(sheetName);
            List<PermissionRequestDto> permissionRequestsDto = new LinkedList<>();
            for (PermissionRequest permissionRequest : permissionRequests) {
                permissionRequestsDto.add(new PermissionRequestDto(permissionRequest.getRequestUsername(),
                        permissionRequest.getPermission(), permissionRequest.getCurrentApprovalStatus(),
                        permissionRequest.getRequestUid()
                ));
            }
            return permissionRequestsDto;
        }
        return null;
    }
}