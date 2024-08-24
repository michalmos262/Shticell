package engine.api;

import engine.entity.dto.CellDto;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.sheet.impl.SheetDimension;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Engine {
    String SUPPORTED_FILE_TYPE = "xml";

    boolean isDataLoaded();
    String getSheetName();
    int getCurrentSheetVersion();
    CellDto findCellInSheet(int row, int column, int sheetVersion);
    int getLastCellVersion(int row, int column);
    List<CellPositionInSheet> getInfluencedByList(int row, int column, int sheetVersion);
    List<CellPositionInSheet> getInfluencesList(int row, int column, int sheetVersion);
    void updateSheetCell(int row, int column, String newValue) throws Exception;
    Map<Integer, Integer> getSheetVersions();
    void validateSheetVersionExists(int version);
    CellPositionInSheet getCellPositionInSheet(int row, int column);
    CellPositionInSheet getCellPositionInSheet(String position);
    void writeSheetManagerToFile(String fileName) throws IOException;
    void readSheetManagerFromFile(String fileName);
    void loadFile(String fileName) throws Exception;
}