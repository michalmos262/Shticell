package server.servlet.dashboard;

import dto.SheetDto;
import engine.api.Engine;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import server.util.ExceptionUtil;
import server.util.ServletUtils;
import server.util.SessionUtils;

import java.io.IOException;
import java.io.InputStream;

import static server.constant.Constants.*;

@MultipartConfig
@WebServlet(name = "SheetServlet", urlPatterns = "/dashboard/sheet")
public class SheetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response)) {
                Engine engine = ServletUtils.getEngineInstance(getServletContext());
                String sheetName = request.getParameter(SHEET_NAME).trim();
                int sheetVersion = Integer.parseInt(request.getParameter(SHEET_VERSION));
                SheetDto sheetDto = engine.getSheet(sheetName, sheetVersion);
                String json = GSON_INSTANCE.toJson(sheetDto);
                response.getWriter().println(json);
                request.getSession(true).setAttribute(SHEET_NAME, sheetName);
            }
        }
        catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response)) {
                Engine engine = ServletUtils.getEngineInstance(getServletContext());
                Part filePart = request.getPart(FILE_PART);
                InputStream fileInputStream = filePart.getInputStream();
                String sheetName = engine.loadFile(fileInputStream);
                int numOfRows = engine.getNumOfSheetRows(sheetName);
                int numOfColumns = engine.getNumOfSheetColumns(sheetName);

                String sheetSize = numOfRows + "X" + numOfColumns;
                response.setStatus(HttpServletResponse.SC_OK);

                FileMetadata fileMetadata = new FileMetadata(SessionUtils.getUsername(request), sheetName, sheetSize);
                String json = GSON_INSTANCE.toJson(fileMetadata);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }

    private record FileMetadata(String owner, String sheetName, String sheetSize) { }
}