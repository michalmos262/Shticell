package engine.api;

import engine.entity.dto.CellDto;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.dto.SheetDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Engine {
    String SUPPORTED_FILE_TYPE = "xml";
    String SYSTEM_FILE_TYPE = "shticell";

    boolean isDataLoaded();
    String getSheetName();
    int getCurrentSheetVersion();
    CellDto findCellInSheet(int row, int column, int sheetVersion);
    SheetDto getSheet(int sheetVersion);
    int getLastCellVersion(int row, int column);
    List<CellPositionInSheet> getInfluencedByList(int row, int column, int sheetVersion);
    List<CellPositionInSheet> getInfluencesList(int row, int column, int sheetVersion);
    void updateSheetCell(int row, int column, String newValue);
    Map<Integer, Integer> getSheetVersions();
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
}