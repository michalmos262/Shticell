package engine.entity.sheet;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.range.Range;
import engine.entity.range.RangesManager;
import engine.entity.sheet.api.Sheet;
import engine.entity.sheet.impl.SheetImpl;
import engine.exception.cell.CellPositionOutOfSheetBoundsException;
import engine.exception.range.CannotDeleteUsedRangeException;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import static engine.entity.cell.CellPositionInSheet.parseColumn;

public class SheetManager implements Serializable {
    private final Map<Integer, Sheet> version2sheet;
    private final String name;
    private int currentVersion;
    private final SheetDimension sheetDimension;
    private final RangesManager rangesManager;

    public SheetManager(String name, SheetDimension sheetDimension) {
        currentVersion = 0;
        version2sheet = new LinkedHashMap<>();
        version2sheet.put(1, new SheetImpl(this));
        this.name = name;
        this.sheetDimension = sheetDimension;
        rangesManager = new RangesManager();
    }

    public String getName() {
        return name;
    }

    public Map<Integer, Sheet> getVersion2sheet() {
        return version2sheet;
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

    public RangesManager getRangeManager() {
        return rangesManager;
    }

    public void createRange(String name, CellPositionInSheet fromPosition, CellPositionInSheet toPosition) {
        validatePositionInSheetBounds(fromPosition);
        validatePositionInSheetBounds(toPosition);

        rangesManager.createRange(name, fromPosition, toPosition);
    }
}