/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package LOL_141.vehicle_addition.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetWorker {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5L)).followRedirects(HttpClient.Redirect.ALWAYS).version(HttpClient.Version.HTTP_1_1).build();

    private NetWorker() {
    }

    public static String get(String url, Map<String, String> requestPropertyData) throws IOException {
        HttpRequest request = NetWorker.createRequestBuilder(url, requestPropertyData).GET().build();
        return NetWorker.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body();
    }

    public static String post(String url, String param, Map<String, String> requestPropertyData) throws IOException {
        HttpRequest request = NetWorker.createRequestBuilder(url, requestPropertyData).POST(HttpRequest.BodyPublishers.ofString(param, StandardCharsets.UTF_8)).build();
        return NetWorker.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body();
    }

    private static HttpRequest.Builder createRequestBuilder(String url, Map<String, String> requestPropertyData) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url));
        requestPropertyData.forEach(builder::header);
        return builder;
    }

    public static <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> bodyHandler) throws IOException {
        try {
            return HTTP_CLIENT.send(request, bodyHandler);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while sending HTTP request", e);
        }
    }
}

