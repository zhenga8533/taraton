package net.volcaronitee.volcclient.util;

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
    private ServerType serverType;

    /**
     * Returns the singleton instance of LocationUtil.
     * 
     * @return The LocationUtil instance.
     */
    public static LocationUtil getInstance() {
        return INSTANCE;
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private LocationUtil() {}

    public static void register() {
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);
        HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class,
                LocationUtil::handleLocationPacket);
    }

    /**
     * Handles the ClientboundLocationPacket to update the location information. This method
     * extracts the server name, lobby name, map, mode, server type, and version.
     * 
     * @param packet The ClientboundLocationPacket containing the location data.
     */
    private static void handleLocationPacket(ClientboundLocationPacket packet) {
        INSTANCE.serverName = packet.getServerName();
        INSTANCE.lobbyName = packet.getLobbyName().orElse("");
        INSTANCE.map = packet.getMap().orElse("");
        INSTANCE.mode = packet.getMode().orElse("");
        INSTANCE.serverType = packet.getServerType().orElse(ServerType.valueOf("UNKNOWN").get());
    }

    /**
     * Returns a debug message containing the current location information.
     * 
     * @return A formatted string with the server, lobby, map, mode, type, and version.
     */
    public static String debugLocation() {
        StringBuilder debugMessage = new StringBuilder("Location Info:\n");
        debugMessage.append("Server: ").append(INSTANCE.serverName).append("\n");
        debugMessage.append("Lobby: ").append(INSTANCE.lobbyName).append("\n");
        debugMessage.append("Map: ").append(INSTANCE.map).append("\n");
        debugMessage.append("Mode: ").append(INSTANCE.mode).append("\n");
        debugMessage.append("Type: ").append(INSTANCE.serverType).append("\n");
        return debugMessage.toString();
    }
}
