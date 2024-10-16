package client.component.dashboard;

import client.util.http.HttpClientUtil;
import dto.user.SheetNamesAndFileMetadatasDto;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static client.resources.CommonResourcesPaths.GSON_INSTANCE;
import static client.resources.CommonResourcesPaths.USER_SHEETS_ENDPOINT;

public class AvailableSheetsTableRefresher extends TimerTask {
    private SheetNamesAndFileMetadatasDto lastFetchedData;
    private final Consumer<SheetNamesAndFileMetadatasDto> sheetNameAndFileMetadataConsumer;
    private final Runnable clickOnLastSelectedSheetEntryRunnable;

    public AvailableSheetsTableRefresher(Consumer<SheetNamesAndFileMetadatasDto> sheetNameAndFileMetadataConsumer,
                                         Runnable clickOnLastSelectedSheetEntryRunnable) {
        this.sheetNameAndFileMetadataConsumer = sheetNameAndFileMetadataConsumer;
        this.clickOnLastSelectedSheetEntryRunnable = clickOnLastSelectedSheetEntryRunnable;
    }

    @Override
    public void run() {
        HttpClientUtil.runAsyncGet(USER_SHEETS_ENDPOINT, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    SheetNamesAndFileMetadatasDto sheetNamesAndFileMetadatasDto = GSON_INSTANCE.fromJson(responseBody, SheetNamesAndFileMetadatasDto.class);
                    if (lastFetchedData == null || !lastFetchedData.equals(sheetNamesAndFileMetadatasDto)) {
                        // If the data is different, update the last fetched data
                        lastFetchedData = sheetNamesAndFileMetadatasDto;
                        sheetNameAndFileMetadataConsumer.accept(sheetNamesAndFileMetadatasDto);
                        clickOnLastSelectedSheetEntryRunnable.run();
                    }
                } else {
                    System.out.println("Error: " + response.body().string());
                }
            }
        });
    }
}