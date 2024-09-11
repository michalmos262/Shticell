package engine.api;

import engine.entity.dto.CellDto;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.dto.SheetDto;
import engine.entity.range.Range;
import engine.entity.sheet.SheetDimension;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Engine {
    String SUPPORTED_FILE_TYPE = "xml";
    String SYSTEM_FILE_TYPE = "shticell";

    boolean isDataLoaded();
    String getSheetName();
    int getCurrentSheetVersion();
    CellDto findCellInSheet(int row, int column, int sheetVersion);
    SheetDto getSheet(int sheetVersion);
    int getLastCellVersion(int row, int column);
    Set<CellPositionInSheet> getInfluencedBySet(int row, int column, int sheetVersion);
    Set<CellPositionInSheet> getInfluencesSet(int row, int column, int sheetVersion);
    CellDto updateSheetCell(int row, int column, String newValue);
    Map<Integer, Integer> getVersion2updatedCellsCount();
    Map<Integer, SheetDto> getVersion2sheet();
    void validateSheetVersionExists(int version);
    CellPositionInSheet getCellPositionInSheet(int row, int column);
    CellPositionInSheet getCellPositionInSheet(String position);
    void writeSystemToFile(String fileName) throws IOException;
    void readSystemFromFile(String fileName);
    void loadFile(String fileName) throws Exception;
    int getNumOfSheetRows();
    int getNumOfSheetColumns();
    int getSheetRowHeight();
    int getSheetColumnWidth();
    SheetDimension getSheetDimension();
    Range getRangeByName(String rangeName);
    List<String> getRangeNames();
    void createRange(String name, CellPositionInSheet fromPosition, CellPositionInSheet toPosition);
    void deleteRange(String name);
    SheetDto getSortedRowsSheet(Range rangeToSort, LinkedHashSet<String> columnsSortedBy);
}