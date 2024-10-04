package client.resources;

import com.google.gson.Gson;
import serversdk.exception.ServerException;

public class CommonResourcesPaths {
    // fxml locations
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/client/component/login/login.fxml";
    public final static String DASHBOARD_PAGE_FXML_RESOURCE_LOCATION = "/client/component/dashboard/dashboard.fxml";
    public final static String SHEET_PAGE_FXML_RESOURCE_LOCATION = "/client/component/sheet/grid.fxml";
    public final static String MAIN_APP_FXML_RESOURCE_LOCATION = "/client/component/mainapp/mainApp.fxml";

    // css locations
    public final static String MAIN_APP_LIGHT_CSS_RESOURCE = "/client/component/sheet/app/css/lightApp.css";
    public final static String MAIN_APP_DARK_CSS_RESOURCE = "/client/component/sheet/app/css/darkApp.css";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/Server_war_exploaded";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String LOGOUT = FULL_SERVER_PATH + "/logout";

    public final static String DASHBOARD_ENDPOINT = FULL_SERVER_PATH + "/dashboard";
    public final static String SHEET_ENDPOINT = DASHBOARD_ENDPOINT + "/sheet";
    public final static String CELL_ENDPOINT = SHEET_ENDPOINT + "/cell";

    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();

    // constants
    public final static String SUPPORTED_FILE_TYPE = "xml";
    public final static String GENERAL_ERROR_JSON = GSON_INSTANCE.toJson(
            new ServerException.ErrorResponse("General error occurred")
    );
    public final static String JSON_MEDIA_TYPE = "application/json; charset=utf-8";
}