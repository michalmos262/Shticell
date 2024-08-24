package engine.entity.sheet.impl;

import engine.entity.sheet.api.Sheet;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class SheetManager implements Serializable {
    private final Map<Integer, Sheet> version2sheet;
    private final String name;
    private int currentVersion;
    private final SheetDimension dimension;

    public SheetManager(String name, SheetDimension dimension) {
        currentVersion = 0;
        version2sheet = new LinkedHashMap<>();
        version2sheet.put(1, new SheetImpl());
        this.name = name;
        this.dimension = dimension;
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

    public SheetDimension getDimension() {
        return dimension;
    }

    public void addNewSheet(Sheet sheet) {
        currentVersion++;
        version2sheet.put(currentVersion, sheet);
    }

    public Sheet getSheetByVersion(int version) {
        return version2sheet.get(version);
    }
}
