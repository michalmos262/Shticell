package client.util.http;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static client.resources.CommonResourcesPaths.*;

public class HttpClientUtil {

    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();
    public final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(simpleCookieManager)
                    .followRedirects(false)
                    .build();

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

    public static void runAsyncPut(String finalUrl, RequestBody body, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .put(body)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static void runAsyncDelete(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .delete()
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static Request getCurrentSheet() {
        return new Request.Builder()
                .url(SHEET_ENDPOINT)
                .build();
    }

    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        RequestBody emptyBody = FormBody.create(null, new byte[0]);

        HttpClientUtil.runAsyncPost(LOGOUT, emptyBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.isSuccessful() || response.isRedirect()) {
                    HttpClientUtil.removeCookiesOf(BASE_DOMAIN);
                }
            }
        });
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}