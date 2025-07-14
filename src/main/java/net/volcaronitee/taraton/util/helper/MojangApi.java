package net.volcaronitee.taraton.util.helper;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import com.google.gson.JsonObject;
import net.volcaronitee.taraton.config.TaratonJson;
import net.volcaronitee.taraton.util.RequestUtil;

/**
 * Utility class for fetching player information from Mojang's session server.
 */
public class MojangApi {
    /**
     * Parses a UUID into a string without dashes.
     * 
     * @param uuid UUID to parse
     * @return String representation of the UUID without dashes, or an empty string if null
     */
    public static String parseUUID(UUID uuid) {
        if (uuid == null) {
            return "";
        }

        String uuidString = uuid.toString();
        return uuidString.replace("-", "");
    }

    /**
     * Fetches a username from Mojang's session server using a UUID.
     *
     * @param uuid UUID of the player (with or without dashes)
     * @return The player's username, or null if not found
     */
    public static CompletableFuture<String> getUsernameFromUUID(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            // Return an already completed future with an empty string if input is invalid
            return CompletableFuture.completedFuture("");
        }

        String cleanUuid = uuid.replace("-", "");

        String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + cleanUuid;

        // Use RequestUtil.get to send the async GET request
        return RequestUtil.get(url).thenApply(responseBody -> {
            if (responseBody == null || responseBody.isEmpty()) {
                return null;
            }

            // Parse the username from the JSON response
            String username = parseUsername(responseBody);
            if (username != null && !username.isEmpty()) {
                return username;
            } else {
                return null;
            }
        }).exceptionally(e -> {
            return null;
        });
    }

    /**
     * Parses the JSON response from Mojang's session server to extract the username.
     *
     * @param jsonResponse The JSON string received from Mojang's session server.
     * @return The extracted username, or null if parsing fails or the 'name' field is not found.
     */
    private static String parseUsername(String jsonResponse) {
        try {
            JsonObject profile = TaratonJson.GSON.fromJson(jsonResponse, JsonObject.class);
            if (profile != null && profile.has("name") && profile.get("name").isJsonPrimitive()) {
                return profile.get("name").getAsString();
            }
        } catch (Exception e) {
        }

        return null;
    }
}
