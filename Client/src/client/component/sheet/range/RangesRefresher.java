package client.component.sheet.range;

import client.util.http.HttpClientUtil;
import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static client.resources.CommonResourcesPaths.GSON_INSTANCE;
import static client.resources.CommonResourcesPaths.RANGE_NAMES_ENDPOINT;

public class RangesRefresher extends TimerTask {
    private List<String> lastFetchedData;
    private final Consumer<List<String>> rangeNamesListConsumer;

    public RangesRefresher(Consumer<List<String>> rangeNamesListConsumer) {
        this.rangeNamesListConsumer = rangeNamesListConsumer;
    }

    @Override
    public void run() {
        HttpClientUtil.runAsyncGet(RANGE_NAMES_ENDPOINT, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    Type listType = new TypeToken<List<String>>(){}.getType();
                    List<String> rangeNames = GSON_INSTANCE.fromJson(responseBody, listType);
                    Collections.sort(rangeNames);
                    if (lastFetchedData == null || !lastFetchedData.equals(rangeNames)) {
                        // If the data is different, update the last fetched data
                        lastFetchedData = rangeNames;
                        rangeNamesListConsumer.accept(rangeNames);
                    }
                } else {
                    System.out.println("Error: " + response.body().string());
                }
            }
        });
    }
}