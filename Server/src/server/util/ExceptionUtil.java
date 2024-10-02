package server.util;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.NoSuchElementException;

import static server.constant.Constants.GSON_INSTANCE;

public class ExceptionUtil {
    public static void handleException(HttpServletResponse response, Exception e) throws IOException {
        // Log the exception for debugging
        e.fillInStackTrace();

        // Set the response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Set error code
        if (e instanceof IllegalArgumentException ||
                e instanceof NoSuchElementException ||
                e instanceof IndexOutOfBoundsException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else if (e instanceof NullPointerException) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        // Write the error response as JSON
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        response.getWriter().println(GSON_INSTANCE.toJson(errorResponse));
        response.getWriter().flush(); // Ensure the response is sent
    }

    private record ErrorResponse(String error) { }
}
