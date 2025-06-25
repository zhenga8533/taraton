package net.volcaronitee.volcclient.feature.general;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

/**
 * Feature that tracks server status such as ping, FPS, TPS, and player angles.
 */
public class ServerStatus {
    private final static ServerStatus INSTANCE = new ServerStatus();

    private int ping = 0;
    private int fps = 0;
    private int tps = 0;
    private int cps = 0;
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    private int angle = 0;
    private float day = 0.0f;

    /**
     * Private constructor to prevent instantiation.
     */
    private ServerStatus() {}

    /**
     * Sets the current latency ping of the client.
     */
    private static void setPing() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        PlayerListEntry clientPlayer = networkHandler.getPlayerListEntry(client.player.getUuid());
        if (clientPlayer != null) {
            INSTANCE.ping = clientPlayer.getLatency();
        } else {
            INSTANCE.ping = 0;
        }
    }

    /**
     * Gets the current latency ping of the client.
     * 
     * @return The current ping in milliseconds.
     */
    public static int getPing() {
        return INSTANCE.ping;
    }

    /**
     * Gets the current TPS of the server.
     * 
     * @return The current TPS (Ticks Per Second).
     */
    public static int getTps() {
        return INSTANCE.tps;
    }
}
