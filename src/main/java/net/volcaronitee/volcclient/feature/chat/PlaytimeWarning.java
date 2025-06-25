package net.volcaronitee.volcclient.feature.chat;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.JsonUtil;
import net.volcaronitee.volcclient.util.TextUtil;

/**
 * Singleton class that tracks the player's playtime and sends a warning message
 */
public class PlaytimeWarning {
    private static final PlaytimeWarning INSTANCE = new PlaytimeWarning();
    private static final String FILENAME = "daily_playtime.json";
    private static final int PLAYTIME_THRESHOLD = 20 * 3600 * 8; // 8 hours in ticks

    private int playtimeTicks = 0;

    /**
     * Private constructor to prevent instantiation.
     */
    private PlaytimeWarning() {}

    public static void register() {
        // Load the playtime data from the JSON file
        JsonObject playtimeData = JsonUtil.loadJson(JsonUtil.DATA_DIR, FILENAME);
        if (!playtimeData.has("playtimeTicks")) {
            playtimeData.addProperty("playtimeTicks", 0);
        }
        INSTANCE.playtimeTicks = playtimeData.get("playtimeTicks").getAsInt();

        // Register events
        ClientTickEvents.END_CLIENT_TICK.register(PlaytimeWarning::onEndClientTick);
        ClientLifecycleEvents.CLIENT_STOPPING.register(PlaytimeWarning::onClientClose);
    }

    /**
     * Handles the end of the client tick event to increment playtime ticks.
     * 
     * @param client The Minecraft client instance.
     */
    private static void onEndClientTick(MinecraftClient client) {
        if (client.world == null || client.player == null
                || !ConfigUtil.getHandler().chat.playtimeWarning) {
            return;
        }

        INSTANCE.playtimeTicks++;

        if (INSTANCE.playtimeTicks % PLAYTIME_THRESHOLD == 0) { // Every 8 hour
            int hours = INSTANCE.playtimeTicks / (20 * 3600);
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
    private static void onClientClose(MinecraftClient client) {
        JsonObject playtimeData = new JsonObject();
        playtimeData.addProperty("playtimeTicks", INSTANCE.playtimeTicks);
        JsonUtil.saveJson(JsonUtil.DATA_DIR, FILENAME, playtimeData);
    }

    /**
     * Returns the total playtime in ticks.
     * 
     * @return The total playtime in ticks.
     */
    public static int getPlaytimeTicks() {
        return INSTANCE.playtimeTicks;
    }
}
