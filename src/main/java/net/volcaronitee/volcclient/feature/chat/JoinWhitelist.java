package net.volcaronitee.volcclient.feature.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.ListUtil;
import net.volcaronitee.volcclient.util.ScheduleUtil;

/**
 * JoinWhitelist is a feature that automatically accepts party invites
 */
public class JoinWhitelist {
    private static final JoinWhitelist INSTANCE = new JoinWhitelist();

    public static final ListUtil WHITE_LIST = new ListUtil("White List",
            Text.literal("A list of players to automatically accept party invites from."),
            "white_list.json");

    private static final Pattern PARTY_INVITE_PATTERN = Pattern.compile(
            "-+\\n(?:\\[[^\\]]*\\+?\\] )?(\\w+) has invited you to join their party!\\nYou have \\d+ seconds to accept\\. Click here to join!\\n-+");

    /**
     * Private constructor to prevent instantiation.
     */
    private JoinWhitelist() {}

    /**
     * Registers the join whitelist feature to listen for game messages.
     */
    public static void register() {
        ClientReceiveMessageEvents.GAME.register(INSTANCE::handleJoinWhitelist);
    }

    /**
     * Handles the join whitelist logic when a message is received.
     * 
     * @param message The message received from the game chat.
     * @param overlay Whether the message is an overlay message.
     */
    private void handleJoinWhitelist(Text message, boolean overlay) {
        if (!ConfigUtil.getHandler().chat.joinWhitelist || overlay) {
            return;
        }

        // Check if the message is a party invite
        String text = message.getString();
        Matcher matcher = PARTY_INVITE_PATTERN.matcher(text);
        if (!matcher.matches()) {
            return;
        }

        // Match username from the invite
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        String username = matcher.group(1);
        if (WHITE_LIST.getHandler().list.stream()
                .anyMatch(pair -> pair.getKey().equals(username))) {
            // Accept the party invite after 4 ticks
            ScheduleUtil.schedule(() -> {
                player.networkHandler.sendChatCommand("party accept " + username);
            }, 4);
        }
    }
}
