package server.servlet.sheet;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.CellDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.util.ExceptionUtil;
import server.util.ServletUtils;
import server.util.SessionUtils;

import java.io.IOException;

import static server.constant.Constants.*;

@WebServlet(name = "CellServlet", urlPatterns = "/sheet/cell")
public class CellServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response) && SessionUtils.isInSheet(request, response)) {
                Engine engine = ServletUtils.getEngineInstance(getServletContext());
                String sheetName = SessionUtils.getCurrentSheetName(request);
                int sheetVersion = Integer.parseInt(request.getParameter(SHEET_VERSION));
                String cellPositionStr = request.getParameter(CELL_POSITION);
                CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellPositionStr);
                CellDto cellDto = engine.findCellInSheet(sheetName, cellPositionInSheet.getRow(),
                        cellPositionInSheet.getColumn(), sheetVersion);
                String json = GSON_INSTANCE.toJson(cellDto);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response) && SessionUtils.isInSheet(request, response)) {
                Engine engine = ServletUtils.getEngineInstance(getServletContext());
                String sheetName = SessionUtils.getCurrentSheetName(request);
                String originalValue = request.getParameter(CELL_ORIGINAL_VALUE);
                String cellPositionStr = request.getParameter(CELL_POSITION);
                CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellPositionStr);
                CellDto cellDto = engine.updateSheetCell(sheetName, cellPositionInSheet.getRow(),
                        cellPositionInSheet.getColumn(), originalValue);
                String json = GSON_INSTANCE.toJson(cellDto);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }
}