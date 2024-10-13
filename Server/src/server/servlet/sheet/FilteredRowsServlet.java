package server.servlet.sheet;

import dto.sheet.RowDto;
import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.range.Range;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.util.ExceptionUtil;
import server.util.ServletUtils;
import server.util.SessionUtils;
import static serversdk.request.parameter.RequestParameters.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static server.constant.Constants.*;

@WebServlet(name = "FilteredRowsServlet", urlPatterns = "/sheet/filtered-rows")
public class FilteredRowsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response) && SessionUtils.isInSheet(request, response)) {
                Engine engine = ServletUtils.getEngineInstance(getServletContext());
                String sheetName = SessionUtils.getCurrentSheetName(request);

                String sheetVersionParameter = request.getParameter(SHEET_VERSION);
                int sheetVersion;
                if (sheetVersionParameter != null) {
                    sheetVersion = Integer.parseInt(sheetVersionParameter.trim());
                } else {
                    sheetVersion = engine.getCurrentSheetVersion(sheetName);
                }

                String fromCellPositionStr = request.getParameter(FROM_CELL_POSITION);
                CellPositionInSheet fromCellPosition = PositionFactory.createPosition(fromCellPositionStr);
                String toCellPositionStr = request.getParameter(TO_CELL_POSITION);
                CellPositionInSheet toCellPosition = PositionFactory.createPosition(toCellPositionStr);
                Range range = new Range(fromCellPosition, toCellPosition);

                Map<String, Set<String>> column2effectiveValuesFilteredBy = getColumnsAndUniqueValuesToFilterBy(request);

                LinkedList<RowDto> filteredRows = engine.getFilteredRowsSheet(sheetName, sheetVersion, range,
                        column2effectiveValuesFilteredBy);

                String json = GSON_INSTANCE.toJson(filteredRows);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }

    private boolean isColumnName(String input) {
        return input != null && input.length() == 1;
    }

    private Map<String, Set<String>> getColumnsAndUniqueValuesToFilterBy(HttpServletRequest request) {
        Map<String, Set<String>> column2effectiveValuesFilteredBy = new HashMap<>();
        List<String> parameterNames = Collections.list(request.getParameterNames());

        parameterNames.forEach((param) -> {
            if (isColumnName(param)) {
                String[] paramValues = request.getParameter(param).split(",");
                Set<String> columnValues = Arrays.stream(paramValues)
                       .map(String::trim) // Optional: Trim spaces around each value
                       .collect(Collectors.toSet());
                column2effectiveValuesFilteredBy.put(param, columnValues);
            }
        });

        return column2effectiveValuesFilteredBy;
    }
}
