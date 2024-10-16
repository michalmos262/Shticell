package server.constant;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;

public class Constants {
    // response content types
    public static final String APPLICATION_JSON = "application/json";

    // request parts
    public static final String FILE_PART = "file";

    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();

    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
}