package net.volcaronitee.taraton.util;

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
     * Performs a POST request to the specified URL with the given body and returns the response as
     * a CompletableFuture<String>.
     * 
     * @param url The URL to send the POST request to.
     * @param body The body of the POST request, typically in JSON format.
     * @return CompletableFuture<String> containing the response data, or null if the request fails
     *         or the response is not 200 OK.
     */
    public static CompletableFuture<String> post(String url, String body) {
        try {
            HttpRequest request =
                    HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                            .uri(URI.create(url)).header("Content-Type", "application/json")
                            .header("Accept", "application/json").build();

            return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body).exceptionally(e -> {
                        e.printStackTrace();
                        return null;
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(null);
        }
    }

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
                        e.printStackTrace();
                        return null;
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Performs a GET request to the specified image URL and returns the image as a NativeImage.
     * 
     * @param imageUrl The URL of the image to fetch.
     * @return CompletableFuture containing the NativeImage, or null if the request fails or the
     *         response is not 200 OK.
     */
    public static CompletableFuture<NativeImage> getImage(String imageUrl) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(imageUrl))
                .header("User-Agent", "Mozilla/5.0").build();

        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApplyAsync(response -> {
                    if (response.statusCode() != 200) {
                        return null;
                    }
                    return ParseUtil.parseImageStream(response.body());
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
}
