package net.volcaronitee.taraton.feature.general;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.LocationUtil;
import net.volcaronitee.taraton.util.TickUtil;

/**
 * Feature to display a toast notification when the player enters a new zone.
 */
public class ZoneToast {
    private static final ZoneToast INSTANCE = new ZoneToast();

    private String currentZone = "None";

    /**
     * Private constructor to prevent instantiation.
     */
    private ZoneToast() {}

    /**
     * Registers the ZoneToast feature to listen for client tick events.
     */
    public static void register() {
        TickUtil.register(INSTANCE::checkZone, 5);
    }

    /**
     * Returns the singleton instance of ZoneToast.
     * 
     * @param client The Minecraft client instance.
     */
    private void checkZone(MinecraftClient client) {
        Text newZone = LocationUtil.getZone();
        String newZoneStr = newZone.getString();

        if (!newZoneStr.equals(currentZone)) {
            currentZone = newZoneStr;
            showToast(newZone);
        }
    }

    /**
     * Displays a toast notification with the specified zone name.
     * 
     * @param zone The name of the zone to display in the toast notification.
     */
    private void showToast(Text zone) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getToastManager() == null
                || !FeatureUtil.isEnabled(TaratonConfig.getInstance().general.zoneToast)) {
            return;
        }

        // Create the title and description for the toast
        Text title = Text.literal("Entered Zone");

        // Create and add the toast to the toast manager
        SystemToast toast = new SystemToast(SystemToast.Type.WORLD_BACKUP, title, zone);
        client.getToastManager().add(toast);
    }
}
