package server.servlet.sheet;

import dto.sheet.SheetDimensionDto;
import engine.api.Engine;
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
import static serversdk.request.parameter.RequestParameters.SHEET_NAME;

@WebServlet(name = "SheetDimensionServlet", urlPatterns = "/sheet/dimension")
public class SheetDimensionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response)) {
                Engine engine = ServletUtils.getEngineInstance(getServletContext());
                String sheetNameParameter = request.getParameter(SHEET_NAME);
                String sheetName;
                if (sheetNameParameter != null) {
                    sheetName = sheetNameParameter.trim();
                } else {
                    sheetName = SessionUtils.getCurrentSheetName(request);
                }
                SheetDimensionDto sheetDimension = engine.getSheetDimension(sheetName);
                String json = GSON_INSTANCE.toJson(sheetDimension);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }
}
