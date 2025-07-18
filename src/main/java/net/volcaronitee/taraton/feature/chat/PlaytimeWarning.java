package net.volcaronitee.taraton.feature.chat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonData;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.TickUtil;

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
        if (TaratonData.getData().has("lastPlayed")) {
            long lastPlayed = TaratonData.getData().get("lastPlayed").getAsLong();
            java.time.LocalDate lastPlayedDate = java.time.Instant.ofEpochSecond(lastPlayed)
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            java.time.LocalDate currentDate = java.time.Instant.ofEpochSecond(now)
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            if (!currentDate.isEqual(lastPlayedDate)) {
                TaratonData.getData().addProperty("playtime", 0);
            }
        }

        TaratonData.getData().addProperty("lastPlayed", now);
    }

    /**
     * Handles the end of the client tick event to increment playtime ticks.
     * 
     * @param client The Minecraft client instance.
     */
    private void increment(MinecraftClient client) {
        if (client.world == null || client.player == null
                || !FeatureUtil.isEnabled(TaratonConfig.getInstance().chat.playtimeWarning)) {
            return;
        }

        int playtime = INSTANCE.getPlaytime() + 1;
        TaratonData.getData().addProperty("playtime", playtime);

        if (playtime % PLAYTIME_THRESHOLD == 0) { // Every 8 hour
            int hours = playtime / 3600;
            Taraton.sendMessage(Text.literal("You have played for ").formatted(Formatting.RED)
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
        return TaratonData.getData().get("playtime").getAsInt();
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
