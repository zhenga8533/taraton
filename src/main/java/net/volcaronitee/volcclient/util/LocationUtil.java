package net.volcaronitee.volcclient.util;

import net.hypixel.data.type.LobbyType;
import net.hypixel.data.type.ServerType;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

/**
 * Utility class for detecting world and location information.
 */
public class LocationUtil {
    private static final LocationUtil INSTANCE = new LocationUtil();

    private String serverName = "";
    private String lobbyName = "";
    private String map = "";
    private String mode = "";
    private ServerType serverType = LobbyType.MAIN;
    private World world = World.UNKNOWN;

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
            return null;
        }
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private LocationUtil() {}

    /**
     * Returns the singleton instance of LocationUtil.
     * 
     * @return The LocationUtil instance.
     */
    public static LocationUtil getInstance() {
        return INSTANCE;
    }

    public static void register() {
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);
        HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class,
                LocationUtil::handleLocationPacket);
    }

    /**
     * Handles the ClientboundLocationPacket to update the location information. This method
     * extracts the server name, lobby name, map, mode, server type, and version from the packet.
     * 
     * @param packet The ClientboundLocationPacket containing the location data.
     */
    private static void handleLocationPacket(ClientboundLocationPacket packet) {
        INSTANCE.serverName = packet.getServerName();
        INSTANCE.lobbyName = packet.getLobbyName().orElse("");
        INSTANCE.map = packet.getMap().orElse("");
        INSTANCE.mode = packet.getMode().orElse("");
        INSTANCE.serverType = packet.getServerType().orElse(LobbyType.MAIN);
        INSTANCE.world = World.fromInternalName(INSTANCE.map);
    }

    /**
     * Returns the name of the server.
     * 
     * @return The name of the server.
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Returns the name of the lobby.
     * 
     * @return The name of the lobby, or an empty string if not available.
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Returns the name of the map.
     * 
     * @return The name of the map, or an empty string if not available.
     */
    public String getMap() {
        return map;
    }

    /**
     * Returns the mode of the game.
     * 
     * @return The mode of the game, or an empty string if not available.
     */
    public String getMode() {
        return mode;
    }

    /**
     * Returns the type of the server.
     * 
     * @return The type of the server, or UNKNOWN if not available.
     */
    public ServerType getServerType() {
        return serverType;
    }

    /**
     * Returns the current world based on the map name.
     * 
     * @return The World enum constant representing the current world, or UNKNOWN if not recognized.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Returns a debug message containing the current location information.
     * 
     * @return A formatted string with the server, lobby, map, mode, type, and version.
     */
    public static String debugLocation() {
        String debugMessage = String.format(
                "Location Info:\nServer: %s\nLobby: %s\nMap: %s\nMode: %s\nType: %s\nWorld: %s",
                INSTANCE.serverName, INSTANCE.lobbyName, INSTANCE.map, INSTANCE.mode,
                INSTANCE.serverType.name(), INSTANCE.world.name());
        return debugMessage;
    }
}
