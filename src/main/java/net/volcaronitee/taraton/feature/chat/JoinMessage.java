package net.volcaronitee.taraton.feature.chat;

import java.util.regex.Matcher;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.util.ParseUtil;
import net.volcaronitee.taraton.util.PartyUtil;
import net.volcaronitee.taraton.util.ScheduleUtil;

/**
 * Handles join messages for parties and guilds in the game chat.
 */
public class JoinMessage {
    private static final JoinMessage INSTANCE = new JoinMessage();

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
        String joinMessage = TaratonConfig.getHandler().chat.partyJoinMessage;
        MinecraftClient client = MinecraftClient.getInstance();
        if (joinMessage.isEmpty() || client == null || client.getSession() == null) {
            return;
        }

        // Check if the player is the party leader
        String clientUsername = client.getSession().getUsername();
        if (TaratonConfig.getHandler().chat.partyLeaderOnly
                && !PartyUtil.getLeader().equals(clientUsername)) {
            return;
        }

        // Check if the message matches the party join pattern
        Matcher matcher = ParseUtil.PARTY_JOIN_PATTERN.matcher(text);
        if (matcher.matches()) {
            String playerName = matcher.group(1);
            joinMessage = joinMessage.replace("{player}", playerName);
            ScheduleUtil.scheduleCommand("pc " + joinMessage);
        }
    }

    /**
     * Handles the guild join message and sends a custom message if configured.
     * 
     * @param text The text of the join message.
     */
    private void handleGuildJoin(String text) {
        String joinMessage = TaratonConfig.getHandler().chat.guildJoinMessage;
        MinecraftClient client = MinecraftClient.getInstance();
        if (joinMessage.isEmpty() || client == null || client.getSession() == null) {
            return;
        }

        // Check if the message matches the guild join pattern
        Matcher matcher = ParseUtil.GUILD_JOIN_PATTERN.matcher(text);
        if (matcher.matches()) {
            String playerName = matcher.group(1);
            joinMessage = joinMessage.replace("{player}", playerName);
            ScheduleUtil.scheduleCommand("gc " + joinMessage);
        }
    }
}
