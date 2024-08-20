package engine.api;

import engine.entity.dto.CellDto;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.sheet.SheetDimension;

import java.util.List;
import java.util.Map;

public interface Engine {
    public static final String SUPPORTED_FILE_TYPE = "xml";

    public boolean isDataLoaded();
    String getSheetName();
    int getCurrentSheetVersion();
    SheetDimension getSheetDimension();
    CellDto findCellInSheet(int row, int column, int sheetVersion);
    int getLastCellVersion(int row, int column);
    List<CellPositionInSheet> getInfluencedByList(int row, int column, int sheetVersion);
    List<CellPositionInSheet> getInfluencesList(int row, int column, int sheetVersion);
    void updateSheetCell(int row, int column, String newValue) throws Exception;
    Map<Integer, Integer> getSheetVersions();
    CellPositionInSheet getCellPositionInSheet(int row, int column);
    CellPositionInSheet getCellPositionInSheet(String position);
    int parseRowFromPosition(String position);
    int parseColumnFromPosition(String position);
    void loadFile(String fileName) throws Exception;
}
