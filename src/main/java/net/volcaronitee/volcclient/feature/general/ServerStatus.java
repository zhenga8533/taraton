package net.volcaronitee.volcclient.feature.general;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
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
            List.of(new LineContent(Text.literal("§6§lPing:§r x")),
                    new LineContent(Text.literal("§6§lFPS:§r x")),
                    new LineContent(Text.literal("§6§lTPS:§r x")),
                    new LineContent(Text.literal("§6§lCPS:§r x")),
                    new LineContent(Text.literal("§6§lYaw:§r x")),
                    new LineContent(Text.literal("§6§lPitch:§r x")),
                    new LineContent(Text.literal("§6§lAngle:§r x")),
                    new LineContent(Text.literal("§6§lDay:§r x"))));
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
