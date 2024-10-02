package server.constant;

import com.google.gson.Gson;

public class Constants {
    // request parameters
    public static final String USERNAME = "username";
    public static final String USER_NAME_ERROR = "username_error";

    public static final String SHEET_NAME = "sheet_name";
    public static final String SHEET_VERSION = "sheet_version";
    public static final String SORT_FILTER_BY_COLUMNS = "columns";
    public static final String FILTER_BY_UNIQUE_EFFECTIVE_VALUES = "unique_effective_values";

    public static final String CELL_POSITION = "cell_position";
    public static final String FROM_CELL_POSITION = "from_position";
    public static final String TO_CELL_POSITION = "to_position";
    public static final String CELL_ORIGINAL_VALUE = "original_value";

    public static final String RANGE_NAME = "range_name";

    // response content types
    public static final String APPLICATION_JSON = "application/json";

    // request parts
    public static final String FILE_PART = "file";

    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}