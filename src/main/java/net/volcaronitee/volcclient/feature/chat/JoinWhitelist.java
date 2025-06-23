package net.volcaronitee.volcclient.feature.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.ListUtil;
import net.volcaronitee.volcclient.util.ScheduleUtil;

/**
 * JoinWhitelist is a feature that automatically accepts party invites
 */
public class JoinWhitelist {
    public static final ListUtil WHITE_LIST = new ListUtil("White List",
            Text.literal("A list of players to automatically accept party invites from."),
            "white_list.json", null, null);

    private static Pattern PARTY_INVITE_PATTERN = Pattern.compile(
            "-+\\n(?:\\[[^\\]]*\\+?\\] )?(\\w+) has invited you to join their party!\\nYou have \\d+ seconds to accept\\. Click here to join!\\n-+");

    /**
     * Registers the join whitelist feature to listen for game messages.
     */
    public static void register() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            handleJoinWhitelist(message);
        });
    }

    /**
     * Handles the join whitelist logic when a message is received.
     * 
     * @param message The message received from the game chat.
     */
    private static void handleJoinWhitelist(Text message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !ConfigUtil.getHandler().chat.joinWhitelist) {
            return;
        }

        // Check if the message is a party invite
        String text = message.getString();
        Matcher matcher = PARTY_INVITE_PATTERN.matcher(text);
        if (!matcher.matches()) {
            return;
        }

        // Match username from the invite
        String username = matcher.group(1);
        if (WHITE_LIST.getHandler().list.contains(username)) {
            // Accept the party invite after 4 ticks
            ScheduleUtil.schedule(() -> {
                client.player.networkHandler.sendChatCommand("party accept " + username);
            }, 4);
        }
    }
}
