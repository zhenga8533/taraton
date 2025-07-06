package net.volcaronitee.nar.feature.general;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket.Mode;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.config.NarToggle;
import net.volcaronitee.nar.util.OverlayUtil;
import net.volcaronitee.nar.util.OverlayUtil.LineContent;
import net.volcaronitee.nar.util.OverlayUtil.Overlay;
import net.volcaronitee.nar.util.ScheduleUtil;
import net.volcaronitee.nar.util.TickUtil;

/**
 * Feature that tracks server status such as ping, FPS, TPS, and player angles.
 */
public class ServerStatus {
    private static final ServerStatus INSTANCE = new ServerStatus();

    private static final List<LineContent> LINES = List.of(
            new LineContent("§8[§6XYZ§8]§r ", "§7-195, 88, 58",
                    () -> NarToggle.getHandler().general.xyz),
            new LineContent("§8[§6Y/P§8]§r ", "§7-89.15 / 30.89",
                    () -> NarToggle.getHandler().general.yawPitch),
            new LineContent("§8[§6ANG§8]§r ", "§7East",
                    () -> NarToggle.getHandler().general.direction),
            new LineContent("§8[§6PNG§8]§r ", "§a58 §7ms",
                    () -> NarToggle.getHandler().general.ping),
            new LineContent("§8[§6FPS§8]§r ", "§a60 §7fps",
                    () -> NarToggle.getHandler().general.fps),
            new LineContent("§8[§6TPS§8]§r ", "§a19.8 §7tps",
                    () -> NarToggle.getHandler().general.tps),
            new LineContent("§8[§6CPS§8]§r ", "§a0 §7: §a0",
                    () -> NarToggle.getHandler().general.cps),
            new LineContent("§8[§6DAY§8]§r ", "§a0.75", () -> NarToggle.getHandler().general.day));
    private static final Overlay OVERLAY = OverlayUtil.createOverlay("server_status",
            () -> NarConfig.getHandler().general.serverStatus, LINES);

    // Fields to store server status information
    private int x = 0;
    private int y = 0;
    private int z = 0;
    private double yaw = 0.0;
    private double pitch = 0.0;
    private String direction = "Unknown";
    private int ping = 0;
    private int fps = 0;
    private double tps = 20.0;
    private int leftCps = 0;
    private int rightCps = 0;
    private double day = 0.0;

    // Ping measurement fields
    private long lastPingTime = 0;
    private boolean awaitingPing = false;
    private int pingTickCounter = 0;

    // TPS measurement fields
    private static long lastKeepAliveTime = -1;
    private static final List<Long> tickDurations = new ArrayList<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private ServerStatus() {}

    /**
     * Gets the singleton instance of the ServerStatus feature.
     * 
     * @return The singleton instance of ServerStatus.
     */
    public static ServerStatus getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the server status feature to update every client tick.
     */
    public static void register() {
        TickUtil.register(INSTANCE::updateStatus, 1);
        ClientPlayConnectionEvents.DISCONNECT.register(INSTANCE::resetMeasurements);
    }

    /**
     * Updates the server status information.
     * 
     * @param client The Minecraft client instance.
     */
    private void updateStatus(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            return;
        }

        ClientPlayerEntity player = client.player;

        x = (int) player.getX();
        y = (int) player.getY();
        z = (int) player.getZ();

        yaw = player.getYaw();
        pitch = player.getPitch();
        direction = player.getHorizontalFacing().name().toLowerCase();

        // Request ping every 60 ticks (3 seconds)
        if (++pingTickCounter >= 60) {
            pingTickCounter = 0;
            if (!awaitingPing) {
                lastPingTime = System.currentTimeMillis();
                awaitingPing = true;
                client.getNetworkHandler()
                        .sendPacket(new ClientStatusC2SPacket(Mode.REQUEST_STATS));
            }
        }

        // Timeout if no ping reply received within 5s
        if (awaitingPing && System.currentTimeMillis() - lastPingTime > 5000) {
            awaitingPing = false;
            lastPingTime = 0;
        }

        fps = client.getCurrentFps();
        tps = estimateTps();
        day = MinecraftClient.getInstance().world.getTime() / 24000.0f;

        updateOverlay();
    }

    /**
     * Called when a ping response is received from the server.
     */
    public void onPingResponse() {
        if (awaitingPing && lastPingTime != 0) {
            ping = (int) (System.currentTimeMillis() - lastPingTime);
            awaitingPing = false;
            lastPingTime = 0;
        }
    }

    /**
     * Called when a keep-alive packet is received from the server.
     */
    public void onKeepAlivePacket() {
        long currentTime = System.currentTimeMillis();
        if (lastKeepAliveTime != -1) {
            long duration = currentTime - lastKeepAliveTime;
            tickDurations.add(duration);
            while (tickDurations.size() > 10) {
                tickDurations.remove(0);
            }
        }
        lastKeepAliveTime = currentTime;
    }

    /**
     * Estimates the server's TPS (Ticks Per Second) based on the recorded tick durations.
     * 
     * @return The estimated TPS.
     */
    public double estimateTps() {
        if (tickDurations.isEmpty()) {
            return 20.0;
        }

        long totalDuration = 0;
        for (long duration : tickDurations) {
            totalDuration += duration;
        }

        // Calculate given packets are sent ~550ms
        double averageDuration = totalDuration / (double) tickDurations.size();
        return Math.min(20.0, 20.0 * (550 / averageDuration));
    }

    /**
     * Resets the measurements when the player disconnects.
     * 
     * @param client The Minecraft client instance.
     */
    public void resetMeasurements(ClientPlayNetworkHandler handler, MinecraftClient client) {
        lastKeepAliveTime = -1;
        tickDurations.clear();
        ping = 0;
        lastPingTime = 0;
        awaitingPing = false;
        pingTickCounter = 0;
        tps = 20.0;
    }

    /**
     * Updates the overlay with the current server status information.
     */
    private void updateOverlay() {
        if (!NarConfig.getHandler().general.serverStatus)
            return;

        // Update the overlay lines with the current values
        LINES.get(0).setText("§7" + x + ", " + y + ", " + z);
        LINES.get(1)
                .setText("§7" + String.format("%.2f", yaw) + " / " + String.format("%.2f", pitch));
        LINES.get(2).setText("§7" + direction);

        // Update ping line with color coding
        String pingColor =
                ping < 50 ? "§a" : ping < 100 ? "§2" : ping < 200 ? "§e" : ping < 400 ? "§c" : "§4";
        LINES.get(3).setText(pingColor + ping + " §7ms");

        // Update FPS with color coding
        int maxFps = MinecraftClient.getInstance().options.getMaxFps().getValue();
        String fpsColor = fps >= maxFps * 0.9 ? "§a"
                : fps >= maxFps * 0.7 ? "§2"
                        : fps >= maxFps * 0.6 ? "§e" : fps >= maxFps * 0.5 ? "§c" : "§4";
        LINES.get(4).setText(fpsColor + fps + " §7fps");

        // Update TPS with color coding
        String tpsColor = tps >= 19.0 ? "§a"
                : tps >= 15.0 ? "§2" : tps >= 10.0 ? "§e" : tps >= 5.0 ? "§c" : "§4";
        LINES.get(5).setText(tpsColor + String.format("%.2f", tps) + " §7tps");

        // Update CPS with color coding
        String leftCpsColor = leftCps < 3 ? "§a"
                : leftCps < 6 ? "§2" : leftCps < 10 ? "§e" : leftCps < 16 ? "§c" : "§4";
        String rightCpsColor = rightCps < 3 ? "§a"
                : rightCps < 6 ? "§2" : rightCps < 10 ? "§e" : rightCps < 16 ? "§c" : "§4";
        LINES.get(6).setText(leftCpsColor + leftCps + " §7: " + rightCpsColor + rightCps);

        // Update day with color coding
        String dayColor =
                day < 0.25 ? "§a" : day < 3 ? "§2" : day < 7 ? "§e" : day < 14 ? "§c" : "§4";
        LINES.get(7).setText(dayColor + String.format("%.2f", day));

        // Update the overlay with the new lines
        OVERLAY.setChanged();
    }

    /**
     * Handles mouse click events to update CPS. * @param button The mouse button that was clicked
     * (0 for left, 1 for right).
     * 
     * @param action The action of the click (1 for press, 0 for release).
     */
    public void onClick(int button, int action) {
        if (action != 1)
            return;

        if (button == 0) {
            leftCps++;
            ScheduleUtil.schedule(() -> leftCps--, 20);
        } else if (button == 1) {
            rightCps++;
            ScheduleUtil.schedule(() -> rightCps--, 20);
        }
    }

    /**
     * Gets the current ping of the client. * @return The current ping in milliseconds.
     */
    public int getPing() {
        return ping;
    }

    /**
     * Gets the current TPS of the server. * @return The current TPS of the server.
     */
    public double getTps() {
        return tps;
    }
}
