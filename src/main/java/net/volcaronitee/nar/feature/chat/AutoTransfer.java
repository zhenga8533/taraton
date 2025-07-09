package net.volcaronitee.nar.feature.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.volcaronitee.nar.util.ScheduleUtil;

/**
 * Feature to automatically transfer party leadership when the current player is transferred.
 */
public class AutoTransfer {
    private static final AutoTransfer INSTANCE = new AutoTransfer();

    private static final Pattern AUTO_TRANSFER_PATTERN =
            Pattern.compile("^The party was transferred to (.*?) by (.*?)$");

    public static void register() {
        ClientReceiveMessageEvents.GAME.register(INSTANCE::handleAutoTransfer);
    }

    /**
     * Handles the auto transfer feature by processing incoming messages.
     *
     * @param message The incoming message to check.
     * @param overlay Whether the message is an overlay message.
     */
    private void handleAutoTransfer(Text message, boolean overlay) {
        if (overlay) {
            return;
        }

        // Check if the message matches the auto transfer pattern
        Matcher matcher = AUTO_TRANSFER_PATTERN.matcher(message.getString());
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
}
