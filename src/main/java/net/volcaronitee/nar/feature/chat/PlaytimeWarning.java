package net.volcaronitee.nar.feature.chat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.config.NarData;
import net.volcaronitee.nar.util.TickUtil;

/**
 * Singleton class that tracks the player's playtime and sends a warning message
 */
public class PlaytimeWarning {
    private static final PlaytimeWarning INSTANCE = new PlaytimeWarning();

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
        INSTANCE.playtime = NarData.getData().get("playtime").getAsInt();
        TickUtil.register(INSTANCE::onTick, 20);
    }

    /**
     * Handles the end of the client tick event to increment playtime ticks.
     * 
     * @param client The Minecraft client instance.
     */
    private void onTick(MinecraftClient client) {
        if (client.world == null || client.player == null
                || !NarConfig.getHandler().chat.playtimeWarning) {
            return;
        }

        INSTANCE.playtime++;

        if (INSTANCE.playtime % PLAYTIME_THRESHOLD == 0) { // Every 8 hour
            int hours = INSTANCE.playtime / 3600;
            client.inGameHud.getChatHud().addMessage(NotARat.MOD_TITLE.copy()
                    .append(Text.literal(" You have played for " + hours
                            + " hours. Excessive game playing may cause problems in your normal daily life.")
                            .formatted(Formatting.RED)));
        }
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
