package net.volcaronitee.nar.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.hypixel.data.rank.MonthlyPackageRank;
import net.hypixel.data.rank.PackageRank;
import net.hypixel.data.rank.PlayerRank;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.HypixelPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPlayerInfoPacket;
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPlayerInfoPacket;
import net.hypixel.modapi.serializer.PacketSerializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Utility class for handling player-related operations in Hypixel.
 */
public class PlayerUtil {
    private static MonthlyPackageRank monthlyPackageRank = MonthlyPackageRank.NONE;
    private static PackageRank packageRank = PackageRank.NONE;
    private static PlayerRank playerRank = PlayerRank.NORMAL;
    private static String prefix = "";

    private static final long AFK_THRESHOLD = 5 * 60 * 1000L;
    private static long lastActivityTime = 0L;

    /**
     * Private constructor to prevent instantiation.
     */
    private PlayerUtil() {}

    /**
     * Registers the packet listener for Hypixel player updates.
     */
    public static void init() {
        HypixelModAPI.getInstance().createHandler(ClientboundPlayerInfoPacket.class,
                PlayerUtil::handlePlayerPacket);

        ClientPlayConnectionEvents.JOIN.register(PlayerUtil::sendPlayerPacket);
    }

    /**
     * Sends a packet to the Hypixel server to request player information. This method is
     * 
     * @param handler The ClientPlayNetworkHandler used to send the packet.
     * @param sender The PacketSender used to send the packet.
     * @param client The Minecraft client instance used to send the packet.
     */
    private static void sendPlayerPacket(ClientPlayNetworkHandler handler, PacketSender sender,
            MinecraftClient client) {
        // Check if the client and current server entry are valid
        if (handler == null || sender == null || client == null
                || client.getCurrentServerEntry() == null) {
            return;
        }

        // Ensure the server is Hypixel before sending the packet
        String serverAddress = client.getCurrentServerEntry().address;
        if (!serverAddress.toLowerCase().contains("hypixel.net")) {
            return;
        }

        HypixelPacket packet = new ServerboundPlayerInfoPacket();
        PacketByteBuf buf = PacketByteBufs.create();
        packet.write(new PacketSerializer(buf));
        HypixelModAPI.getInstance().sendPacket(packet);
    }

    /**
     * Handles the ClientboundPartyPacket and updates player-related information. This method
     * extracts the player's monthly package rank, package rank, player rank, and prefix from the
     * packet.
     * 
     * @param packet The party packet from Hypixel.
     */
    private static void handlePlayerPacket(ClientboundPlayerInfoPacket packet) {
        monthlyPackageRank = packet.getMonthlyPackageRank();
        packageRank = packet.getPackageRank();
        playerRank = packet.getPlayerRank();
        prefix = packet.getPrefix().orElse("");
    }

    /**
     * Updates the last activity time of the player when they move.
     */
    public static void clientPlayerEntity$move() {
        lastActivityTime = System.currentTimeMillis();
    }

    /**
     * Returns the player's monthly package rank.
     * 
     * @return The MonthlyPackageRank of the player.
     */
    public static MonthlyPackageRank getMonthlyPackageRank() {
        return monthlyPackageRank;
    }

    /**
     * Returns the player's package rank.
     * 
     * @return The PackageRank of the player.
     */
    public static PackageRank getPackageRank() {
        return packageRank;
    }

    /**
     * Returns the player's rank.
     * 
     * @return The PlayerRank of the player.
     */
    public static PlayerRank getPlayerRank() {
        return playerRank;
    }

    /**
     * Returns the player's prefix.
     * 
     * @return The prefix of the player, or an empty string if not available.
     */
    public static String getPrefix() {
        return prefix;
    }

    /**
     * Checks if the player is currently AFK (Away From Keyboard).
     * 
     * @return True if the player is AFK, false otherwise.
     */
    public static boolean isAfk() {
        long currentTime = System.currentTimeMillis();
        return currentTime - lastActivityTime > AFK_THRESHOLD;
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
                monthlyPackageRank.name(), packageRank.name(), playerRank.name(), prefix, isAfk());
        return debugMessage;
    }
}
