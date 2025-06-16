package net.volcaronitee.volcclient.util;

import java.util.Set;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket.PartyRole;

/**
 * Utility class for tracking the player's party state.
 */
public class PartyUtil {
    private static final PartyUtil INSTANCE = new PartyUtil();

    private boolean inParty = false;
    private String leader = "";
    private Set<String> moderators = Set.of();
    private Set<String> members = Set.of();

    /**
     * Private constructor to prevent instantiation.
     */
    private PartyUtil() {}

    /**
     * Returns the singleton instance of PartyUtil.
     * 
     * @return The PartyUtil instance.
     */
    public static PartyUtil getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the packet listener for Hypixel party updates.
     */
    public static void register() {
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
        INSTANCE.inParty = packet.isInParty();
        if (!INSTANCE.inParty) {
            INSTANCE.leader = "";
            INSTANCE.moderators = Set.of();
            INSTANCE.members = Set.of();
            return;
        }

        INSTANCE.leader = MojangUtil.getUsernameFromUUID(packet.getLeader().orElse(null));
        packet.getMemberMap().forEach((uuid, member) -> {
            String username = MojangUtil.getUsernameFromUUID(uuid);
            if (member.getRole() == PartyRole.MOD) {
                INSTANCE.moderators.add(username);
            } else {
                INSTANCE.members.add(username);
            }
        });
    }

    /**
     * Checks if the player is currently in a party.
     * 
     * @return True if the player is in a party, false otherwise.
     */
    public boolean isInParty() {
        return inParty;
    }

    /**
     * Gets the username of the party leader.
     * 
     * @return The username of the party leader, or an empty string if not in a party.
     */
    public String getLeader() {
        return leader;
    }

    /**
     * Gets the set of usernames of party moderators.
     * 
     * @return A set of usernames of party moderators, or an empty set if not in a party.
     */
    public Set<String> getModerators() {
        return moderators;
    }

    /**
     * Gets the set of usernames of party members.
     * 
     * @return A set of usernames of party members, or an empty set if not in a party.
     */
    public Set<String> getMembers() {
        return members;
    }

    /**
     * Returns a debug message containing the current party information.
     * 
     * @return A formatted string with party details including leader, moderators, and members.
     */
    public static String debugParty() {
        StringBuilder debugMessage = new StringBuilder("Party Info:\n");
        debugMessage.append("In Party: ").append(INSTANCE.inParty).append("\n");
        if (INSTANCE.inParty) {
            debugMessage.append("Leader: ").append(INSTANCE.leader).append("\n");
            debugMessage.append("Moderators: ").append(String.join(", ", INSTANCE.moderators))
                    .append("\n");
            debugMessage.append("Members: ").append(String.join(", ", INSTANCE.members))
                    .append("\n");
        }
        return debugMessage.toString();
    }
}
