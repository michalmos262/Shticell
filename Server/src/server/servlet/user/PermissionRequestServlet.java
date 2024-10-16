package server.servlet.user;

import engine.api.Engine;
import engine.user.permission.ApprovalStatus;
import engine.user.permission.PermissionAndApprovalStatus;
import engine.user.permission.PermissionRequest;
import engine.user.permission.UserPermission;
import engine.user.usermanager.UserManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.util.ExceptionUtil;
import server.util.ServletUtils;
import server.util.SessionUtils;
import serversdk.request.body.CreatePermissionRequestBody;
import serversdk.request.body.UpdatePermissionRequestBody;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static server.constant.Constants.*;
import static serversdk.request.parameter.RequestParameters.SHEET_NAME;

@WebServlet(name = "PermissionRequestServlet", urlPatterns = "/user/permission-request")
public class PermissionRequestServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response)) {
                String requestBody = ServletUtils.extractRequestBody(request);
                CreatePermissionRequestBody permissionRequestBody = GSON_INSTANCE.fromJson(requestBody,
                        CreatePermissionRequestBody.class);

                synchronized (getServletContext()) {
                    UserManager userManager = ServletUtils.getUserManager(getServletContext());
                    Engine engine = ServletUtils.getEngineInstance(getServletContext());
                    String sheetName = permissionRequestBody.getSheetName();
                    String sheetOwner = engine.getSheetOwner(sheetName);
                    String currentUsername = SessionUtils.getUsername(request);
                    UserPermission userPermission = UserPermission.valueOf(permissionRequestBody.getPermission());

                    String formattedDate = DATE_FORMAT.format(new Date());

                    userManager.addPermissionRequestToOwner(sheetOwner, sheetName,
                            new PermissionRequest(formattedDate, currentUsername, userPermission, ApprovalStatus.PENDING));

                    engine.addUserPermissionToSheet(sheetName, currentUsername, userPermission);
                }
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response)) {
                String requestBody = ServletUtils.extractRequestBody(request);
                UpdatePermissionRequestBody updatePermissionRequestBody = GSON_INSTANCE.fromJson(requestBody,
                        UpdatePermissionRequestBody.class);

                synchronized (getServletContext()) {
                    UserManager userManager = ServletUtils.getUserManager(getServletContext());
                    Engine engine = ServletUtils.getEngineInstance(getServletContext());
                    String owner = SessionUtils.getUsername(request);

                    String sendDate = updatePermissionRequestBody.getRequestSendDate();
                    String requestAsker = updatePermissionRequestBody.getRequestAsker();
                    String sheetName = updatePermissionRequestBody.getSheetName();
                    String newApprovalStatus = updatePermissionRequestBody.getNewApprovalStatus();

                    PermissionRequest updatedPermissionRequest = userManager.setPermissionRequestApprovalStatus(owner, sendDate, requestAsker, sheetName,
                            ApprovalStatus.valueOf(newApprovalStatus));

                    PermissionAndApprovalStatus permissionAndApprovalStatus =
                            new PermissionAndApprovalStatus(updatedPermissionRequest.getPermission(),
                                    ApprovalStatus.valueOf(newApprovalStatus));

                    engine.setUserApprovalStatusInSheet(sheetName, requestAsker, permissionAndApprovalStatus);

                    if (ApprovalStatus.valueOf(newApprovalStatus) == ApprovalStatus.APPROVED) {
                        String userPermissionStr = engine.getSheetPermissions(sheetName)
                                .getUsername2permissionAndApprovalStatus().get(requestAsker).getPermission().toString();
                        userManager.setUserSheetPermission(requestAsker, sheetName, userPermissionStr);
                    }
                }
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response)) {
                String sheetName = request.getParameter(SHEET_NAME);
                String owner = SessionUtils.getUsername(request);
                List<PermissionRequest> permissionRequests;

                synchronized (getServletContext()) {
                    UserManager userManager = ServletUtils.getUserManager(getServletContext());
                    permissionRequests = userManager.getPermissionRequestsFromOwner(owner, sheetName);
                }
                String json = GSON_INSTANCE.toJson(permissionRequests);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }
}