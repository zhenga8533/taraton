package net.volcaronitee.volcclient.feature.chat;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.JsonUtil;

public class PlaytimeWarning {
    private static final PlaytimeWarning INSTANCE = new PlaytimeWarning();
    private static final String FILENAME = "daily_playtime.json";
    private static final int PLAYTIME_THRESHOLD = 20 * 3600 * 8; // 8 hours in ticks

    private int playtimeTicks = 0;

    /**
     * Private constructor to prevent instantiation.
     */
    private PlaytimeWarning() {}

    /**
     * Returns the singleton instance of PlaytimeWarning.
     * 
     * @return The PlaytimeWarning instance.
     */
    public static PlaytimeWarning getInstance() {
        return INSTANCE;
    }

    public static void register() {
        // Load the playtime data from the JSON file
        JsonObject playtimeData = JsonUtil.loadJson(FILENAME);
        if (!playtimeData.has("playtimeTicks")) {
            playtimeData.addProperty("playtimeTicks", 0);
        }
        INSTANCE.playtimeTicks = playtimeData.get("playtimeTicks").getAsInt();

        // Register events
        ClientTickEvents.END_CLIENT_TICK.register(PlaytimeWarning::onEndClientTick);
        ServerWorldEvents.UNLOAD.register(PlaytimeWarning::onWorldUnload);
    }

    /**
     * Handles the end of the client tick event to increment playtime ticks.
     * 
     * @param client The Minecraft client instance.
     */
    private static void onEndClientTick(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            return;
        }

        INSTANCE.playtimeTicks++;

        if (INSTANCE.playtimeTicks % PLAYTIME_THRESHOLD == 0) { // Every 8 hour
            int hours = INSTANCE.playtimeTicks / PLAYTIME_THRESHOLD;
            client.inGameHud.getChatHud()
                    .addMessage(Text.literal("You have played for " + hours + " hour(s)."));
        }
    }

    /**
     * Handles the world unload event to save playtime data.
     * 
     * @param server The Minecraft server instance.
     * @param world The server world that is being unloaded.
     */
    private static void onWorldUnload(MinecraftServer server, ServerWorld world) {
        JsonObject playtimeData = new JsonObject();
        playtimeData.addProperty("playtimeTicks", INSTANCE.playtimeTicks);
        JsonUtil.saveJson("", FILENAME, playtimeData);
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
