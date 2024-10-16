package server.servlet.dashboard;

import dto.user.PermissionRequestDto;
import engine.api.Engine;
import dto.user.ApprovalStatus;
import engine.user.permission.PermissionAndApprovalStatus;
import engine.user.permission.PermissionRequest;
import dto.user.UserPermission;
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
import java.util.List;

import static server.constant.Constants.*;
import static serversdk.request.parameter.RequestParameters.SHEET_NAME;

@WebServlet(name = "PermissionRequestServlet", urlPatterns = "/dashboard/permission-request")
public class PermissionRequestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response)) {
                String sheetName = request.getParameter(SHEET_NAME);
                List<PermissionRequestDto> permissionRequests;

                synchronized (getServletContext()) {
                    UserManager userManager = ServletUtils.getUserManager(getServletContext());
                    Engine engine = ServletUtils.getEngineInstance(getServletContext());
                    String owner = engine.getSheetOwner(sheetName);
                    permissionRequests = userManager.getPermissionRequestsFromOwner(owner, sheetName);
                }
                String json = GSON_INSTANCE.toJson(permissionRequests);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }

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

                    userManager.addPermissionRequestToOwner(sheetOwner, sheetName,
                            new PermissionRequest(currentUsername, userPermission, ApprovalStatus.PENDING));

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

                    String requestUid = updatePermissionRequestBody.getRequestUid();
                    String sheetName = updatePermissionRequestBody.getSheetName();
                    String newApprovalStatus = updatePermissionRequestBody.getNewApprovalStatus();

                    PermissionRequest updatedPermissionRequest = userManager.setPermissionRequestApprovalStatus(owner, requestUid, sheetName,
                            ApprovalStatus.valueOf(newApprovalStatus));

                    PermissionAndApprovalStatus permissionAndApprovalStatus =
                            new PermissionAndApprovalStatus(updatedPermissionRequest.getPermission(),
                                    ApprovalStatus.valueOf(newApprovalStatus));

                    engine.setUserApprovalStatusInSheet(sheetName, updatedPermissionRequest.getRequestUsername(), permissionAndApprovalStatus);

                    if (ApprovalStatus.valueOf(newApprovalStatus) == ApprovalStatus.APPROVED) {
                        String requestUsername = updatedPermissionRequest.getRequestUsername();
                        String userPermissionStr = engine.getSheetPermissions(sheetName)
                                .getUsername2permissionAndApprovalStatus().get(requestUsername).getPermission().toString();
                        userManager.setUserSheetPermission(requestUsername, sheetName, userPermissionStr);
                    }
                }
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }
}