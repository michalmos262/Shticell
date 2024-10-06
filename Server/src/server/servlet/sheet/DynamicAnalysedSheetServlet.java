package server.servlet.sheet;

import dto.sheet.SheetDto;
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

import java.io.IOException;

import static server.constant.Constants.APPLICATION_JSON;
import static server.constant.Constants.GSON_INSTANCE;
import static serversdk.request.parameter.RequestParameters.*;

@WebServlet(name = "DynamicAnalysedSheetServlet", urlPatterns = "/sheet/dynamic-analysed")
public class DynamicAnalysedSheetServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response) && SessionUtils.isInSheet(request, response)) {
                Engine engine = ServletUtils.getEngineInstance(getServletContext());
                String sheetName = SessionUtils.getCurrentSheetName(request);
                String cellPositionParameter = request.getParameter(CELL_POSITION);
                CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(cellPositionParameter);
                double originalValue = Double.parseDouble(request.getParameter(CELL_ORIGINAL_VALUE));
                SheetDto sheetDto = engine.getSheetAfterDynamicAnalysisOfCell(sheetName, cellPositionInSheet, originalValue);
                String json = GSON_INSTANCE.toJson(sheetDto);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }
}
