package net.volcaronitee.nar.feature.chat;

import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.volcaronitee.nar.config.controller.KeyValueController.KeyValuePair;
import net.volcaronitee.nar.feature.general.ServerStatus;
import net.volcaronitee.nar.util.ConfigUtil;
import net.volcaronitee.nar.util.ListUtil;
import net.volcaronitee.nar.util.ParseUtil;
import net.volcaronitee.nar.util.PartyUtil;
import net.volcaronitee.nar.util.ScheduleUtil;
import net.volcaronitee.nar.util.ToggleUtil;

/**
 * Feature for handling chat commands in the game.
 */
public class ChatCommands {
    private static final ChatCommands INSTANCE = new ChatCommands();

    public static final ListUtil PREFIX_MAP = new ListUtil("Prefix List",
            Text.literal("A list of prefixes to detect for chat commands."), "prefix_list.json");

    // Patterns for matching chat messages
    private static final Pattern ALL_PATTERN = Pattern.compile(ParseUtil.PLAYER_PATTERN + ": (.+)");
    private static final Pattern GUILD_PATTERN =
            Pattern.compile("Guild > " + ParseUtil.PLAYER_PATTERN + ": (.+)");
    private static final Pattern PARTY_PATTERN =
            Pattern.compile("Party > " + ParseUtil.PLAYER_PATTERN + ": (.+)");
    private static final Pattern PRIVATE_PATTERN =
            Pattern.compile("From " + ParseUtil.PLAYER_PATTERN + ": (.+)");

    // List of responses for the 8-ball command
    private static final String[] EIGHT_BALL = {"As I see it, yes", "It is certain",
            "It is decidedly so", "Most likely", "Outlook good", "Signs point to yes",
            "Without a doubt", "Yes", "Yes - definitely", "You may rely on it",
            "Reply hazy, try again", "Ask again later", "Better not tell you now",
            "Cannot predict now", "Concentrate and ask again", "Don't count on it",
            "My reply is no", "My sources say no", "Outlook not so good", "Very doubtful"};

    private boolean cooldown = false;

    /**
     * Enum representing the type of chat command.
     */
    private enum CommandType {
        ALL, GUILD, PARTY, PRIVATE;

        /**
         * Returns the command head based on the command type and username.
         * 
         * @param type The type of command.
         * @param username The username of the player sending the command.
         * @return
         */
        private static String getCommandHead(CommandType type, String username) {
            switch (type) {
                case ALL:
                    return "ac";
                case GUILD:
                    return "gc";
                case PARTY:
                    return "pc";
                case PRIVATE:
                    return "msg " + username;
                default:
                    return "";
            }
        }
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private ChatCommands() {}

    /**
     * Registers the chat command feature to listen for game messages.
     */
    public static void register() {
        ClientReceiveMessageEvents.GAME.register(INSTANCE::handleChatCommand);
    }

    /**
     * Handles the chat command logic when a message is received.
     * 
     * @param message The text received from the game chat.
     * @param overlay Whether the message is an overlay message.
     */
    private void handleChatCommand(Text message, boolean overlay) {
        if (overlay) {
            return;
        }

        // Check if the text matches any of the defined patterns
        String text = ParseUtil.removeFormatting(message.getString());
        Matcher allMatcher = ALL_PATTERN.matcher(text);
        Matcher guildMatcher = GUILD_PATTERN.matcher(text);
        Matcher partyMatcher = PARTY_PATTERN.matcher(text);
        Matcher privateMatcher = PRIVATE_PATTERN.matcher(text);

        String username;
        String chatMessage;
        CommandType commandType;

        if (ToggleUtil.getHandler().chat.allChat && allMatcher.matches()) {
            username = allMatcher.group(1);
            chatMessage = allMatcher.group(2);
            commandType = CommandType.ALL;
        } else if (ToggleUtil.getHandler().chat.guildChat && guildMatcher.matches()) {
            username = guildMatcher.group(1);
            chatMessage = guildMatcher.group(2);
            commandType = CommandType.GUILD;
        } else if (ToggleUtil.getHandler().chat.partyChat && partyMatcher.matches()) {
            username = partyMatcher.group(1);
            chatMessage = partyMatcher.group(2);
            commandType = CommandType.PARTY;
        } else if (ToggleUtil.getHandler().chat.privateChat && privateMatcher.matches()) {
            username = privateMatcher.group(1);
            chatMessage = privateMatcher.group(2);
            commandType = CommandType.PRIVATE;
        } else {
            return;
        }

        // Check if the text starts with any of the defined prefixes
        boolean isCommand = false;
        for (KeyValuePair<String, Boolean> prefixPair : PREFIX_MAP.getHandler().list) {
            // Skip prefixes that are disabled
            if (!prefixPair.getValue()) {
                continue;
            }

            String prefix = prefixPair.getKey();
            if (chatMessage.startsWith(prefix)) {
                chatMessage = chatMessage.substring(prefix.length());
                isCommand = true;
                break;
            }
        }
        if (!isCommand) {
            return;
        }

        // Process the command
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        String[] args = chatMessage.split(" ");
        String head = CommandType.getCommandHead(commandType, username);

        if (commandType == CommandType.PARTY) {
            handleLeaderCommand(player, username, args);
        }
        handlePartyCommand(player, username, head, args);
        handleStatusCommand(player, username, head, args);
    }

    /**
     * Schedules a command to be executed after a delay.
     * 
     * @param command The command to be executed.
     */
    private void scheduleCommand(String command) {
        // Prevent command spamming
        if (INSTANCE.cooldown) {
            return;
        }

        // Send the command to the player network handler
        INSTANCE.cooldown = true;
        ScheduleUtil.schedule(() -> {
            MinecraftClient.getInstance().player.networkHandler.sendChatCommand(command);
            INSTANCE.cooldown = false;
        }, 4);
    }

    /**
     * Appends a command to the StringBuilder if the condition is true.
     * 
     * @param builder The StringBuilder to append to.
     * @param command The command to append.
     * @param condition The condition to check before appending the command.
     */
    private void appendCommand(StringBuilder builder, String command, boolean condition) {
        if (condition) {
            builder.append(command + ", ");
        }
    }

    /**
     * Handles the leader command logic when a message is received.
     * 
     * @param player The player who sent the command.
     * @param username The username of the player who sent the command.
     * @param args The arguments of the command.
     */
    private void handleLeaderCommand(ClientPlayerEntity player, String username, String[] args) {
        // Verify if the player is the party leader and if leader commands are enabled
        String partyLeader = PartyUtil.getLeader();
        String clientUsername = MinecraftClient.getInstance().getSession().getUsername();
        if (!ConfigUtil.getHandler().chat.leaderCommands
                || (ToggleUtil.getHandler().chat.leaderLock && partyLeader == clientUsername)
                || !PartyUtil.isInParty() || PartyUtil.getLeader() != clientUsername) {
            return;
        }

        String command = args.length > 0 ? args[0].toLowerCase() : "";
        String arg1 = args.length > 1 ? args[1] : "";

        switch (command) {
            // All invite commands
            case "allinvite":
            case "allinv":
                if (!ToggleUtil.getHandler().chat.allInvite) {
                    return;
                }

                scheduleCommand("p settings allinvite");
                break;
            // Party mute commands
            case "mute":
                if (!ToggleUtil.getHandler().chat.mute) {
                    return;
                }

                scheduleCommand("p mute");
                break;
            // Stream open commands
            case "streamopen":
            case "stream":
                if (!ToggleUtil.getHandler().chat.stream) {
                    return;
                }

                String size = ParseUtil.isNumeric(arg1) ? arg1 : "10";
                scheduleCommand("stream open " + size);
                break;
            // Party warp commands
            case "warp":
                if (!ToggleUtil.getHandler().chat.warp) {
                    return;
                }

                scheduleCommand("p warp");
                break;
            // Join instance commands
            case "instance":
            case "join":
                if (!ToggleUtil.getHandler().chat.instance) {
                    return;
                }
                // TODO
                break;
            // Party invite commands
            case "invite":
            case "inv":
                if (!ToggleUtil.getHandler().chat.invite || arg1.isEmpty()) {
                    return;
                }

                scheduleCommand("p " + arg1);
                break;
            // Party kick commands
            case "kick":
                if (!ToggleUtil.getHandler().chat.kick) {
                    return;
                }

                String kick = arg1.isEmpty() ? username : arg1;
                scheduleCommand("p kick " + kick);
                break;
            // Party transfer commands
            case "transfer":
            case "ptme":
            case "pm":
                if (!ToggleUtil.getHandler().chat.transfer) {
                    return;
                }

                String transfer = arg1.isEmpty() ? username : arg1;
                scheduleCommand("p transfer " + transfer);
                break;
            // Party promote commands
            case "promote":
                if (!ToggleUtil.getHandler().chat.promote) {
                    return;
                }

                String promote = arg1.isEmpty() ? username : arg1;
                scheduleCommand("p promote " + promote);
                break;
            // Party demote commands
            case "demote":
                if (!ToggleUtil.getHandler().chat.demote) {
                    return;
                }

                String demote = arg1.isEmpty() ? username : arg1;
                scheduleCommand("p demote " + demote);
                break;
            // Leader help commands
            case "help":
            case "leaderhelp":
            case "lhelp":
                if (!ToggleUtil.getHandler().chat.leaderHelp) {
                    return;
                }

                // Build the help message with available leader commands
                StringBuilder helpMessage = new StringBuilder("Leader Commands: ");
                appendCommand(helpMessage, "allinv", ToggleUtil.getHandler().chat.allInvite);
                appendCommand(helpMessage, "mute", ToggleUtil.getHandler().chat.mute);
                appendCommand(helpMessage, "stream [max]", ToggleUtil.getHandler().chat.stream);
                appendCommand(helpMessage, "warp", ToggleUtil.getHandler().chat.warp);
                appendCommand(helpMessage, "instance [name]",
                        ToggleUtil.getHandler().chat.instance);
                appendCommand(helpMessage, "invite <player>", ToggleUtil.getHandler().chat.invite);
                appendCommand(helpMessage, "kick [player]", ToggleUtil.getHandler().chat.kick);
                appendCommand(helpMessage, "transfer [player]",
                        ToggleUtil.getHandler().chat.transfer);
                appendCommand(helpMessage, "promote [player]",
                        ToggleUtil.getHandler().chat.promote);
                appendCommand(helpMessage, "demote [player]", ToggleUtil.getHandler().chat.demote);
                appendCommand(helpMessage, "lhelp", ToggleUtil.getHandler().chat.leaderHelp);
                helpMessage.setLength(helpMessage.length() - 2);
                scheduleCommand("pc " + helpMessage);
                break;
        }
    }

    /**
     * Handles the party command logic when a message is received.
     * 
     * @param player The player who sent the command.
     * @param username The username of the player who sent the command.
     * @param head The command head for the party commands.
     * @param args The arguments of the command.
     */
    private void handlePartyCommand(ClientPlayerEntity player, String username, String head,
            String[] args) {
        if (!ConfigUtil.getHandler().chat.partyCommands) {
            return;
        }

        String command = args.length > 0 ? args[0].toLowerCase() : "";
        String arg1 = args.length > 1 ? args[1] : "";

        switch (command) {
            // 8ball commands
            case "8ball":
                if (!ToggleUtil.getHandler().chat.eightBall) {
                    return;
                }

                String response = EIGHT_BALL[(int) (Math.random() * EIGHT_BALL.length)];
                scheduleCommand(head + " @" + username + ", " + response);
                break;
            // Coin flip commands
            case "coin":
            case "flip":
            case "coinflip":
            case "cf":
                if (!ToggleUtil.getHandler().chat.coinFlip) {
                    return;
                }

                boolean heads = Math.random() < 0.5;
                String result = heads ? "Heads!" : "Tails!";
                scheduleCommand(head + " " + username + " flipped a " + result);
                break;
            // Dice roll commands
            case "dice":
            case "roll":
                if (!ToggleUtil.getHandler().chat.diceRoll) {
                    return;
                }

                int sides = ParseUtil.isNumeric(arg1) ? Integer.parseInt(arg1) : 6;
                int roll = (int) (Math.random() * sides) + 1;
                scheduleCommand(head + " " + username + " rolled a " + roll + "!");
                break;
            // Waifu commands
            case "waifu":
            case "women":
            case "w":
                if (!ToggleUtil.getHandler().chat.waifu) {
                    return;
                }
                // TODO
                break;
            // Party help commands
            case "help":
            case "partyhelp":
            case "phelp":
                if (!ToggleUtil.getHandler().chat.partyHelp) {
                    return;
                }

                // Build the help message with available party commands
                StringBuilder helpMessage = new StringBuilder("Party Commands: ");
                appendCommand(helpMessage, "8ball", ToggleUtil.getHandler().chat.eightBall);
                appendCommand(helpMessage, "cf", ToggleUtil.getHandler().chat.coinFlip);
                appendCommand(helpMessage, "dice [sides]", ToggleUtil.getHandler().chat.diceRoll);
                appendCommand(helpMessage, "waifu", ToggleUtil.getHandler().chat.waifu);
                appendCommand(helpMessage, "phelp", ToggleUtil.getHandler().chat.partyHelp);
                scheduleCommand(head + " " + helpMessage);
                break;
        }
    }

    /**
     * Handles the status command logic when a message is received.
     * 
     * @param player The player who sent the command.
     * @param username The username of the player who sent the command.
     * @param head The command head for the status commands.
     * @param args The arguments of the command.
     */
    private void handleStatusCommand(ClientPlayerEntity player, String username, String head,
            String[] args) {
        if (!ConfigUtil.getHandler().chat.statusCommands) {
            return;
        }

        String command = args[0];

        switch (command) {
            // Coordinates commands
            case "coords":
            case "waypoint":
            case "xyz":
                if (!ToggleUtil.getHandler().chat.coords) {
                    return;
                }

                long x = Math.round(player.getX());
                long y = Math.round(player.getY());
                long z = Math.round(player.getZ());
                String coords = String.format("x: %d, y: %d, z: %d", x, y, z);
                scheduleCommand(head + " " + coords);
                break;
            // FPS commands
            case "fps":
                if (!ToggleUtil.getHandler().chat.fps) {
                    return;
                }

                int fps = MinecraftClient.getInstance().getCurrentFps();
                scheduleCommand(head + " " + fps + " FPS");
                break;
            // TPS commands
            case "tps":
                if (!ToggleUtil.getHandler().chat.tps) {
                    return;
                }

                double tps = ServerStatus.getInstance().getTps();
                scheduleCommand(head + " " + String.format("%.2f TPS", tps));
                break;
            // Leave party commands
            case "leave":
                if (!ToggleUtil.getHandler().chat.leave) {
                    return;
                }

                scheduleCommand("p leave");
                break;
            // Limbo or lobby commands
            case "limbo":
            case "lobby":
            case "l":
                if (!ToggleUtil.getHandler().chat.limbo) {
                    return;
                }

                scheduleCommand("l");
                break;
            // Ping commands
            case "ping":
                if (!ToggleUtil.getHandler().chat.ping) {
                    return;
                }

                int ping = ServerStatus.getInstance().getPing();
                scheduleCommand(command + " " + ping + "ms");
                break;
            // Playtime commands
            case "playtime":
            case "pt":
                if (!ToggleUtil.getHandler().chat.playtime) {
                    return;
                }

                String playtime = PlaytimeWarning.getInstance().formatPlaytime();
                scheduleCommand(head + " " + playtime + " playtime");
                break;
            // Stats commands
            case "stats":
            case "stat":
                if (!ToggleUtil.getHandler().chat.stats) {
                    return;
                }
                // TODO
                break;
            // Time commands
            case "time":
                if (!ToggleUtil.getHandler().chat.time) {
                    return;
                }

                ZonedDateTime now = ZonedDateTime.now();
                scheduleCommand(head + " " + now);
                break;
            // Status help commands
            case "help":
            case "statushelp":
            case "shelp":
                if (!ToggleUtil.getHandler().chat.statusHelp) {
                    return;
                }

                // Build the help message with available status commands
                StringBuilder helpMessage = new StringBuilder("Status Commands: ");
                appendCommand(helpMessage, "coords", ToggleUtil.getHandler().chat.coords);
                appendCommand(helpMessage, "fps", ToggleUtil.getHandler().chat.fps);
                appendCommand(helpMessage, "tps", ToggleUtil.getHandler().chat.tps);
                appendCommand(helpMessage, "leave", ToggleUtil.getHandler().chat.leave);
                appendCommand(helpMessage, "limbo", ToggleUtil.getHandler().chat.limbo);
                appendCommand(helpMessage, "ping", ToggleUtil.getHandler().chat.ping);
                appendCommand(helpMessage, "playtime", ToggleUtil.getHandler().chat.playtime);
                appendCommand(helpMessage, "stats", ToggleUtil.getHandler().chat.stats);
                appendCommand(helpMessage, "time", ToggleUtil.getHandler().chat.time);
                appendCommand(helpMessage, "shelp", ToggleUtil.getHandler().chat.statusHelp);
                helpMessage.setLength(helpMessage.length() - 2);
                scheduleCommand(head + " " + helpMessage);
                break;
        }
    }
}
