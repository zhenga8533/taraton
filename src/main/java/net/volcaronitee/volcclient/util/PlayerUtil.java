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
     * Returns a debug message containing the current player information.
     * 
     * @return A formatted string with the player's monthly package rank, package rank, player rank,
     *         and prefix.
     */
    public static String debugPlayer() {
        StringBuilder debugMessage = new StringBuilder("Player Info:\n");
        debugMessage.append("Monthly Package Rank: ").append(INSTANCE.monthlyPackageRank)
                .append("\n");
        debugMessage.append("Package Rank: ").append(INSTANCE.packageRank).append("\n");
        debugMessage.append("Player Rank: ").append(INSTANCE.playerRank).append("\n");
        debugMessage.append("Prefix: ").append(INSTANCE.prefix).append("\n");
        return debugMessage.toString();
    }
}
