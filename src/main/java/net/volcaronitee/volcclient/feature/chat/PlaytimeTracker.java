package net.volcaronitee.volcclient.feature.chat;

import com.google.gson.JsonObject;
import net.volcaronitee.volcclient.util.JsonUtil;

public class PlaytimeTracker {
    private static final PlaytimeTracker INSTANCE = new PlaytimeTracker();

    private static final String FILENAME = "daily_playtime.json";
    private int playtime;

    /**
     * Private constructor to prevent instantiation.
     */
    private PlaytimeTracker() {}

    public void register() {
        // Load the playtime data from the JSON file
        JsonObject playtimeData = JsonUtil.loadJson(FILENAME);
        if (!playtimeData.has("playtime")) {
            playtimeData.addProperty("playtime", 0);
        }
        INSTANCE.playtime = playtimeData.get("playtime").getAsInt();

        // Register world/server join event

        // Register world/server leave event
    }
}
