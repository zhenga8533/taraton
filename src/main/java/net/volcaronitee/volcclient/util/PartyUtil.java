package net.volcaronitee.volcclient.util;

import java.util.Set;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket.PartyRole;

/**
 * Utility class for tracking the player's party state.
 */
public class PartyUtil {
    private static boolean inParty = false;
    private static String leader = "";
    private static Set<String> moderators = Set.of();
    private static Set<String> members = Set.of();

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
     * Handles the ClientboundPartyPacket and updates party-related information. This method
     * extracts the party leader, moderators, and members from the packet.
     * 
     * @param packet The party packet from Hypixel.
     */
    private static void handlePartyPacket(ClientboundPartyInfoPacket packet) {
        inParty = packet.isInParty();
        if (!inParty) {
            leader = "";
            moderators = Set.of();
            members = Set.of();
            return;
        }

        leader = MojangUtil.getUsernameFromUUID(packet.getLeader().orElse(null));
        packet.getMemberMap().forEach((uuid, member) -> {
            String username = MojangUtil.getUsernameFromUUID(uuid);
            if (member.getRole() == PartyRole.MOD) {
                moderators.add(username);
            } else {
                members.add(username);
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
        return moderators;
    }

    /**
     * Gets the set of usernames of party members.
     * 
     * @return A set of usernames of party members, or an empty set if not in a party.
     */
    public static Set<String> getMembers() {
        return members;
    }

    /**
     * Returns a debug message containing the current party information.
     * 
     * @return A formatted string with party details including leader, moderators, and members.
     */
    public static String debugParty() {
        String debugMessage =
                String.format("Party Info:\nIn Party: %b\nLeader: %s\nModerators: %s\nMembers: %s",
                        inParty, leader, String.join(", ", moderators), String.join(", ", members));
        return debugMessage;
    }
}
