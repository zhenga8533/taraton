package net.volcaronitee.nar.feature.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.util.PartyUtil;

/**
 * Handles join messages for parties and guilds in the game chat.
 */
public class JoinMessage {
    private static final JoinMessage INSTANCE = new JoinMessage();

    private static final Pattern PARTY_JOIN_PATTERN =
            Pattern.compile("^(.*?)\\sjoined the party\\.$");
    private static final Pattern GUILD_JOIN_PATTERN =
            Pattern.compile("^(.*?)\\sjoined the guild\\.$");

    /**
     * Registers the join message handler to listen for game chat messages.
     */
    public static void register() {
        ClientReceiveMessageEvents.GAME.register(INSTANCE::handleJoinMessage);
    }

    /**
     * Handles the join message received from the game chat.
     * 
     * @param message The message received from the game chat.
     * @param overlay True if the message is an overlay message, false otherwise.
     */
    private void handleJoinMessage(Text message, boolean overlay) {
        if (overlay) {
            return;
        }

        String text = message.getString();
        handlePartyJoin(text);
        handleGuildJoin(text);
    }

    /**
     * Handles the party join message and sends a custom message if configured.
     * 
     * @param text The text of the join message.
     */
    private void handlePartyJoin(String text) {
        String joinMessage = NarConfig.getHandler().chat.partyJoinMessage;
        MinecraftClient client = MinecraftClient.getInstance();
        if (joinMessage.isEmpty() || client == null || client.getSession() == null) {
            return;
        }

        // Check if the player is the party leader
        String clientUsername = client.getSession().getUsername();
        if (NarConfig.getHandler().chat.partyLeaderOnly
                && !PartyUtil.getLeader().equals(clientUsername)) {
            return;
        }

        // Check if the message matches the party join pattern
        Matcher matcher = PARTY_JOIN_PATTERN.matcher(text);
        if (matcher.matches()) {
            String playerName = matcher.group(1);
            joinMessage = joinMessage.replace("{player}", playerName);
            client.getNetworkHandler().sendChatCommand("pc " + joinMessage);
        }
    }

    /**
     * Handles the guild join message and sends a custom message if configured.
     * 
     * @param text The text of the join message.
     */
    private void handleGuildJoin(String text) {
        String joinMessage = NarConfig.getHandler().chat.guildJoinMessage;
        MinecraftClient client = MinecraftClient.getInstance();
        if (joinMessage.isEmpty() || client == null || client.getSession() == null) {
            return;
        }

        // Check if the message matches the guild join pattern
        Matcher matcher = GUILD_JOIN_PATTERN.matcher(text);
        if (matcher.matches()) {
            String playerName = matcher.group(1);
            joinMessage = joinMessage.replace("{player}", playerName);
            client.getNetworkHandler().sendChatCommand("gc " + joinMessage);
        }
    }
}
