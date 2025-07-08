package net.volcaronitee.nar.util;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
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
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URI(imageUrl).toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                BufferedImage bufferedImage;
                try (InputStream is = connection.getInputStream()) {
                    bufferedImage = ImageIO.read(is);
                }

                if (bufferedImage == null) {
                    return null;
                }

                NativeImage nativeImage =
                        new NativeImage(bufferedImage.getWidth(), bufferedImage.getHeight(), false);
                for (int y = 0; y < bufferedImage.getHeight(); y++) {
                    for (int x = 0; x < bufferedImage.getWidth(); x++) {
                        int rgb = bufferedImage.getRGB(x, y);
                        int alpha = (rgb >> 24) & 0xFF;
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;
                        int abgr = (alpha << 24) | (blue << 16) | (green << 8) | red;
                        nativeImage.setColor(x, y, abgr);
                    }
                }
                return nativeImage;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }, EXECUTOR);
    }
}
