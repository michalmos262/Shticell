package engine.api;

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
    CellDto findCellInSheet(CellPositionInSheet cellPosition, int sheetVersion);
    int getLastCellVersion(CellPositionInSheet cellPosition);
    List<CellPositionInSheet> getAffectedByCellsList(CellPositionInSheet cellPosition, int sheetVersion);
    List<CellPositionInSheet> getAffectedCellsList(CellPositionInSheet cellPosition, int sheetVersion);
    void updateSheetCell(CellPositionInSheet cellPosition, String newValue);
    Map<Integer, Integer> getSheetVersions();
}
