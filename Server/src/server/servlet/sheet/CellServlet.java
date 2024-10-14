package server.servlet.sheet;

import dto.cell.CellDto;
import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.util.ExceptionUtil;
import server.util.ServletUtils;
import server.util.SessionUtils;
import serversdk.request.body.EditCellBody;

import java.io.IOException;

import static server.constant.Constants.*;
import static serversdk.request.parameter.RequestParameters.*;

@WebServlet(name = "CellServlet", urlPatterns = "/sheet/cell")
public class CellServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response) && SessionUtils.isInSheet(request, response)) {
                CellDto cellDto;

                synchronized (getServletContext()) {
                    Engine engine = ServletUtils.getEngineInstance(getServletContext());
                    String sheetName = SessionUtils.getCurrentSheetName(request);
                    String sheetVersionParameter = request.getParameter(SHEET_VERSION);
                    int sheetVersion;

                    if (sheetVersionParameter == null) {
                        sheetVersion = engine.getCurrentSheetVersion(sheetName);
                    } else {
                        sheetVersion = Integer.parseInt(sheetVersionParameter);
                    }

                    String cellPositionStr = request.getParameter(CELL_POSITION);
                    CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellPositionStr);
                    cellDto = engine.findCellInSheet(sheetName, cellPositionInSheet.getRow(),
                            cellPositionInSheet.getColumn(), sheetVersion);
                }

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
                CellDto cellDto;
                String cellPositionStr = request.getParameter(CELL_POSITION);
                String requestBody = ServletUtils.extractRequestBody(request);
                EditCellBody editCellBody = GSON_INSTANCE.fromJson(requestBody, EditCellBody.class);
                String originalValue = editCellBody.getOriginalValue();

                synchronized (getServletContext()) {
                    Engine engine = ServletUtils.getEngineInstance(getServletContext());
                    String sheetName = SessionUtils.getCurrentSheetName(request);

                    CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellPositionStr);
                    cellDto = engine.updateSheetCell(sheetName, cellPositionInSheet.getRow(),
                            cellPositionInSheet.getColumn(), originalValue, SessionUtils.getUsername(request));
                }

                String json = GSON_INSTANCE.toJson(cellDto);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }
}