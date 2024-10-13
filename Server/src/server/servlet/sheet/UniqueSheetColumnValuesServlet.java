package server.servlet.sheet;

import dto.cell.EffectiveValueDto;
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
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static server.constant.Constants.APPLICATION_JSON;
import static server.constant.Constants.GSON_INSTANCE;
import static serversdk.request.parameter.RequestParameters.*;

@WebServlet(name = "UniqueSheetColumnValuesServlet", urlPatterns = "/sheet/unique-column-values")
public class UniqueSheetColumnValuesServlet extends HttpServlet {
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


                Set<String> columns = new LinkedHashSet<>(
                        Arrays.stream(request.getParameter(SORT_OR_FILTER_BY_COLUMNS).split(","))
                                .map(String::trim)
                                .toList()
                );

                Map<String, Set<EffectiveValueDto>> uniqueValuesInColumns =
                        engine.getUniqueColumnValuesByRange(sheetName, sheetVersion, range, columns);

                String json = GSON_INSTANCE.toJson(uniqueValuesInColumns);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }
}
