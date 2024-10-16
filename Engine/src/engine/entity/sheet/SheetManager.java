package engine.entity.sheet;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.range.Range;
import engine.entity.range.RangesManager;
import engine.entity.sheet.api.Sheet;
import engine.entity.sheet.impl.SheetImpl;
import engine.exception.cell.CellPositionOutOfSheetBoundsException;
import engine.exception.sheet.SheetVersionDoesNotExistException;
import engine.user.permission.ApprovalStatus;
import engine.user.permission.PermissionAndApprovalStatus;
import engine.user.permission.UserPermission;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static engine.entity.cell.CellPositionInSheet.parseColumn;

public class SheetManager implements Serializable {
    private final Map<Integer, Sheet> version2sheet;
    private int currentVersion;
    private final SheetDimension sheetDimension;
    private final RangesManager rangesManager;
    private final String ownerName;
    private final SheetPermissions sheetPermissions;

    public SheetManager(SheetDimension sheetDimension, String ownerName) {
        this.currentVersion = 0;
        this.version2sheet = new LinkedHashMap<>();
        this.version2sheet.put(1, new SheetImpl(this));
        this.sheetDimension = sheetDimension;
        this.rangesManager = new RangesManager();
        this.ownerName = ownerName;
        this.sheetPermissions = new SheetPermissions();
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

    public SheetDimension getSheetDimension() {
        return sheetDimension;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void addNewSheet(Sheet sheet) {
        currentVersion++;
        version2sheet.put(currentVersion, sheet);
    }

    public Sheet getSheetByVersion(int version) {
        if (!version2sheet.containsKey(version)) {
            throw new SheetVersionDoesNotExistException(version);
        }
        return version2sheet.get(version);
    }

    public RangesManager getRangesManager() {
        return rangesManager;
    }

    public void validatePositionInSheetBounds(CellPositionInSheet cellPosition) {
        int row = cellPosition.getRow();
        int column = cellPosition.getColumn();
        int numOfRows = getSheetDimension().getNumOfRows();
        int numOfColumns = getSheetDimension().getNumOfColumns();

        if (!(row >= 1 && row <= numOfRows && column >= 0 && column <= numOfColumns)) {
            throw new CellPositionOutOfSheetBoundsException(numOfRows, parseColumn(numOfColumns));
        }
    }

    public Range createRange(String name, CellPositionInSheet fromPosition, CellPositionInSheet toPosition) {
        validatePositionInSheetBounds(fromPosition);
        validatePositionInSheetBounds(toPosition);

        return rangesManager.createRange(name, fromPosition, toPosition);
    }

    public void addUserPermission(String username, UserPermission permission) {
        sheetPermissions.addUserPermission(username, new PermissionAndApprovalStatus(permission, ApprovalStatus.PENDING));
    }

    public void setUserPermissionApprovalStatus(String username, PermissionAndApprovalStatus permissionAndApprovalStatus) {
        sheetPermissions.setUserApprovalStatus(username, permissionAndApprovalStatus);
    }

    public SheetPermissions getSheetPermissions() {
        return sheetPermissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SheetManager that = (SheetManager) o;
        return getCurrentVersion() == that.getCurrentVersion() && Objects.equals(version2sheet, that.version2sheet) && Objects.equals(getSheetDimension(), that.getSheetDimension()) && Objects.equals(getRangesManager(), that.getRangesManager());
    }

    @Override
    public int hashCode() {
        return Objects.hash(version2sheet, getCurrentVersion(), getSheetDimension(), getRangesManager());
    }
}