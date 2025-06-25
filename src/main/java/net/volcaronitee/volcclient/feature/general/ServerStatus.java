package net.volcaronitee.volcclient.feature.general;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

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

    public static ServerStatus getInstance() {
        return INSTANCE;
    }

    public int getPing() {
        return ping;
    }

    private void setPing() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        PlayerListEntry clientPlayer = networkHandler.getPlayerListEntry(client.player.getUuid());
        if (clientPlayer != null) {
            this.ping = clientPlayer.getLatency();
        } else {
            this.ping = 0;
        }
    }
}
