package client.component.dashboard;

import client.util.http.HttpClientUtil;
import com.google.gson.reflect.TypeToken;
import dto.user.PermissionRequestDto;
import javafx.beans.property.StringProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static client.resources.CommonResourcesPaths.*;
import static serversdk.request.parameter.RequestParameters.SHEET_NAME;

public class SheetUserPermissionsRefresher extends TimerTask {
    private final StringProperty currentSheetNameProperty;
    private final Consumer<List<PermissionRequestDto>> sheetPermissionsConsumer;
    private List<PermissionRequestDto> lastFetchedData;

    public SheetUserPermissionsRefresher(StringProperty currentSheetNameProperty, Consumer<List<PermissionRequestDto>> sheetPermissionsConsumer) {
        this.currentSheetNameProperty = currentSheetNameProperty;
        this.sheetPermissionsConsumer = sheetPermissionsConsumer;
    }

    @Override
    public void run() {
        if (currentSheetNameProperty.getValue() != null) {
            String url = HttpUrl
                    .parse(PERMISSION_REQUEST_ENDPOINT)
                    .newBuilder()
                    .addQueryParameter(SHEET_NAME, currentSheetNameProperty.getValue())
                    .build()
                    .toString();

            HttpClientUtil.runAsyncGet(url, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.isSuccessful()) {
                        Type listType = new TypeToken<List<PermissionRequestDto>>(){}.getType();
                        List<PermissionRequestDto> permissionRequests = GSON_INSTANCE.fromJson(responseBody, listType);
                        if (lastFetchedData == null || !lastFetchedData.equals(permissionRequests)) {
                            // If the data is different, update the last fetched data
                            lastFetchedData = permissionRequests;
                            sheetPermissionsConsumer.accept(permissionRequests);
                        }
                    } else {
                        System.out.println("Error: " + response.body().string());
                    }
                }
            });
        }
    }
}
