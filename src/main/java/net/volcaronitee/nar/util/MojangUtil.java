package net.volcaronitee.nar.util;

import java.util.UUID;
import com.google.gson.JsonObject;

/**
 * Utility class for fetching player information from Mojang's session server.
 */
public class MojangUtil {
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
    public static String getUsernameFromUUID(String uuid) {
        try {
            if (uuid.isEmpty()) {
                return "";
            }

            JsonObject profile = RequestUtil
                    .get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
            return profile != null ? profile.get("name").getAsString() : "";
        } catch (Exception e) {
            return "";
        }
    }
}
