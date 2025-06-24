package net.volcaronitee.volcclient.feature.chat;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.JsonUtil;
import net.volcaronitee.volcclient.util.ListUtil;
import net.volcaronitee.volcclient.util.ParseUtil;
import net.volcaronitee.volcclient.util.PartyUtil;
import net.volcaronitee.volcclient.util.ToggleUtil;

/**
 * Feature for handling chat commands in the game.
 */
public class ChatCommands {
    private static final JsonObject PREFIX_JSON = JsonUtil.loadTemplate("prefix.json");
    private static final List<String> DEFAULT_LIST = JsonUtil.parseList(PREFIX_JSON, "prefix");
    public static final ListUtil PREFIX_MAP = new ListUtil("Prefix List",
            Text.literal("A list of prefixes to detect for chat commands."), "prefix_list.json",
            DEFAULT_LIST, null);

    private static final Pattern ALL_PATTERN = Pattern.compile(ParseUtil.PLAYER_PATTERN + ": (.+)");
    private static final Pattern GUILD_PATTERN =
            Pattern.compile("Guild > " + ParseUtil.PLAYER_PATTERN + ": (.+)");
    private static final Pattern PARTY_PATTERN =
            Pattern.compile("Party > " + ParseUtil.PLAYER_PATTERN + ": (.+)");
    private static final Pattern PRIVATE_PATTERN =
            Pattern.compile("From " + ParseUtil.PLAYER_PATTERN + ": (.+)");

    private enum CommandType {
        ALL, GUILD, PARTY, PRIVATE
    }

    /**
     * Registers the chat command feature to listen for game messages.
     */
    public static void register() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            handleChatCommand(ParseUtil.removeFormatting(message.getString()));
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
        Matcher privateMatcher = PRIVATE_PATTERN.matcher(text);

        String username;
        String message;
        CommandType commandType;

        if (ToggleUtil.getHandler().chat.allChat && allMatcher.matches()) {
            username = allMatcher.group(1);
            message = allMatcher.group(2);
            commandType = CommandType.ALL;
        } else if (ToggleUtil.getHandler().chat.guildChat && guildMatcher.matches()) {
            username = guildMatcher.group(1);
            message = guildMatcher.group(2);
            commandType = CommandType.GUILD;
        } else if (ToggleUtil.getHandler().chat.partyChat && partyMatcher.matches()) {
            username = partyMatcher.group(1);
            message = partyMatcher.group(2);
            commandType = CommandType.PARTY;
        } else if (ToggleUtil.getHandler().chat.privateChat && privateMatcher.matches()) {
            username = privateMatcher.group(1);
            message = privateMatcher.group(2);
            commandType = CommandType.PRIVATE;

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
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        String[] args = text.split(" ");
        handleLeaderCommand(player, username, args);
        handlePartyCommand(player, username, commandType, args);
        handleStatusCommand(player, username, commandType, args);
    }

    /**
     * Handles the leader command logic when a message is received.
     * 
     * @param player The player who sent the command.
     * @param username The username of the player who sent the command.
     * @param args The arguments of the command.
     */
    private static void handleLeaderCommand(ClientPlayerEntity player, String username,
            String[] args) {
        // Verify if the player is the party leader and if leader commands are enabled
        String partyLeader = PartyUtil.getInstance().getLeader();
        String clientUsername = MinecraftClient.getInstance().getSession().getUsername();
        if (!ConfigUtil.getHandler().chat.leaderCommands || partyLeader != clientUsername) {
            return;
        }

        String command = args.length > 0 ? args[0].toLowerCase() : "";
        String args1 = args.length > 1 ? args[1] : null;

        switch (command) {
            case "allinvite":
            case "allinv":
                if (!ToggleUtil.getHandler().chat.allInvite) {
                    return;
                }

                break;
            case "mute":
                if (!ToggleUtil.getHandler().chat.mute) {
                    return;
                }

                break;
            case "streamopen":
            case "stream":
                if (!ToggleUtil.getHandler().chat.stream) {
                    return;
                }

                break;
            case "warp":
                if (!ToggleUtil.getHandler().chat.warp) {
                    return;
                }

                break;
            case "instance":
            case "join":
                if (!ToggleUtil.getHandler().chat.instance) {
                    return;
                }

                break;
            case "invite":
            case "inv":
                if (!ToggleUtil.getHandler().chat.invite) {
                    return;
                }

                break;
            case "kick":
                if (!ToggleUtil.getHandler().chat.kick) {
                    return;
                }

                break;
            case "transfer":
            case "ptme":
            case "pm":
                if (!ToggleUtil.getHandler().chat.transfer) {
                    return;
                }

                break;
            case "promote":
                if (!ToggleUtil.getHandler().chat.promote) {
                    return;
                }

                break;
            case "demote":
                if (!ToggleUtil.getHandler().chat.demote) {
                    return;
                }

                break;
            case "help":
            case "leaderhelp":
            case "lhelp":
                if (!ToggleUtil.getHandler().chat.leaderHelp) {
                    return;
                }

                break;
        }
    }

    /**
     * Handles the party command logic when a message is received.
     * 
     * @param player The player who sent the command.
     * @param username The username of the player who sent the command.
     * @param commandType The type of command (ALL, GUILD, PARTY).
     * @param args The arguments of the command.
     */
    private static void handlePartyCommand(ClientPlayerEntity player, String username,
            CommandType commandType, String[] args) {
        if (!ConfigUtil.getHandler().chat.partyCommands) {
            return;
        }

        String command = args[0];

        switch (command) {
            case "8ball":
                if (!ToggleUtil.getHandler().chat.eightBall) {
                    return;
                }

                break;
            case "coin":
            case "flip":
            case "coinflip":
            case "cf":
                if (!ToggleUtil.getHandler().chat.coinFlip) {
                    return;
                }

                break;
            case "dice":
            case "roll":
                if (!ToggleUtil.getHandler().chat.diceRoll) {
                    return;
                }

                break;
            case "waifu":
            case "women":
            case "w":
                if (!ToggleUtil.getHandler().chat.waifu) {
                    return;
                }

                break;
            case "help":
            case "partyhelp":
            case "phelp":
                if (!ToggleUtil.getHandler().chat.partyHelp) {
                    return;
                }

                break;
        }
    }

    /**
     * Handles the status command logic when a message is received.
     * 
     * @param player The player who sent the command.
     * @param username The username of the player who sent the command.
     * @param commandType The type of command (ALL, GUILD, PARTY).
     * @param args The arguments of the command.
     */
    private static void handleStatusCommand(ClientPlayerEntity player, String username,
            CommandType commandType, String[] args) {
        if (!ConfigUtil.getHandler().chat.statusCommands) {
            return;
        }

        String command = args[0];

        switch (command) {
            case "coords":
            case "waypoint":
            case "xyz":
                if (!ToggleUtil.getHandler().chat.coords) {
                    return;
                }

                break;
            case "fps":
                if (!ToggleUtil.getHandler().chat.fps) {
                    return;
                }

                break;
            case "tps":
                if (!ToggleUtil.getHandler().chat.tps) {
                    return;
                }

                break;
            case "leave":
                if (!ToggleUtil.getHandler().chat.leave) {
                    return;
                }

                break;
            case "limbo":
            case "lobby":
            case "l":
                if (!ToggleUtil.getHandler().chat.limbo) {
                    return;
                }

                break;
            case "ping":
                if (!ToggleUtil.getHandler().chat.ping) {
                    return;
                }

                break;
            case "playtime":
            case "pt":
                if (!ToggleUtil.getHandler().chat.playtime) {
                    return;
                }

                break;
            case "stats":
            case "stat":
                if (!ToggleUtil.getHandler().chat.stats) {
                    return;
                }

                break;
            case "time":
                if (!ToggleUtil.getHandler().chat.time) {
                    return;
                }

                break;
            case "help":
            case "statushelp":
            case "shelp":
                if (!ToggleUtil.getHandler().chat.statusHelp) {
                    return;
                }

                break;
        }
    }
}
