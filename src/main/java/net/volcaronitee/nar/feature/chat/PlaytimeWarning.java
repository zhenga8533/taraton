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
        TickUtil.register(INSTANCE::increment, 20);
        INSTANCE.refreshPlatime();
    }

    private void refreshPlatime() {
        long now = System.currentTimeMillis() / 1000;;

        // If last played was yesterday, reset playtime
        if (NarData.getData().has("lastPlayed")) {
            long lastPlayed = NarData.getData().get("lastPlayed").getAsLong();
            java.time.LocalDate lastPlayedDate = java.time.Instant.ofEpochSecond(lastPlayed)
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            java.time.LocalDate currentDate = java.time.Instant.ofEpochSecond(now)
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            if (!currentDate.isEqual(lastPlayedDate)) {
                NarData.getData().addProperty("playtime", 0);
            }
        }

        NarData.getData().addProperty("lastPlayed", now);
    }

    /**
     * Handles the end of the client tick event to increment playtime ticks.
     * 
     * @param client The Minecraft client instance.
     */
    private void increment(MinecraftClient client) {
        if (client.world == null || client.player == null
                || !NarConfig.getHandler().chat.playtimeWarning) {
            return;
        }

        int playtime = INSTANCE.getPlaytime() + 1;
        NarData.getData().addProperty("playtime", playtime);

        if (playtime % PLAYTIME_THRESHOLD == 0) { // Every 8 hour
            int hours = playtime / 3600;
            NotARat.sendMessage(Text.literal("You have played for ").formatted(Formatting.RED)
                    .append(Text.literal(hours + " hours").formatted(Formatting.DARK_RED))
                    .append(Text.literal(
                            ". Excessive game playing may cause problems in your normal daily life.")
                            .formatted(Formatting.RED)));
        }
    }

    /**
     * Returns the total playtime in ticks.
     * 
     * @return The total playtime in ticks.
     */
    public int getPlaytime() {
        return NarData.getData().get("playtime").getAsInt();
    }

    /**
     * Formats the playtime into a string in the format HH:MM:SS.
     * 
     * @return Formatted playtime string.
     */
    public String formatPlaytime() {
        int totalSeconds = INSTANCE.getPlaytime();
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
