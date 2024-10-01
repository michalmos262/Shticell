package engine.entity.sheet;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.range.RangesManager;
import engine.entity.sheet.api.Sheet;
import engine.entity.sheet.impl.SheetImpl;
import engine.exception.cell.CellPositionOutOfSheetBoundsException;

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

    public SheetManager(SheetDimension sheetDimension) {
        currentVersion = 0;
        version2sheet = new LinkedHashMap<>();
        version2sheet.put(1, new SheetImpl(this));
        this.sheetDimension = sheetDimension;
        rangesManager = new RangesManager();
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

    public SheetDimension getSheetDimension() {
        return sheetDimension;
    }

    public void addNewSheet(Sheet sheet) {
        currentVersion++;
        version2sheet.put(currentVersion, sheet);
    }

    public Sheet getSheetByVersion(int version) {
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

    public void createRange(String name, CellPositionInSheet fromPosition, CellPositionInSheet toPosition) {
        validatePositionInSheetBounds(fromPosition);
        validatePositionInSheetBounds(toPosition);

        rangesManager.createRange(name, fromPosition, toPosition);
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