package net.volcaronitee.volcclient.feature.general;

public class SkyBlockStats {
    private static final SkyBlockStats INSTANCE = new SkyBlockStats();

    private String pet = "None";
    private int soulflow = 0;
    private int legion = 0;

    /**
     * Private constructor to prevent instantiation.
     */
    private SkyBlockStats() {}

    public static SkyBlockStats getInstance() {
        return INSTANCE;
    }
}
