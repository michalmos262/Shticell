package server.util;

import engine.api.Engine;
import engine.impl.EngineImpl;
import engine.user.usermanager.UserManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServletUtils {
    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	private static final String ENGINE_ATTRIBUTE_NAME = "engine";

	private static final Object userManagerLock = new Object();
	private static final Object engineManagerLock = new Object();

	public static UserManager getUserManager(ServletContext servletContext) {
		synchronized (userManagerLock) {
			if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
			}
		}
		return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
	}

	public static Engine getEngineInstance(ServletContext servletContext) {
		synchronized (engineManagerLock) {
			if (servletContext.getAttribute(ENGINE_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(ENGINE_ATTRIBUTE_NAME, new EngineImpl());
			}
		}
		return (Engine) servletContext.getAttribute(ENGINE_ATTRIBUTE_NAME);
	}

	public static String extractRequestBody(HttpServletRequest request) throws IOException {
		// Read the request body
		StringBuilder stringBuilder = new StringBuilder();
		String line;

		// Using BufferedReader to read the input stream
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
			}
		}

		// Get the request body as a String
		return stringBuilder.toString();
	}
}