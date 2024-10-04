package server.servlet.sheet;

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
import serversdk.request.body.RangeBody;
import static serversdk.request.parameter.RequestParameters.*;

import java.io.IOException;

import static server.constant.Constants.*;

@WebServlet(name = "RangeServlet", urlPatterns = "/sheet/range")
public class RangeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response) && SessionUtils.isInSheet(request, response)) {
                Engine engine = ServletUtils.getEngineInstance(getServletContext());
                String sheetName = SessionUtils.getCurrentSheetName(request);
                String rangeName = request.getParameter(RANGE_NAME);
                Range range = engine.getRangeByName(sheetName, rangeName);
                String json = GSON_INSTANCE.toJson(range);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response) && SessionUtils.isInSheet(request, response)) {
                Engine engine = ServletUtils.getEngineInstance(getServletContext());
                String sheetName = SessionUtils.getCurrentSheetName(request);

                String requestBody = ServletUtils.extractRequestBody(request);
                RangeBody rangeBody = GSON_INSTANCE.fromJson(requestBody, RangeBody.class);
                String rangeName = rangeBody.getName();
                String fromCellPositionStr = rangeBody.getFromPosition();

                CellPositionInSheet fromCellPosition = PositionFactory.createPosition(fromCellPositionStr);
                String toCellPositionStr = rangeBody.getToPosition();
                CellPositionInSheet toCellPosition = PositionFactory.createPosition(toCellPositionStr);
                Range range = engine.createRange(sheetName, rangeName, fromCellPosition, toCellPosition);

                String json = GSON_INSTANCE.toJson(range);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response) && SessionUtils.isInSheet(request, response)) {
                Engine engine = ServletUtils.getEngineInstance(getServletContext());
                String sheetName = SessionUtils.getCurrentSheetName(request);
                String rangeName = request.getParameter(RANGE_NAME);
                engine.deleteRange(sheetName, rangeName);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }
}
