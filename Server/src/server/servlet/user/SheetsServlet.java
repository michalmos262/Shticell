package server.servlet.user;

import dto.user.SheetNamesAndFileMetadatasDto;
import engine.user.usermanager.UserManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.util.ExceptionUtil;
import server.util.ServletUtils;
import server.util.SessionUtils;

import java.io.IOException;

import static server.constant.Constants.*;
import static server.constant.Constants.GSON_INSTANCE;

@WebServlet(name = "SheetsServlet", urlPatterns = "/user/sheets")
public class SheetsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response)) {
                UserManager userManager = ServletUtils.getUserManager(getServletContext());
                SheetNamesAndFileMetadatasDto permissions = userManager.getUserSheetPermissionsDto(
                        SessionUtils.getUsername(request)
                );
                String json = GSON_INSTANCE.toJson(permissions);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }
}
