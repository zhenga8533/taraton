package net.volcaronitee.nar.feature.general;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.util.LocationUtil;

/**
 * ServerRejoinAlert is a feature that alerts the user when they rejoin a server
 */
public class ServerRejoinAlert {
    private static final ServerRejoinAlert INSTANCE = new ServerRejoinAlert();

    private String currentServer = "";
    private final Map<String, Long> serverLeaves = new HashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private ServerRejoinAlert() {}

    /**
     * Initializes the ServerRejoinAlert by registering the location callback.
     */
    public static void register() {
        LocationUtil.registerCallback(INSTANCE::onLocationUpdate);
    }

    /**
     * Handles the location update event. This method checks if the server rejoin alert
     */
    private void onLocationUpdate() {
        if (!NarConfig.getHandler().general.serverRejoinAlert) {
            return;
        }

        if (!currentServer.isEmpty()) {
            serverLeaves.put(currentServer, System.currentTimeMillis());
        }

        INSTANCE.currentServer = LocationUtil.getServerName();
        if (INSTANCE.serverLeaves.containsKey(INSTANCE.currentServer)) {
            // Send alert to player
            long sinceLeave =
                    System.currentTimeMillis() - INSTANCE.serverLeaves.get(INSTANCE.currentServer);
            String formattedDuration = Duration.ofMillis(sinceLeave).toString().substring(2)
                    .replaceAll("H", "h ").replaceAll("M", "m ").replaceAll("S", "s").trim()
                    .replaceAll("(?i)(\\d+)(h|m)\\s*(0m\\s*|0s)?", "$1$2").replaceAll("\\s+", " ")
                    .trim();

            NotARat.sendMessage(Text.literal("Rjoined ").formatted(Formatting.RED)
                    .append(Text.literal(INSTANCE.currentServer).formatted(Formatting.GRAY))
                    .append(Text.literal(" after ").formatted(Formatting.RED))
                    .append(Text.literal(formattedDuration).formatted(Formatting.GRAY))
                    .append(Text.literal(".").formatted(Formatting.RED)));
        }
    }
}
