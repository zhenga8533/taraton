package net.volcaronitee.volcclient.feature.general;

import net.volcaronitee.volcclient.feature.chat.PlaytimeWarning;

/**
 * Feature that tracks SkyBlock stats such as pet, soulflow, and legion.
 */
public class SkyBlockStats {
    private static final SkyBlockStats INSTANCE = new SkyBlockStats();

    private String pet = "None";
    private int soulflow = 0;
    private int legion = 0;

    /**
     * Private constructor to prevent instantiation.
     */
    private SkyBlockStats() {}

    /**
     * Gets the singleton instance of the SkyBlockStats feature.
     * 
     * @return The SkyBlockStats instance.
     */
    public static SkyBlockStats getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the SkyBlock stats feature.
     */
    public static void register() {

    }

    /**
     * Gets the daily playtime in the format HH:MM:SS.
     * 
     * @return The formatted playtime string.
     */
    public String getPlaytime() {
        int totalSeconds = PlaytimeWarning.getPlaytimeTicks() / 20;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
