package server.servlet.dashboard;

import engine.api.Engine;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import server.util.ServletUtils;
import server.util.SessionUtils;

import java.io.IOException;
import java.io.InputStream;

@MultipartConfig
@WebServlet(name = "UploadFileServlet", urlPatterns = "/dashboard/file")
public class UploadFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        try {
            String usernameFromSession = SessionUtils.getUsername(request);
            if (usernameFromSession == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                Engine engine = ServletUtils.getEngineInstance(getServletContext());
                Part filePart = request.getPart("file");
                InputStream fileInputStream = filePart.getInputStream();
                engine.loadFile(fileInputStream);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("File uploaded successfully");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(e.getMessage());
        }
    }
}
