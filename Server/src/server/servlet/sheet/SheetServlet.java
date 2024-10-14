package server.servlet.sheet;

import dto.sheet.SheetDto;
import dto.user.ApprovalStatusDto;
import dto.user.UserPermissionDto;
import engine.api.Engine;
import engine.user.permission.SheetNamesAndFileMetadatas;
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
import java.util.Map;

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
                SheetDto sheetDto;
                String sheetName;
                synchronized (getServletContext()) {
                    Engine engine = ServletUtils.getEngineInstance(getServletContext());

                    // this API is for entering a sheet and getting a sheet version
                    String sheetNameParameter = request.getParameter(SHEET_NAME);
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

                    sheetDto = engine.getSheet(sheetName, sheetVersion);
                }
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
                String currentUsername;
                FileMetadata ownerFileMetadata;

                synchronized (getServletContext()) {
                    UserManager userManager = ServletUtils.getUserManager(getServletContext());
                    Engine engine = ServletUtils.getEngineInstance(getServletContext());
                    Part filePart = request.getPart(FILE_PART);
                    InputStream fileInputStream = filePart.getInputStream();

                    currentUsername = SessionUtils.getUsername(request);
                    FileMetadata fileMetadata = engine.loadFile(fileInputStream, currentUsername);
                    ownerFileMetadata = new FileMetadata(fileMetadata.getSheetName(),
                            fileMetadata.getOwner(), fileMetadata.getSheetSize(), UserPermission.OWNER.toString());

                    userManager.getUserSheetPermissions(currentUsername).setSheetNameAndFileMetadata(ownerFileMetadata);

                    Map<String, SheetNamesAndFileMetadatas> users = userManager.getUsername2sheetNamesAndFileMetadatas();
                    users.forEach((username, permission) -> {
                        if (!username.equals(currentUsername)) {
                            permission.setSheetNameAndFileMetadata(
                                    new FileMetadata(fileMetadata.getSheetName(), fileMetadata.getOwner(),
                                            fileMetadata.getSheetSize(), UserPermission.NONE.toString())
                            );
                        }
                    });

                    engine.addUserPermissionToSheet(fileMetadata.getSheetName(), currentUsername, UserPermissionDto.OWNER);
                    engine.setUserApprovalStatusInSheet(fileMetadata.getSheetName(), currentUsername, ApprovalStatusDto.APPROVED);
                }
                response.setStatus(HttpServletResponse.SC_OK);
                String json = GSON_INSTANCE.toJson(ownerFileMetadata);
                response.getWriter().println(json);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(response, e);
        }
    }
}