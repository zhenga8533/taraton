package net.volcaronitee.volcclient.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.UUID;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Utility class for fetching player information from Mojang's session server.
 */
public class MojangUtil {

    /**
     * Fetches a username from Mojang's session server using a UUID.
     *
     * @param uuid UUID of the player (with or without dashes)
     * @return The player's username, or null if not found
     */
    public static String getUsernameFromUUID(UUID uuid) {
        try {
            if (uuid == null) {
                return "";
            }

            String cleanUUID = uuid.toString().replace("-", "");
            URL url = new URI(
                    "https://sessionserver.mojang.com/session/minecraft/profile/" + cleanUUID)
                            .toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200)
                return "";

            try (BufferedReader in =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    json.append(line);
                }

                JsonObject profile = JsonParser.parseString(json.toString()).getAsJsonObject();
                return profile.get("name").getAsString();
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch username for UUID " + uuid + ": " + e.getMessage());
            return "";
        }
    }
}
