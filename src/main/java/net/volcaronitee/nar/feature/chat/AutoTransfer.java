package net.volcaronitee.nar.feature.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.config.category.ChatConfig;
import net.volcaronitee.nar.util.PartyUtil;
import net.volcaronitee.nar.util.ScheduleUtil;

/**
 * Feature to automatically transfer party leadership when the current player is transferred.
 */
public class AutoTransfer {
    private static final AutoTransfer INSTANCE = new AutoTransfer();

    private static final Pattern AUTO_TRANSFER_PATTERN =
            Pattern.compile("^The party was transferred to (.*?) by (.*?)$");
    private static final Pattern[] KICK_PATTERNS =
            {Pattern.compile("Oops! You are not on SkyBlock so we couldn't warp you!"),
                    Pattern.compile("You were kicked while joining that server!")};

    /**
     * Registers the AutoTransfer feature to listen for incoming messages.
     */
    public static void register() {
        ClientReceiveMessageEvents.GAME.register(INSTANCE::handleAutoTransfer);
    }

    /**
     * Handles the auto transfer feature by checking if the message matches the transfer pattern.
     * 
     * @param message The incoming message to check for transfer patterns.
     * @param overlay Whether the message is an overlay message.
     */
    private void handleAutoTransfer(Text message, boolean overlay) {
        if (overlay) {
            return;
        }

        String text = message.getString();
        handleOnTransfer(text);
        handleOnKick(text);
    }

    /**
     * Handles the case when the party is transferred to a new leader.
     * 
     * @param text The message text to check for transfer patterns.
     */
    private void handleOnTransfer(String text) {
        if (NarConfig.getHandler().chat.autoTransfer != ChatConfig.AutoTransfer.ON_TRANSFER) {
            return;
        }

        // Check if the message matches the auto transfer pattern
        Matcher matcher = AUTO_TRANSFER_PATTERN.matcher(text);
        if (!matcher.matches()) {
            return;
        }

        // Extract the new leader and old leader from the message
        String newLeader = matcher.group(1);
        String oldLeader = matcher.group(2);
        String clientName = MinecraftClient.getInstance().getSession().getUsername();

        // If the new leader is the current client, transfer the party to the old leader
        if (newLeader.equals(clientName)) {
            ScheduleUtil.scheduleCommand("p transfer " + oldLeader);
        }
    }

    /**
     * Handles the case when the player is kicked from the party.
     * 
     * @param text The message text to check for kick patterns.
     */
    private void handleOnKick(String text) {
        if (NarConfig.getHandler().chat.autoTransfer != ChatConfig.AutoTransfer.ON_KICK
                || !PartyUtil.isInParty()) {
            return;
        }

        // Check if the message matches any of the kick patterns
        for (Pattern pattern : KICK_PATTERNS) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.matches()) {
                // If the player is kicked, transfer the party to a random member
                String randomMember = PartyUtil.getRandomMember();

                if (!randomMember.isEmpty()) {
                    ScheduleUtil.scheduleCommand("p transfer " + randomMember);
                }
                return;
            }
        }
    }
}
