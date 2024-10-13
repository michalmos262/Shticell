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

import java.io.IOException;
import java.util.*;

import static server.constant.Constants.*;
import static serversdk.request.parameter.RequestParameters.*;

@WebServlet(name = "SortedRowsServlet", urlPatterns = "/sheet/sorted-rows")
public class SortedRowsServlet extends HttpServlet {
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

                Set<String> chosenColumns = new LinkedHashSet<>(
                        Arrays.stream(columns.split(","))
                                .map(String::trim)
                                .toList()
                );

                String sheetVersionParameter = request.getParameter(SHEET_VERSION);
                int sheetVersion;
                if (sheetVersionParameter != null) {
                    sheetVersion = Integer.parseInt(sheetVersionParameter.trim());
                } else {
                    sheetVersion = engine.getCurrentSheetVersion(sheetName);
                }

                LinkedList<RowDto> sortedRows = engine.getSortedRowsSheet(sheetName, sheetVersion, range, chosenColumns);
                String json = GSON_INSTANCE.toJson(sortedRows);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }
}
