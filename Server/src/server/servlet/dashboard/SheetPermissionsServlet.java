package server.servlet.dashboard;

import dto.sheet.SheetPermissionsDto;
import engine.api.Engine;
import engine.user.permission.ApprovalStatus;
import engine.user.permission.PermissionAndApprovalStatus;
import engine.user.permission.UserPermission;
import engine.user.usermanager.UserManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.util.ExceptionUtil;
import server.util.ServletUtils;
import server.util.SessionUtils;
import serversdk.request.body.SetSheetPermissionBody;
import serversdk.request.body.SheetPermissionBody;

import java.io.IOException;

import static server.constant.Constants.APPLICATION_JSON;
import static server.constant.Constants.GSON_INSTANCE;
import static serversdk.request.parameter.RequestParameters.SHEET_NAME;

@WebServlet(name = "SheetPermissionsServlet", urlPatterns = "/dashboard/sheet-permissions")
public class SheetPermissionsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response)) {
                SheetPermissionsDto sheetPermissions;

                synchronized (getServletContext()) {
                    Engine engine = ServletUtils.getEngineInstance(getServletContext());
                    String sheetName = request.getParameter(SHEET_NAME);
                    sheetPermissions = engine.getSheetPermissions(sheetName);
                }

                String json = GSON_INSTANCE.toJson(sheetPermissions);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response)) {
                String requestBody = ServletUtils.extractRequestBody(request);
                SheetPermissionBody sheetPermissionBody = GSON_INSTANCE.fromJson(requestBody, SheetPermissionBody.class);
                synchronized (getServletContext()) {
                    Engine engine = ServletUtils.getEngineInstance(getServletContext());
                    engine.addUserPermissionToSheet(sheetPermissionBody.getSheetName(), sheetPermissionBody.getUsername(),
                            UserPermission.valueOf(sheetPermissionBody.getPermission().toUpperCase()));
                }
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }

    //todo: delete doPut???
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        try {
            if (SessionUtils.isAuthorized(request, response)) {
                String requestBody = ServletUtils.extractRequestBody(request);
                SetSheetPermissionBody sheetPermissionBody = GSON_INSTANCE.fromJson(requestBody, SetSheetPermissionBody.class);
                synchronized (getServletContext()) {
                    Engine engine = ServletUtils.getEngineInstance(getServletContext());
                    UserManager userManager = ServletUtils.getUserManager(getServletContext());

                    String sheetName = sheetPermissionBody.getSheetName();
                    UserPermission permission = UserPermission.valueOf(sheetPermissionBody.getPermission());
                    ApprovalStatus approvalStatusDto = ApprovalStatus.valueOf(sheetPermissionBody.getApprovalStatus().toUpperCase());
                    PermissionAndApprovalStatus permissionAndApprovalStatus =
                            new PermissionAndApprovalStatus(permission, approvalStatusDto);
                    String username = sheetPermissionBody.getUsername();

                    engine.setUserApprovalStatusInSheet(sheetName, username, permissionAndApprovalStatus);

                    if (approvalStatusDto == ApprovalStatus.APPROVED) {
                        String userPermissionStr = engine.getSheetPermissions(sheetName)
                                .getUsername2permissionAndApprovalStatus().get(username).getPermission().toString();
                        userManager.setUserSheetPermission(username, sheetName, userPermissionStr);
                    }
                }
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }
}