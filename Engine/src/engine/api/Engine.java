package engine.api;

import engine.entity.cell.EffectiveValue;
import engine.entity.dto.CellDto;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.dto.RowDto;
import engine.entity.dto.SheetDto;
import engine.entity.range.Range;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Engine {
    String SUPPORTED_FILE_TYPE = "xml";

    boolean isDataLoaded();
    int getCurrentSheetVersion(String sheetName);
    CellDto findCellInSheet(String sheetName, int row, int column, int sheetVersion);
    SheetDto getSheet(String sheetName, int sheetVersion);
    int getLastCellVersion(String sheetName, int row, int column);
    Set<CellPositionInSheet> getInfluencedBySet(String sheetName, int row, int column, int sheetVersion);
    Set<CellPositionInSheet> getInfluencesSet(String sheetName, int row, int column, int sheetVersion);
    CellDto updateSheetCell(String sheetName, int row, int column, String newValue);
    CellPositionInSheet getCellPositionInSheet(String sheetName, int row, int column);
    CellPositionInSheet getCellPositionInSheet(String sheetName, String position);
    String loadFile(String fileName) throws Exception;
    String loadFile(InputStream fileInputStream) throws Exception;
    int getFilesAmount();
    int getNumOfSheetRows(String sheetName);
    int getNumOfSheetColumns(String sheetName);
    int getSheetRowHeight(String sheetName);
    int getSheetColumnWidth(String sheetName);
    EffectiveValue getEffectiveValueForDisplay(EffectiveValue originalEffectiveValue);
    Range getRangeByName(String sheetName, String rangeName);
    List<String> getRangeNames(String sheetName);
    Range createRange(String sheetName, String rangeName, CellPositionInSheet fromPosition, CellPositionInSheet toPosition);
    void deleteRange(String sheetName, String rangeName);
    LinkedList<RowDto> getSortedRowsSheet(String sheetName, Range rangeToSort, Set<String> columnsSortedBy);
    Map<String, Set<EffectiveValue>> getUniqueColumnValuesByRange(String sheetName, Range range, Set<String> columns);
    LinkedList<RowDto> getFilteredRowsSheet(String sheetName, Range rangeToFilter, Map<String, Set<String>> column2effectiveValuesFilteredBy);
    SheetDto getSheetAfterDynamicAnalysisOfCell(String sheetName, CellPositionInSheet cellPosition, double cellOriginalValue);
}