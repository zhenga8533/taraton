package net.volcaronitee.volcclient.feature.chat;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.JsonUtil;
import net.volcaronitee.volcclient.util.TextUtil;
import net.volcaronitee.volcclient.util.TickUtil;

/**
 * Singleton class that tracks the player's playtime and sends a warning message
 */
public class PlaytimeWarning {
    private static final PlaytimeWarning INSTANCE = new PlaytimeWarning();

    private static final String FILENAME = "daily_playtime.json";
    private static final int PLAYTIME_THRESHOLD = 3600 * 8; // 8 hours

    private int playtime = 0;

    /**
     * Private constructor to prevent instantiation.
     */
    private PlaytimeWarning() {}

    /**
     * Returns the singleton instance of PlaytimeWarning.
     * 
     * @return The singleton instance.
     */
    public static PlaytimeWarning getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the playtime warning feature to listen for client tick and lifecycle events.
     */
    public static void register() {
        // Load the playtime data from the JSON file
        JsonObject playtimeData = JsonUtil.loadJson(JsonUtil.DATA_DIR, FILENAME);
        if (!playtimeData.has("playtime")) {
            playtimeData.addProperty("playtime", 0);
        }
        INSTANCE.playtime = playtimeData.get("playtime").getAsInt();

        // Register events
        TickUtil.register(INSTANCE::onTick, 20);
        ClientLifecycleEvents.CLIENT_STOPPING.register(INSTANCE::onClientClose);
    }

    /**
     * Handles the end of the client tick event to increment playtime ticks.
     * 
     * @param client The Minecraft client instance.
     */
    private void onTick(MinecraftClient client) {
        if (client.world == null || client.player == null
                || !ConfigUtil.getHandler().chat.playtimeWarning) {
            return;
        }

        INSTANCE.playtime++;

        if (INSTANCE.playtime % PLAYTIME_THRESHOLD == 0) { // Every 8 hour
            int hours = INSTANCE.playtime / 3600;
            client.inGameHud.getChatHud().addMessage(TextUtil.MOD_TITLE.copy()
                    .append(Text.literal(" You have played for " + hours
                            + " hours. Excessive game playing may cause problems in your normal daily life.")
                            .formatted(Formatting.RED)));
        }
    }

    /**
     * Handles the world unload event to save playtime data.
     * 
     * @param event The event triggered when the client is stopping.
     */
    private void onClientClose(MinecraftClient client) {
        JsonObject playtimeData = new JsonObject();
        playtimeData.addProperty("playtime", INSTANCE.playtime);
        JsonUtil.saveJson(JsonUtil.DATA_DIR, FILENAME, playtimeData);
    }

    /**
     * Returns the total playtime in ticks.
     * 
     * @return The total playtime in ticks.
     */
    public int getPlaytime() {
        return INSTANCE.playtime;
    }

    /**
     * Formats the playtime into a string in the format HH:MM:SS.
     * 
     * @return Formatted playtime string.
     */
    public String formatPlaytime() {
        int totalSeconds = INSTANCE.playtime;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
