package client.component.dashboard;

import client.util.http.HttpClientUtil;
import dto.user.SheetNameAndFileMetadataDto;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static client.resources.CommonResourcesPaths.GSON_INSTANCE;
import static client.resources.CommonResourcesPaths.USER_SHEET_PERMISSIONS_ENDPOINT;

public class DashboardRefresher extends TimerTask {
    private SheetNameAndFileMetadataDto lastFetchedData;
    private final Consumer<SheetNameAndFileMetadataDto> sheetNameAndFileMetadataConsumer;

    public DashboardRefresher(Consumer<SheetNameAndFileMetadataDto> sheetNameAndFileMetadataConsumer) {
        this.sheetNameAndFileMetadataConsumer = sheetNameAndFileMetadataConsumer;
    }

    @Override
    public void run() {
        HttpClientUtil.runAsyncGet(USER_SHEET_PERMISSIONS_ENDPOINT, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    SheetNameAndFileMetadataDto sheetNameAndFileMetadataDto = GSON_INSTANCE.fromJson(responseBody, SheetNameAndFileMetadataDto.class);
                    if (lastFetchedData == null || !lastFetchedData.equals(sheetNameAndFileMetadataDto)) {
                        // If the data is different, update the last fetched data
                        lastFetchedData = sheetNameAndFileMetadataDto;
                        sheetNameAndFileMetadataConsumer.accept(sheetNameAndFileMetadataDto);
                    }
                } else {
                    System.out.println("Error: " + response.body().string());
                }
            }
        });
    }
}