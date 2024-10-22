package client.component.sheet.mainsheet;

import client.util.http.HttpClientUtil;
import dto.sheet.FileMetadata;
import dto.user.UserPermission;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static client.resources.CommonResourcesPaths.*;
import static serversdk.request.parameter.RequestParameters.SHEET_NAME;

public class MainSheetRefresher extends TimerTask {
    private final String sheetName;
    private final Consumer<Boolean> setIsWriterConsumer;
    private String yourLastSheetPermissionType;

    public MainSheetRefresher(String sheetName, Consumer<Boolean> setIsWriterConsumer) {
        this.sheetName = sheetName;
        this.setIsWriterConsumer = setIsWriterConsumer;
    }

    @Override
    public void run() {
        String url = HttpUrl
                .parse(USER_SHEETS_ENDPOINT)
                .newBuilder()
                .addQueryParameter(SHEET_NAME, sheetName)
                .build()
                .toString();

        HttpClientUtil.runAsyncGet(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error refreshing main sheet: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    FileMetadata fileMetadata = GSON_INSTANCE.fromJson(responseBody, FileMetadata.class);
                    String yourPermissionType = fileMetadata.getYourPermission();
                    if (yourLastSheetPermissionType == null || !yourLastSheetPermissionType.equals(yourPermissionType)) {
                        // If the data is different, update the last fetched data
                        yourLastSheetPermissionType = yourPermissionType;

                        boolean isWriter = yourPermissionType.equals(UserPermission.WRITER.name()) ||
                                yourPermissionType.equals(UserPermission.OWNER.name());

                        setIsWriterConsumer.accept(isWriter);
                    }
                } else {
                    System.out.println("Error refreshing main sheet: " + response.body().string());
                }
            }
        });
    }
}