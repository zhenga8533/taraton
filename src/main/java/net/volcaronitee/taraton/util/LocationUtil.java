package net.volcaronitee.taraton.util;

import java.util.ArrayList;
import java.util.List;
import net.hypixel.data.type.LobbyType;
import net.hypixel.data.type.ServerType;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/**
 * Utility class for detecting world and location information.
 */
public class LocationUtil {
    private static String serverName = "";
    private static String lobbyName = "";
    private static String map = "";
    private static String mode = "";
    private static ServerType serverType = LobbyType.MAIN;
    private static World world = World.UNKNOWN;
    private static Text zone = Text.empty();

    private static final List<Runnable> CALLBACKS = new ArrayList<>();

    /**
     * Enum representing different worlds in Hypixel SkyBlock.
     */
    public enum World {
        // @formatter:off
        UNKNOWN("unknown"),
        PRIVATE_ISLAND("dynamic"),
        GARDEN("garden"),
        HUB("hub"),
        DARK_AUCTION("dark_auction"),
        DUNGEON_HUB("dungeon_hub"),
        THE_FARMING_ISLANDS("farming_1"),
        THE_PARK("foraging_1"),
        GALATEA("galatea"),
        SPIDERS_DEN("combat_1"),
        BLAZING_FORTRESS("combat_2"),
        THE_END("combat_3"),
        CRIMSON_ISLE("crimson_isle"),
        GOLD_MINE("mining_1"),
        DEEP_CAVERN("mining_2"),
        DWARVEN_MINES("mining_3"),
        CRYSTAL_HOLLOWS("crystal_hollows"),
        BACKWATER_BAYOU("fishing_1"),
        WINTER_ISLAND("winter"),
        THE_RIFT("rift"),
        DUNGEON("dungeon"),
        KUUDRA("kuudra");
        // @formatter:on

        private final String internalName;

        /**
         * Constructor for the World enum.
         * 
         * @param internalName The internal name of the world used in the Hypixel API.
         */
        World(String internalName) {
            this.internalName = internalName;
        }

        /**
         * Returns the internal name of the world.
         * 
         * @param name The internal name of the world to match.
         * @return The World enum constant that matches the internal name, or null if not found.
         */
        public static World fromInternalName(String name) {
            for (World world : values()) {
                if (world.internalName.equalsIgnoreCase(name)) {
                    return world;
                }
            }
            return UNKNOWN;
        }
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private LocationUtil() {}

    /**
     * Initializes the LocationUtil by subscribing to the ClientboundLocationPacket
     */
    public static void init() {
        TickUtil.register(LocationUtil::updateZone, 20);
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);
        HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class,
                LocationUtil::handleLocationPacket);
    }

    /**
     * Registers a callback to be executed when the location information is updated.
     * 
     * @param callback The Runnable callback to register.
     */
    public static void registerCallback(Runnable callback) {
        CALLBACKS.add(callback);
    }

    /**
     * Updates the zone information based on the current server type and world.
     * 
     * @param client The Minecraft client instance used to access the world and server information.
     */
    private static void updateZone(MinecraftClient client) {
        List<Text> sidebarLines = ScoreboardUtil.getScoreboard();
        if (sidebarLines.isEmpty()) {
            return;
        }

        zone = sidebarLines.stream().filter(line -> {
            String plainText = line.getString().trim();
            return plainText.startsWith("⏣") || plainText.startsWith("ф");
        }).findFirst().orElse(Text.empty());
    }

    /**
     * Handles the ClientboundLocationPacket to update the location information. This method
     * extracts the server name, lobby name, map, mode, server type, and version from the packet.
     * 
     * @param packet The ClientboundLocationPacket containing the location data.
     */
    private static void handleLocationPacket(ClientboundLocationPacket packet) {
        serverName = packet.getServerName();
        lobbyName = packet.getLobbyName().orElse("");
        map = packet.getMap().orElse("");
        mode = packet.getMode().orElse("");
        serverType = packet.getServerType().orElse(LobbyType.MAIN);
        world = World.fromInternalName(map);

        // Execute all registered callbacks
        for (Runnable callback : CALLBACKS) {
            callback.run();
        }
    }

    /**
     * Returns the name of the server.
     * 
     * @return The name of the server.
     */
    public static String getServerName() {
        return serverName;
    }

    /**
     * Returns the name of the lobby.
     * 
     * @return The name of the lobby, or an empty string if not available.
     */
    public static String getLobbyName() {
        return lobbyName;
    }

    /**
     * Returns the name of the map.
     * 
     * @return The name of the map, or an empty string if not available.
     */
    public static String getMap() {
        return map;
    }

    /**
     * Returns the mode of the game.
     * 
     * @return The mode of the game, or an empty string if not available.
     */
    public static String getMode() {
        return mode;
    }

    /**
     * Returns the type of the server.
     * 
     * @return The type of the server, or UNKNOWN if not available.
     */
    public static ServerType getServerType() {
        return serverType;
    }

    /**
     * Returns the current world based on the map name.
     * 
     * @return The World enum constant representing the current world, or UNKNOWN if not recognized.
     */
    public static World getWorld() {
        return world;
    }

    /**
     * Returns the zone of the server.
     * 
     * @return The zone of the server as a Text object, or an empty Text if not available.
     */
    public static Text getZone() {
        return zone;
    }

    /**
     * Returns a debug message containing the current location information.
     * 
     * @return A formatted string with the server, lobby, map, mode, type, and version.
     */
    public static String debugLocation() {
        String debugMessage = String.format(
                "Location Info:\nServer: %s\nLobby: %s\nMap: %s\nMode: %s\nType: %s\nWorld: %s\nZone: %s",
                serverName, lobbyName, map, mode, serverType.name(), world.name(), zone);
        return debugMessage;
    }
}
