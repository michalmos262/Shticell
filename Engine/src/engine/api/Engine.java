package engine.api;

import dto.cell.CellDto;
import dto.cell.EffectiveValueDto;
import dto.sheet.*;
import dto.user.ApprovalStatusDto;
import dto.user.UserPermissionDto;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.range.Range;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Engine {
    int getCurrentSheetVersion(String sheetName);
    CellDto findCellInSheet(String sheetName, int row, int column, int sheetVersion);
    SheetDto getSheet(String sheetName, int sheetVersion);
    CellDto updateSheetCell(String sheetName, int row, int column, String newValue, String updatedByName);
    FileMetadata loadFile(InputStream fileInputStream, String owner) throws Exception;
    List<FileMetadata> getSheetFilesMetadata();
    SheetDimensionDto getSheetDimension(String sheetName);
    EffectiveValueDto getEffectiveValueDtoForDisplay(EffectiveValueDto originalEffectiveValue);
    RangeDto getRangeByName(String sheetName, String rangeName);
    RangeDto getUnNamedRange(String sheetName, CellPositionInSheet fromPosition, CellPositionInSheet toPosition);
    List<String> getRangeNames(String sheetName);
    RangeDto createRange(String sheetName, String rangeName, CellPositionInSheet fromPosition, CellPositionInSheet toPosition);
    void deleteRange(String sheetName, String rangeName);
    LinkedList<RowDto> getSortedRowsSheet(String sheetName, int sheetVersion, Range rangeToSort,
                                          Set<String> columnsSortedBy);
    Map<String, Set<EffectiveValueDto>> getUniqueColumnValuesByRange(String sheetName, int sheetVersion,
                                                                     Range range, Set<String> columns);
    LinkedList<RowDto> getFilteredRowsSheet(String sheetName, int sheetVersion, Range rangeToFilter,
                                            Map<String, Set<String>> column2effectiveValuesFilteredBy);
    SheetDto getSheetAfterDynamicAnalysisOfCell(String sheetName, int sheetVersion, CellPositionInSheet cellPosition,
                                                double cellOriginalValue);
    SheetPermissionsDto getSheetPermissions(String sheetName);
    void addUserPermissionToSheet(String sheetName, String username, UserPermissionDto permission);
    void setUserApprovalStatusInSheet(String sheetName, String username, ApprovalStatusDto approvalStatus);
}