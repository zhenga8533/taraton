package net.volcaronitee.volcclient.util;

import net.hypixel.data.rank.MonthlyPackageRank;
import net.hypixel.data.rank.PackageRank;
import net.hypixel.data.rank.PlayerRank;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPlayerInfoPacket;

/**
 * Utility class for handling player-related operations in Hypixel.
 */
public class PlayerUtil {
    private static final PlayerUtil INSTANCE = new PlayerUtil();

    private MonthlyPackageRank monthlyPackageRank = MonthlyPackageRank.NONE;
    private PackageRank packageRank = PackageRank.NONE;
    private PlayerRank playerRank = PlayerRank.NORMAL;
    private String prefix = "";

    private static final long AFK_THRESHOLD = 5 * 60 * 1000L;
    private long lastActivityTime = 0L;

    /**
     * Private constructor to prevent instantiation.
     */
    private PlayerUtil() {}

    /**
     * Returns the singleton instance of PlayerUtil.
     * 
     * @return The PlayerUtil instance.
     */
    public static PlayerUtil getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the packet listener for Hypixel player updates.
     */
    public static void register() {
        HypixelModAPI.getInstance().createHandler(ClientboundPlayerInfoPacket.class,
                PlayerUtil::handlePlayerPacket);
    }

    /**
     * Handles the ClientboundPartyPacket and updates player-related information. This method
     * extracts the player's monthly package rank, package rank, player rank, and prefix from the
     * packet.
     * 
     * @param packet The party packet from Hypixel.
     */
    private static void handlePlayerPacket(ClientboundPlayerInfoPacket packet) {
        INSTANCE.monthlyPackageRank = packet.getMonthlyPackageRank();
        INSTANCE.packageRank = packet.getPackageRank();
        INSTANCE.playerRank = packet.getPlayerRank();
        INSTANCE.prefix = packet.getPrefix().orElse("");
    }

    /**
     * Updates the last activity time of the player when they move.
     */
    public void clientPlayerEntity$onPlayerMove() {
        INSTANCE.lastActivityTime = System.currentTimeMillis();
    }

    /**
     * Returns the player's monthly package rank.
     * 
     * @return The MonthlyPackageRank of the player.
     */
    public static MonthlyPackageRank getMonthlyPackageRank() {
        return INSTANCE.monthlyPackageRank;
    }

    /**
     * Returns the player's package rank.
     * 
     * @return The PackageRank of the player.
     */
    public static PackageRank getPackageRank() {
        return INSTANCE.packageRank;
    }

    /**
     * Returns the player's rank.
     * 
     * @return The PlayerRank of the player.
     */
    public static PlayerRank getPlayerRank() {
        return INSTANCE.playerRank;
    }

    /**
     * Returns the player's prefix.
     * 
     * @return The prefix of the player, or an empty string if not available.
     */
    public static String getPrefix() {
        return INSTANCE.prefix;
    }

    /**
     * Checks if the player is currently AFK (Away From Keyboard).
     * 
     * @return True if the player is AFK, false otherwise.
     */
    public static boolean isAFK() {
        long currentTime = System.currentTimeMillis();
        return currentTime - INSTANCE.lastActivityTime > AFK_THRESHOLD;
    }

    /**
     * Returns a debug message containing the current player information.
     * 
     * @return A formatted string with the player's monthly package rank, package rank, player rank,
     *         and prefix.
     */
    public static String debugPlayer() {
        String debugMessage = String.format(
                "Player Info:\nMonthly Package Rank: %s\nPackage Rank: %s\nPlayer Rank: %s\nPrefix: %s\nAFK: %b",
                INSTANCE.monthlyPackageRank.name(), INSTANCE.packageRank.name(),
                INSTANCE.playerRank.name(), INSTANCE.prefix, isAFK());
        return debugMessage;
    }
}
