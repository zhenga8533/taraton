package net.volcaronitee.volcclient.feature.general;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.OverlayUtil;
import net.volcaronitee.volcclient.util.OverlayUtil.LineContent;
import net.volcaronitee.volcclient.util.OverlayUtil.Overlay;

/**
 * Feature that tracks server status such as ping, FPS, TPS, and player angles.
 */
public class ServerStatus {
    private static final Overlay overlay = OverlayUtil.createOverlay("server_status",
            () -> ConfigUtil.getHandler().general.serverStatus,
            List.of(new LineContent("§7[§6XYZ§7]§r ", "§f-195, 88, 58"),
                    new LineContent("§7[§6Y/P§7]§r ", "§f-89.15 / 30.89"),
                    new LineContent("§7[§6Dir§7]§r ", "§fEast"),
                    new LineContent("§7[§6Png§7]§r ", "§a58§fms"),
                    new LineContent("§7[§6FPS§7]§r ", "§a60 §ffps"),
                    new LineContent("§7[§6TPS§7]§r ", "§a19.8 §ftps"),
                    new LineContent("§7[§6CPS§7]§r ", "§a0 §f: §a0"),
                    new LineContent("§7[§6Day§7]§r ", "§f0.75")));
    private final static ServerStatus INSTANCE = new ServerStatus();

    private float yaw = 0.0f;
    private float pitch = 0.0f;
    private int angle = 0;
    private int ping = 0;
    private int fps = 0;
    private int tps = 0;
    private int cps = 0;
    private float day = 0.0f;

    /**
     * Private constructor to prevent instantiation.
     */
    private ServerStatus() {}

    public static void register() {

    }

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
