package net.volcaronitee.taraton.feature.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonList;
import net.volcaronitee.taraton.util.PartyUtil;
import net.volcaronitee.taraton.util.ScheduleUtil;

/**
 * Feature to handle joining parties based on whitelist or last party members.
 */
public class JoinParty {
    private static final JoinParty INSTANCE = new JoinParty();

    public static final TaratonList WHITE_LIST = new TaratonList("White List",
            Text.literal("A list of players to allow the use of various features."),
            "white_list.json", new String[] {"Username"});

    private static final Pattern PARTY_INVITE_PATTERN = Pattern.compile(
            "-+\\n(?:\\[[^\\]]*\\+?\\] )?(\\w+) has invited you to join their party!\\nYou have \\d+ seconds to accept\\. Click here to join!\\n-+");

    /**
     * Private constructor to prevent instantiation.
     */
    private JoinParty() {}

    /**
     * Registers the join party handler to listen for game chat messages.
     */
    public static void register() {
        ClientReceiveMessageEvents.GAME.register(INSTANCE::handleJoinParty);
    }

    /**
     * Handles the join whitelist logic when a message is received.
     * 
     * @param message The message received from the game chat.
     * @param overlay Whether the message is an overlay message.
     */
    private void handleJoinParty(Text message, boolean overlay) {
        if (overlay) {
            return;
        }

        // Check if the message is a party invite
        String text = message.getString();
        Matcher matcher = PARTY_INVITE_PATTERN.matcher(text);
        if (!matcher.matches()) {
            return;
        }

        // Use username from the matcher group
        String username = matcher.group(1);
        handleJoinWhitelist(username);
        handleJoinReparty(username);
    }

    /**
     * Handles the join whitelist logic for a player.
     * 
     * @param player The name of the player who sent the invite.
     */
    private void handleJoinWhitelist(String player) {
        if (!TaratonConfig.getHandler().chat.joinWhitelist) {
            return;
        }

        if (WHITE_LIST.list.contains(player)) {
            ScheduleUtil.scheduleCommand("party accept " + player);
        }
    }

    /**
     * Handles the join reparty logic for a player.
     * 
     * @param player The name of the player who sent the invite.
     */
    private void handleJoinReparty(String player) {
        if (!TaratonConfig.getHandler().chat.joinReparty) {
            return;
        }

        if (PartyUtil.getLastParty().contains(player)) {
            ScheduleUtil.scheduleCommand("party accept " + player);
        }
    }
}
