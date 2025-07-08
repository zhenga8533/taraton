package net.volcaronitee.nar.util;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.texture.NativeImage;

/**
 * Utility class for making HTTP requests and handling JSON responses.
 */
public class RequestUtil {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2).followRedirects(HttpClient.Redirect.NORMAL).build();

    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    /**
     * Performs a GET request to the specified URL and returns the response as a JsonObject.
     * 
     * @param url The URL to send the GET request to.
     * @return JsonObject containing the response data, or null if the request fails or the response
     *         is not 200 OK.
     */
    public static CompletableFuture<String> get(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
                    .header("Accept", "application/json").build();

            return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body).exceptionally(e -> {
                        return null;
                    });
        } catch (Exception e) {
            return null;
        }
    }

    public static CompletableFuture<NativeImage> getImage(String imageUrl) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(imageUrl))
                .header("User-Agent", "Mozilla/5.0").build();

        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        return null;
                    }
                    try (InputStream is = response.body()) {
                        return NativeImage.read(is);
                    } catch (Exception e) {
                        return null;
                    }
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
}
