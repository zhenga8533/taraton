package net.volcaronitee.nar.helper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * Utility class for making HTTP requests and handling JSON responses.
 */
public class RequestHttp {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2).followRedirects(HttpClient.Redirect.NORMAL).build();

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
}
