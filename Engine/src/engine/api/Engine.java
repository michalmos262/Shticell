package engine.api;

import engine.entity.cell.Cell;
import engine.entity.cell.CellDto;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.sheet.SheetDimension;

import java.util.List;
import java.util.Map;

public interface Engine {
    String getSheetName();
    int getCurrentSheetVersion();
    SheetDimension getSheetDimension();
    CellDto findCellInSheet(int row, int column, int sheetVersion);
    int getLastCellVersion(int row, int column);
    List<CellPositionInSheet> getDependsOnList(int row, int column, int sheetVersion);
    List<CellPositionInSheet> getInfluencingOnList(int row, int column, int sheetVersion);
    void updateSheetCell(int row, int column, String newValue);
    Map<Integer, Integer> getSheetVersions();
    CellPositionInSheet getCellPositionInSheet(int row, int column);
    CellPositionInSheet getCellPositionInSheet(String position);
    int parseRowFromPosition(String position);
    int parseColumnFromPosition(String position);
}
