package server.servlet.sheet;

import dto.sheet.SheetDto;
import engine.api.Engine;
import engine.user.usermanager.UserManager;
import engine.user.permission.UserPermission;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import server.util.ExceptionUtil;
import server.util.ServletUtils;
import server.util.SessionUtils;
import dto.sheet.FileMetadata;

import java.io.IOException;
import java.io.InputStream;

import static server.constant.Constants.*;
import static serversdk.request.parameter.RequestParameters.*;

@MultipartConfig
@WebServlet(name = "SheetServlet", urlPatterns = "/sheet")
public class SheetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response)) {
                Engine engine = ServletUtils.getEngineInstance(getServletContext());

                // this API is for entering a sheet and getting a sheet version
                String sheetNameParameter = request.getParameter(SHEET_NAME);
                String sheetName;
                if (sheetNameParameter != null) {
                    sheetName = sheetNameParameter.trim();
                } else {
                    sheetName = SessionUtils.getCurrentSheetName(request);
                    if (!SessionUtils.isInSheet(request, response)) {
                        return;
                    }
                }

                // can choose a sheet version or ignore and get the last version
                String sheetVersionParameter = request.getParameter(SHEET_VERSION);
                int sheetVersion;
                if (sheetVersionParameter != null) {
                    sheetVersion = Integer.parseInt(sheetVersionParameter.trim());
                } else {
                    sheetVersion = engine.getCurrentSheetVersion(sheetName);
                }

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
                UserManager userManager = ServletUtils.getUserManager(getServletContext());
                Engine engine = ServletUtils.getEngineInstance(getServletContext());
                Part filePart = request.getPart(FILE_PART);
                InputStream fileInputStream = filePart.getInputStream();

                String sheetName = engine.loadFile(fileInputStream);
                int numOfRows = engine.getNumOfSheetRows(sheetName);
                int numOfColumns = engine.getNumOfSheetColumns(sheetName);
                String sheetSize = numOfRows + "X" + numOfColumns;
                String username = SessionUtils.getUsername(request);
                userManager.getUserSheetPermissions(username).setSheetPermission(sheetName, UserPermission.OWNER);

                response.setStatus(HttpServletResponse.SC_OK);

                FileMetadata fileMetadata = new FileMetadata(sheetName, SessionUtils.getUsername(request), sheetSize);
                String json = GSON_INSTANCE.toJson(fileMetadata);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }
}