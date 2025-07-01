package net.volcaronitee.nar.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket.PartyRole;

/**
 * Utility class for tracking the player's party state.
 */
public class PartyUtil {
    private static boolean inParty = false;
    private static String leader = "";
    private static final Set<String> MODERATORS = Set.of();
    private static final Set<String> MEMBERS = Set.of();

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
    }

    /**
     * Retrieves the username associated with a given UUID. This method checks a cache to avoid
     * 
     * @param uuid The UUID of the player whose username is to be retrieved.
     * @return The username of the player, or an empty string if the UUID is null or not found.
     */
    private static String getUsernameFromUUID(UUID uuid) {
        if (uuid == null) {
            return "";
        }

        String uuidString = MojangUtil.parseUUID(uuid);
        if (UUID_CACHE.containsKey(uuidString)) {
            return UUID_CACHE.get(uuidString);
        } else {
            String username = MojangUtil.getUsernameFromUUID(uuidString);
            UUID_CACHE.put(uuidString, username);
            return username;
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
        System.out.println(packet);
        if (!inParty) {
            leader = "";
            MODERATORS.clear();
            MEMBERS.clear();
            return;
        }

        leader = getUsernameFromUUID(packet.getLeader().orElse(null));

        packet.getMemberMap().forEach((uuid, member) -> {
            String username = getUsernameFromUUID(uuid);
            if (member.getRole() == PartyRole.MOD) {
                MODERATORS.add(username);
            } else if (member.getRole() == PartyRole.MEMBER) {
                MEMBERS.add(username);
            }
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
