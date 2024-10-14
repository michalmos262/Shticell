package client.util.http;

import okhttp3.*;
import serversdk.request.body.EditCellBody;

import java.util.function.Consumer;

import static client.resources.CommonResourcesPaths.*;
import static serversdk.request.parameter.RequestParameters.CELL_POSITION;

public class HttpClientUtil {

    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();
    public final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(simpleCookieManager)
                    .followRedirects(false)
                    .build();

    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        simpleCookieManager.setLogData(logConsumer);
    }

    public static void removeCookiesOf(String domain) {
        simpleCookieManager.removeCookiesOf(domain);
    }

    public static void runAsyncGet(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static void runAsyncPost(String finalUrl, RequestBody body, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .post(body)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static Request getCell(String cellPositionId) {
        String url = HttpUrl
                .parse(CELL_ENDPOINT)
                .newBuilder()
                .addQueryParameter(CELL_POSITION, cellPositionId)
                .build()
                .toString();

        return new Request.Builder()
                .url(url)
                .build();
    }

    public static Request putCell(String cellPositionId, String originalValue) {
        // create the request body
        String updateCellBodyJson = GSON_INSTANCE.toJson(new EditCellBody(originalValue));
        MediaType mediaType = MediaType.get(JSON_MEDIA_TYPE);
        RequestBody requestBody = RequestBody.create(updateCellBodyJson, mediaType);

        String url = HttpUrl
                .parse(CELL_ENDPOINT)
                .newBuilder()
                .addQueryParameter(CELL_POSITION, cellPositionId)
                .build()
                .toString();

        return new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();
    }

    public static Request getCurrentSheet() {
        return new Request.Builder()
                .url(SHEET_ENDPOINT)
                .build();
    }

    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}