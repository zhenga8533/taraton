package net.volcaronitee.nar.feature.general;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.util.ParseUtil;
import net.volcaronitee.nar.util.RenderUtil;
import net.volcaronitee.nar.util.ScheduleUtil;

/**
 * Feature to create waypoints based on messages received in the game.
 */
public class WaypointMaker {
    private static final WaypointMaker INSTANCE = new WaypointMaker();

    private static final Pattern WAYPOINT_PATTERN =
            Pattern.compile(".*" + ParseUtil.PLAYER_PATTERN + ": x: \\d+, y: \\d+, z: \\d+.*");

    private static final List<Waypoint> WAYPOINTS = new ArrayList<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private WaypointMaker() {}

    /**
     * Registers the waypoint maker to listen for rendering events and message reception.
     */
    public static void register() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(INSTANCE::renderWaypoints);
        ClientReceiveMessageEvents.GAME.register(INSTANCE::parseWaypoint);
    }

    /**
     * Parses a waypoint message and adds it to the list of waypoints if valid.
     * 
     * @param message The message received from the game.
     * @param overlay Whether the message is an overlay message.
     */
    private void parseWaypoint(Text message, boolean overlay) {
        int renderWaypoints = NarConfig.getHandler().general.renderWaypoints;
        if (renderWaypoints == 0 || overlay || message == null) {
            return;
        }

        Matcher matcher = WAYPOINT_PATTERN.matcher(message.getString());
        if (!matcher.matches()) {
            return;
        }

        // Parse the player name and coordinates from the message
        String playerName = matcher.group(1);
        int x = ParseUtil.parseInt(matcher.group(2));
        int y = ParseUtil.parseInt(matcher.group(3));
        int z = ParseUtil.parseInt(matcher.group(4));
        float[] rgba = new float[] {0.0f, 1.0f, 0.0f, 1.0f};
        Text waypointName = Text.literal(playerName).formatted(Formatting.GREEN);

        // Create a new waypoint with timeout
        Waypoint waypoint = new Waypoint(waypointName, x, y, z, rgba);
        WAYPOINTS.add(waypoint);
        ScheduleUtil.schedule(() -> {
            if (WAYPOINTS.contains(waypoint)) {
                WAYPOINTS.remove(waypoint);
            }
        }, renderWaypoints * 20);
    }

    /**
     * Renders beacon beams in the world.
     */
    private void renderWaypoints(WorldRenderContext context) {
        for (Waypoint waypoint : WAYPOINTS) {
            waypoint.render(context);
        }
    }

    /**
     * Represents a waypoint with a name, position, and color.
     */
    private class Waypoint {
        private final Text name;
        private final BlockPos pos;
        private final float[] rgba;

        /**
         * Constructs a new Waypoint instance.
         */
        public Waypoint(Text name, int x, int y, int z, float[] rgba) {
            this.name = name;
            this.pos = new BlockPos(x, y, z);
            this.rgba = rgba;
        }

        /**
         * Renders the waypoint in the world.
         * 
         * @param context The rendering context.
         */
        public void render(WorldRenderContext context) {
            RenderUtil.renderWaypoint(context, pos, name, rgba, true);
        }
    }
}
