package client.component.sheet.actionline;

import client.util.http.HttpClientUtil;
import dto.sheet.SheetDto;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.TimerTask;

import static client.resources.CommonResourcesPaths.*;

public class ActionLineRefresher extends TimerTask {
    private final IntegerProperty currentSheetVersion;
    private final Runnable indicateButton;

    public ActionLineRefresher(Runnable indicateButton, IntegerProperty currentSheetVersion) {
        this.indicateButton = indicateButton;
        this.currentSheetVersion = currentSheetVersion;
    }

    @Override
    public void run() {
        HttpClientUtil.runAsyncGet(SHEET_ENDPOINT, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    SheetDto sheetDto = GSON_INSTANCE.fromJson(responseBody, SheetDto.class);
                    int currentVersion = sheetDto.getVersion();
                    if (currentSheetVersion.getValue() < currentVersion) {
                        // If the current version is different, update the last version
                        Platform.runLater(indicateButton);
                    }
                } else {
                    System.out.println("Error: " + response.body().string());
                }
            }
        });
    }
}
