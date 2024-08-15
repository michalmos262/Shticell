package engine.api;

import engine.entity.cell.Cell;
import engine.entity.cell.CellDto;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.sheet.Sheet;
import engine.entity.sheet.SheetDto;

import java.util.List;
import java.util.Map;

public interface Engine {
    SheetDto getSheetDto(int version);
    String getSheetName();
    int getCurrentSheetVersion();
    Sheet.Dimension getSheetDimension();
    CellDto findCellInSheet(int row, int column, int sheetVersion);
    int getLastCellVersion(int row, int column);
    List<Cell> getDependsOnList(int row, int column, int sheetVersion);
    List<Cell> getInfluencingOnList(int row, int column, int sheetVersion);
    void updateSheetCell(int row, int column, String newValue);
    Map<Integer, Integer> getSheetVersions();
    CellPositionInSheet getCellPositionInSheet(int row, int column);
    CellPositionInSheet getCellPositionInSheet(String position);
    int parseRowFromPosition(String position);
    int parseColumnFromPosition(String position);
}
