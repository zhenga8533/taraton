package net.volcaronitee.nar.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Utility class for making HTTP requests and handling JSON responses.
 */
public class RequestUtil {
    /**
     * Performs a GET request to the specified URL and returns the response as a JsonObject.
     * 
     * @param url The URL to send the GET request to.
     * @return JsonObject containing the response data, or null if the request fails or the response
     *         is not 200 OK.
     */
    public static JsonObject get(String url) {
        try {
            URL uri = new URI(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return null;
            }

            try (BufferedReader in =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    json.append(line);
                }

                return JsonParser.parseString(json.toString()).getAsJsonObject();
            }
        } catch (Exception e) {
            return null;
        }
    }
}
