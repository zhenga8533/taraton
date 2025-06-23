package net.volcaronitee.volcclient.feature.chat;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.JsonUtil;
import net.volcaronitee.volcclient.util.ListUtil;
import net.volcaronitee.volcclient.util.ParseUtil;

/**
 * Feature for handling chat commands in the game.
 */
public class ChatCommands {
    private static final JsonObject PREFIX_JSON = JsonUtil.loadTemplate("prefix.json");
    private static final List<String> DEFAULT_LIST = JsonUtil.parseList(PREFIX_JSON, "prefix");
    public static final ListUtil PREFIX_MAP = new ListUtil("Prefix List",
            Text.literal("A list of prefixes to detect for chat commands."), "prefix_list.json",
            DEFAULT_LIST, null);

    private static final Pattern ALL_PATTERN =
            Pattern.compile("(?:\\[\\d+\\] )?(?:\\[[^\\]]*\\+?\\] )?(\\w+): (.+)");
    private static final Pattern GUILD_PATTERN =
            Pattern.compile("Guild > (?:\\[[^\\]]*\\+?\\] )?(\\w+)(?: \\[[^\\]]+\\])?: (.+)");
    private static final Pattern PARTY_PATTERN =
            Pattern.compile("Party > (?:\\[[^\\]]*\\+?\\] )?(\\w+): (.+)");

    /**
     * Registers the chat command feature to listen for game messages.
     */
    public static void register() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            handleChatCommand(message.getString());
        });
    }

    /**
     * Handles the chat command logic when a message is received.
     * 
     * @param text The text received from the game chat.
     */
    private static void handleChatCommand(String text) {
        // Check if the text matches any of the defined patterns
        text = ParseUtil.removeFormatting(text);
        Matcher allMatcher = ALL_PATTERN.matcher(text);
        Matcher guildMatcher = GUILD_PATTERN.matcher(text);
        Matcher partyMatcher = PARTY_PATTERN.matcher(text);

        String username;
        String message;

        if (allMatcher.matches()) {
            username = allMatcher.group(1);
            message = allMatcher.group(2);
        } else if (guildMatcher.matches()) {
            username = guildMatcher.group(1);
            message = guildMatcher.group(2);
        } else if (partyMatcher.matches()) {
            username = partyMatcher.group(1);
            message = partyMatcher.group(2);
        } else {
            return;
        }

        // Check if the text starts with any of the defined prefixes
        boolean isCommand = false;
        for (String prefix : PREFIX_MAP.getHandler().list) {
            if (message.startsWith(prefix)) {
                message = message.substring(prefix.length());
                isCommand = true;
                break;
            }
        }
        if (!isCommand) {
            return;
        }

        // Process the command
        String[] args = text.split(" ");
        handleLeaderCommand(username, args);
        handlePartyCommand(username, args);
    }

    /**
     * Handles the leader command logic when a message is received.
     * 
     * @param player The player who sent the command.
     * @param args The arguments of the command.
     */
    private static void handleLeaderCommand(String player, String[] args) {
        if (!ConfigUtil.getHandler().chat.leaderCommands) {
            return;
        }

        // TODO
    }

    /**
     * Handles the party command logic when a message is received.
     * 
     * @param player The player who sent the command.
     * @param args The arguments of the command.
     */
    private static void handlePartyCommand(String player, String[] args) {
        if (!ConfigUtil.getHandler().chat.partyCommands) {
            return;
        }

        // TODO
    }
}
