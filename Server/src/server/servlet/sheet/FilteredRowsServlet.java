package server.servlet.sheet;

import dto.RowDto;
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

import java.io.IOException;
import java.util.LinkedList;

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

                String fromCellPositionStr = request.getParameter(FROM_CELL_POSITION);
                CellPositionInSheet fromCellPosition = PositionFactory.createPosition(fromCellPositionStr);
                String toCellPositionStr = request.getParameter(TO_CELL_POSITION);
                CellPositionInSheet toCellPosition = PositionFactory.createPosition(toCellPositionStr);
                Range range = new Range(fromCellPosition, toCellPosition);

                String columns = request.getParameter(SORT_FILTER_BY_COLUMNS);
                //todo: filter rows
//                Set<String> chosenColumns = new LinkedHashSet<>(
//                        Arrays.stream(columns.split(","))
//                                .map(String::trim)
//                                .toList()
//                );
//
//                String uniqueEffectiveValues = request.getParameter(FILTER_BY_UNIQUE_EFFECTIVE_VALUES);
//                Map<String, Set<String>> column2effectiveValuesFilteredBy = new HashMap<>();
//
//
//                LinkedList<RowDto> filteredRows = engine.getFilteredRowsSheet(sheetName, range,
//                        column2effectiveValuesFilteredBy);
//                String json = GSON_INSTANCE.toJson(filteredRows);
//                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }
}