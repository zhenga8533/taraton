package net.volcaronitee.nar.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.HypixelPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket.PartyRole;
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPartyInfoPacket;
import net.hypixel.modapi.serializer.PacketSerializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Utility class for tracking the player's party state.
 */
public class PartyUtil {
    private static boolean inParty = false;
    private static String leader = "";
    private static final Set<String> MODERATORS = new HashSet<>();
    private static final Set<String> MEMBERS = new HashSet<>();

    private static final Map<String, String> UUID_CACHE = new HashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private PartyUtil() {}

    /**
     * Registers the packet listener for Hypixel party updates.
     */
    public static void init() {
        HypixelModAPI.getInstance().createHandler(ClientboundPartyInfoPacket.class,
                PartyUtil::handlePartyPacket);
        TickUtil.register(PartyUtil::sendPacket, 100);
    }

    /**
     * Sends a packet to the Hypixel server to request party information. This method is
     * 
     * @param client The Minecraft client instance used to send the packet.
     */
    public static void sendPacket(MinecraftClient client) {
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler == null) {
            return;
        }

        HypixelPacket packet = new ServerboundPartyInfoPacket();
        PacketByteBuf buf = PacketByteBufs.create();
        packet.write(new PacketSerializer(buf));
        HypixelModAPI.getInstance().sendPacket(packet);
    }

    /**
     * Retrieves the username associated with a given UUID. This method checks a cache to avoid
     * 
     * @param uuid The UUID of the player whose username is to be retrieved.
     * @return The username of the player, or an empty string if the UUID is null or not found.
     */
    public static CompletableFuture<String> getUsernameFromUUID(UUID uuid) {
        if (uuid == null) {
            return CompletableFuture.completedFuture(null);
        }

        String uuidString = MojangUtil.parseUUID(uuid);

        // Check cache first
        if (UUID_CACHE.containsKey(uuidString)) {
            return CompletableFuture.completedFuture(UUID_CACHE.get(uuidString));
        } else {
            return MojangUtil.getUsernameFromUUID(uuidString).thenApply(username -> {
                if (username != null && !username.isEmpty()) {
                    UUID_CACHE.put(uuidString, username);
                }
                return username;
            });
        }
    }

    /**
     * Handles the ClientboundPartyPacket and updates party-related information. This method
     * extracts the party leader, moderators, and members from the packet.
     * 
     * @param packet The party packet from Hypixel.
     */
    private static void handlePartyPacket(ClientboundPartyInfoPacket packet) {
        inParty = packet.isInParty();
        if (!inParty) {
            // Clear party info immediately if not in a party
            MinecraftClient.getInstance().execute(() -> {
                leader = "";
                MODERATORS.clear();
                MEMBERS.clear();
            });
            return;
        }

        // A temporary map to store UUID -> username as they resolve asynchronously
        Map<UUID, String> resolvedUsernames = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> usernameFetchFutures = new ArrayList<>();

        // Handle leader username asynchronously
        packet.getLeader().ifPresent(leaderUuid -> {
            CompletableFuture<Void> leaderFuture =
                    getUsernameFromUUID(leaderUuid).thenAccept(resolvedLeaderUsername -> {
                        if (resolvedLeaderUsername != null) {
                            resolvedUsernames.put(leaderUuid, resolvedLeaderUsername);
                        }
                    }).exceptionally(e -> {
                        return null;
                    });
            usernameFetchFutures.add(leaderFuture);
        });

        // Handle all members' and moderators' usernames asynchronously
        packet.getMemberMap().forEach((uuid, member) -> {
            CompletableFuture<Void> memberFuture =
                    getUsernameFromUUID(uuid).thenAccept(resolvedMemberUsername -> {
                        if (resolvedMemberUsername != null) {
                            resolvedUsernames.put(uuid, resolvedMemberUsername);
                        }
                    }).exceptionally(e -> {
                        return null;
                    });
            usernameFetchFutures.add(memberFuture);
        });

        // Use CompletableFuture.allOf to wait for all username lookups to complete
        CompletableFuture.allOf(usernameFetchFutures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    packet.getLeader().ifPresent(leaderUuid -> {
                        leader = resolvedUsernames.getOrDefault(leaderUuid, "[Unknown Leader]");
                    });

                    List<String> newModerators = new ArrayList<>();
                    List<String> newMembers = new ArrayList<>();

                    // Populate the new lists using the resolved usernames
                    packet.getMemberMap().forEach((uuid, member) -> {
                        String username = resolvedUsernames.getOrDefault(uuid, "[Unknown Player]");
                        if (member.getRole() == PartyRole.MOD) {
                            newModerators.add(username);
                        } else if (member.getRole() == PartyRole.MEMBER) {
                            newMembers.add(username);
                        }
                    });

                    // Clear and add all to ensure thread safety and atomicity of updates
                    MODERATORS.clear();
                    MODERATORS.addAll(newModerators);
                    MEMBERS.clear();
                    MEMBERS.addAll(newMembers);
                });
    }

    /**
     * Checks if the player is currently in a party.
     * 
     * @return True if the player is in a party, false otherwise.
     */
    public static boolean isInParty() {
        return inParty;
    }

    /**
     * Gets the username of the party leader.
     * 
     * @return The username of the party leader, or an empty string if not in a party.
     */
    public static String getLeader() {
        return leader;
    }

    /**
     * Gets the set of usernames of party moderators.
     * 
     * @return A set of usernames of party moderators, or an empty set if not in a party.
     */
    public static Set<String> getModerators() {
        return MODERATORS;
    }

    /**
     * Gets the set of usernames of party members.
     * 
     * @return A set of usernames of party members, or an empty set if not in a party.
     */
    public static Set<String> getMembers() {
        return MEMBERS;
    }

    /**
     * Returns a debug message containing the current party information.
     * 
     * @return A formatted string with party details including leader, moderators, and members.
     */
    public static String debugParty() {
        String debugMessage =
                String.format("Party Info:\nIn Party: %b\nLeader: %s\nModerators: %s\nMembers: %s",
                        inParty, leader, String.join(", ", MODERATORS), String.join(", ", MEMBERS));
        return debugMessage;
    }
}
