package server.servlet.login;

import dto.sheet.FileMetadata;
import engine.api.Engine;
import engine.user.usermanager.UserManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.util.ExceptionUtil;
import server.util.ServletUtils;
import server.util.SessionUtils;
import serversdk.request.body.LoginBody;

import java.io.IOException;
import java.util.List;

import static server.constant.Constants.*;
import static serversdk.request.parameter.RequestParameters.*;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.setContentType(APPLICATION_JSON);
            String usernameFromSession = SessionUtils.getUsername(request);
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            if (usernameFromSession == null) {
                //user is not logged in yet
                String requestBody = ServletUtils.extractRequestBody(request);
                LoginBody loginBody = GSON_INSTANCE.fromJson(requestBody, LoginBody.class);
                String username = loginBody.getUsername();

                if (username == null || username.isEmpty()) {
                    //no username in session and no username in parameter
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                } else {
                    //normalize the username value
                    username = username.trim();
                    synchronized (this) {
                        //add the new user to the users list
                        userManager.addUser(username);
                        request.getSession(true).setAttribute(USERNAME, username);
                        response.setStatus(HttpServletResponse.SC_OK);

                        Engine engine = ServletUtils.getEngineInstance(getServletContext());
                        List<FileMetadata> fileMetadataList = engine.getSheetFilesMetadata();

                        for (FileMetadata fileMetadata : fileMetadataList) {
                            userManager.getUserSheetPermissions(username).setSheetNameAndFileMetadata(fileMetadata);
                        }
                    }
                }
            } else {
                //user was already logged in and comes back
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }
}
