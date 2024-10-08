package client.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.cell.CellPositionDto;
import dto.cell.CellPositionDtoDeserializer;
import dto.cell.CellPositionDtoSerializer;
import serversdk.exception.ServerException;

public class CommonResourcesPaths {
    // fxml locations
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/client/component/login/login.fxml";
    public final static String DASHBOARD_PAGE_FXML_RESOURCE_LOCATION = "/client/component/dashboard/dashboard.fxml";
    public final static String MAIN_SHEET_PAGE_FXML_RESOURCE_LOCATION = "/client/component/sheet/mainsheet/mainSheet.fxml";
    public final static String MAIN_APP_FXML_RESOURCE_LOCATION = "/client/component/mainapp/mainApp.fxml";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/Server_war_exploaded";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String LOGOUT = FULL_SERVER_PATH + "/logout";

    public final static String DASHBOARD_ENDPOINT = FULL_SERVER_PATH + "/dashboard";
    public final static String USER_ENDPOINT = FULL_SERVER_PATH + "/user";
    public final static String USER_SHEET_PERMISSIONS_ENDPOINT = USER_ENDPOINT + "/sheet-permissions";

    public final static String SHEET_ENDPOINT = FULL_SERVER_PATH + "/sheet";
    public final static String SORTED_SHEET_ROWS_ENDPOINT = SHEET_ENDPOINT + "/sorted-rows";
    public final static String CELL_ENDPOINT = SHEET_ENDPOINT + "/cell";
    public final static String RANGE_ENDPOINT = SHEET_ENDPOINT + "/range";
    public final static String RANGE_NAMES_ENDPOINT = SHEET_ENDPOINT + "/range-names";
    public final static String SHEET_DIMENSION_ENDPOINT = SHEET_ENDPOINT + "/dimension";

    // GSON instance
    public final static Gson GSON_INSTANCE = new GsonBuilder().
            registerTypeAdapter(CellPositionDto.class, new CellPositionDtoSerializer())
            .registerTypeAdapter(CellPositionDto.class, new CellPositionDtoDeserializer())
            .create();

    // constants
    public final static String SUPPORTED_FILE_TYPE = "xml";
    public final static String GENERAL_ERROR_JSON = GSON_INSTANCE.toJson(
            new ServerException.ErrorResponse("General error occurred")
    );
    public final static String JSON_MEDIA_TYPE = "application/json; charset=utf-8";
    public final static int REFRESH_RATE = 1000;
}