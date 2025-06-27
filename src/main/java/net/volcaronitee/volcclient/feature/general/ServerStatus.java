package net.volcaronitee.volcclient.feature.general;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Util; // Keep Util for System.currentTimeMillis() equivalent
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.OverlayUtil;
import net.volcaronitee.volcclient.util.OverlayUtil.LineContent;
import net.volcaronitee.volcclient.util.OverlayUtil.Overlay;
import net.volcaronitee.volcclient.util.ScheduleUtil;
import net.volcaronitee.volcclient.util.ToggleUtil;

/**
 * Feature that tracks server status such as ping, FPS, TPS, and player angles.
 */
public class ServerStatus {
    private static final List<LineContent> LINES = List.of(
            new LineContent("§8[§6XYZ§8]§r ", "§7-195, 88, 58",
                    () -> ToggleUtil.getHandler().general.xyz),
            new LineContent("§8[§6Y/P§8]§r ", "§7-89.15 / 30.89",
                    () -> ToggleUtil.getHandler().general.yawPitch),
            new LineContent("§8[§6ANG§8]§r ", "§7East",
                    () -> ToggleUtil.getHandler().general.direction),
            new LineContent("§8[§6PNG§8]§r ", "§a58 §7ms",
                    () -> ToggleUtil.getHandler().general.ping),
            new LineContent("§8[§6FPS§8]§r ", "§a60 §7fps",
                    () -> ToggleUtil.getHandler().general.fps),
            new LineContent("§8[§6TPS§8]§r ", "§a19.8 §7tps",
                    () -> ToggleUtil.getHandler().general.tps),
            new LineContent("§8[§6CPS§8]§r ", "§a0 §7: §a0",
                    () -> ToggleUtil.getHandler().general.cps));
    private static final Overlay OVERLAY = OverlayUtil.createOverlay("server_status",
            () -> ConfigUtil.getHandler().general.serverStatus, LINES);

    private final static ServerStatus INSTANCE = new ServerStatus();

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

    // TPS tracking fields
    private static final int HISTORY_SIZE = 100;
    private final Deque<Long> tickTimestamps = new LinkedList<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private ServerStatus() {}

    /**
     * Gets the singleton instance of ServerStatus. Required for Mixin access.
     */
    public static ServerStatus getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the server status feature to update the overlay.
     */
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(ServerStatus::updateStatus);
    }

    /**
     * Updates the server status information based on the current player state.
     * 
     * @param client The Minecraft client instance.
     */
    private static void updateStatus(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            return;
        }

        ClientPlayerEntity player = client.player;

        // Update XYZ
        INSTANCE.x = (int) player.getX();
        INSTANCE.y = (int) player.getY();
        INSTANCE.z = (int) player.getZ();

        // Update Yaw and Pitch
        INSTANCE.yaw = player.getYaw();
        INSTANCE.pitch = player.getPitch();

        // Update direction
        INSTANCE.direction = player.getHorizontalFacing().name().toLowerCase();

        // Update ping
        PlayerListEntry playerEntry =
                client.player.networkHandler.getPlayerListEntry(client.player.getUuid());
        if (playerEntry != null) {
            INSTANCE.ping = playerEntry.getLatency();
        }

        // TPS is updated via the Mixin directly calling recordServerTick()
        // No need to update INSTANCE.tps here directly, it's already managed.

        // Update FPS
        INSTANCE.fps = client.getCurrentFps();

        // Update the overlay with the new status
        updateOverlay();
    }

    /**
     * Updates the overlay with the current server status information.
     */
    private static void updateOverlay() {
        if (!ConfigUtil.getHandler().general.serverStatus) {
            return;
        }

        LINES.get(0).setText("§7" + INSTANCE.x + ", " + INSTANCE.y + ", " + INSTANCE.z);
        LINES.get(1).setText("§7" + String.format("%.2f", INSTANCE.yaw) + " / "
                + String.format("%.2f", INSTANCE.pitch));
        LINES.get(2).setText("§7" + INSTANCE.direction);
        LINES.get(3).setText("§a" + INSTANCE.ping + " §7ms");
        LINES.get(4).setText("§a" + INSTANCE.fps + " §7fps");
        LINES.get(5).setText("§a" + String.format("%.2f", INSTANCE.tps) + " §7tps");
        LINES.get(6).setText("§a" + INSTANCE.leftCps + " §7: §a" + INSTANCE.rightCps);
        OVERLAY.calcSize();
    }

    /**
     * Handles mouse button clicks to track CPS (Clicks Per Second).
     * 
     * @param button The mouse button that was clicked (0 for left, 1 for right).
     * @param action The action performed on the button (1 for press, 0 for release).
     */
    public static void onClick(int button, int action) {
        // Only handle clicks if the action is "press"
        if (action != 1) {
            return;
        }

        // Increment the CPS count for the respective button
        if (button == 0) {
            INSTANCE.leftCps++;
            ScheduleUtil.schedule(() -> {
                INSTANCE.leftCps--;
            }, 20);
        } else if (button == 1) {
            INSTANCE.rightCps++;
            ScheduleUtil.schedule(() -> {
                INSTANCE.rightCps--;
            }, 20);
        }
    }

    /**
     * Called by Mixin when a WorldTimeUpdateS2CPacket is received. This method records the
     * timestamp and updates the estimated TPS.
     */
    public void recordServerTick() {
        long currentTime = Util.getMeasuringTimeMs(); // Use Minecraft's utility for time

        if (tickTimestamps.size() >= HISTORY_SIZE) {
            tickTimestamps.poll(); // Remove the oldest timestamp
        }
        tickTimestamps.offer(currentTime); // Add the new timestamp

        // Calculate and update the estimated TPS immediately
        if (tickTimestamps.size() < 2) {
            this.tps = 20.0; // Not enough data to calculate an average yet
            return;
        }

        long firstTime = tickTimestamps.peek();
        long lastTime = tickTimestamps.peekLast();
        int numIntervals = tickTimestamps.size() - 1; // Number of intervals between N timestamps

        if (numIntervals > 0) {
            double totalDurationMs = (double) (lastTime - firstTime);
            double averageMsPerTick = totalDurationMs / numIntervals;

            // Cap at 20.0 TPS, and ensure we don't divide by zero or near-zero
            if (averageMsPerTick > 0.1) { // Small threshold to prevent extreme values if time diff
                                          // is too small
                this.tps = Math.min(20.0, 1000.0 / averageMsPerTick);
            } else {
                this.tps = 20.0; // Assume ideal if calculation is problematic
            }
        } else {
            this.tps = 20.0; // Still not enough data for an interval
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
    public static double getTps() {
        return INSTANCE.tps;
    }
}
