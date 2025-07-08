package net.volcaronitee.nar.feature.chat;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.config.NarData;
import net.volcaronitee.nar.config.NarJson;
import net.volcaronitee.nar.config.NarList;
import net.volcaronitee.nar.config.NarToggle;
import net.volcaronitee.nar.feature.general.ServerStatus;
import net.volcaronitee.nar.util.ParseUtil;
import net.volcaronitee.nar.util.PartyUtil;
import net.volcaronitee.nar.util.RequestUtil;
import net.volcaronitee.nar.util.ScheduleUtil;

/**
 * Feature for handling chat commands in the game.
 */
public class ChatCommands {
    private static final ChatCommands INSTANCE = new ChatCommands();

    public static final NarList BLACK_LIST = new NarList("Black List",
            Text.literal("A list of players to block from sending chat commands."),
            "black_list.json");
    public static final NarList PREFIX_LIST = new NarList("Prefix List",
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

    private static final Map<String, List<String>> WAIFUS = new HashMap<>();

    private int delay = 0;

    /**
     * Enum representing the type of chat command.
     */
    private enum CommandType {
        ALL, GUILD, PARTY, PRIVATE, COMMAND;

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
                case COMMAND:
                    return "nar echo";
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
     * Returns the singleton instance of ChatCommands.
     * 
     * @return The singleton instance of ChatCommands.
     */
    public static ChatCommands getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the chat command feature to listen for game messages.
     */
    public static void register() {
        INSTANCE.addWaifu("waifu");
        ClientReceiveMessageEvents.GAME.register(INSTANCE::handleChatCommand);
    }

    /**
     * Adds a waifu image URL to the WAIFUS list based on the specified category.
     */
    private void addWaifu(String category) {
        // Determine the type of waifu based on the nsfw setting
        String type = NarData.getData().get("nsfw").getAsBoolean() ? "nsfw" : "sfw";

        // Fetch a waifu image URL from the API and add it to the list
        RequestUtil.get("https://api.waifu.pics/" + type + "/" + category).thenAccept(response -> {
            if (response != null) {
                try {
                    JsonObject json = NarJson.GSON.fromJson(response, JsonObject.class);
                    if (json != null && json.has("url")) {
                        // Create a new list for the category if it doesn't exist
                        if (!WAIFUS.containsKey(category)) {
                            WAIFUS.put(category, new ArrayList<>());
                        }

                        // Add the waifu image URL to the list for the category
                        String url = json.get("url").getAsString();
                        if (type.equals("nsfw")) {
                            url = "_" + url;
                        }
                        WAIFUS.get(category).add(url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });

        // Limit the number of waifus in each category to 10
        for (String cat : WAIFUS.keySet()) {
            if (WAIFUS.get(cat).size() > 10) {
                WAIFUS.get(cat).remove(0);
            }
        }
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

        if (NarToggle.getHandler().chat.allChat && allMatcher.matches()) {
            username = allMatcher.group(1);
            chatMessage = allMatcher.group(2);
            commandType = CommandType.ALL;
        } else if (NarToggle.getHandler().chat.guildChat && guildMatcher.matches()) {
            username = guildMatcher.group(1);
            chatMessage = guildMatcher.group(2);
            commandType = CommandType.GUILD;
        } else if (NarToggle.getHandler().chat.partyChat && partyMatcher.matches()) {
            username = partyMatcher.group(1);
            chatMessage = partyMatcher.group(2);
            commandType = CommandType.PARTY;
        } else if (NarToggle.getHandler().chat.privateChat && privateMatcher.matches()) {
            username = privateMatcher.group(1);
            chatMessage = privateMatcher.group(2);
            commandType = CommandType.PRIVATE;
        } else {
            return;
        }

        // Check if username is blacklisted
        if (NarToggle.getHandler().chat.blacklistLock && BLACK_LIST.list.contains(username)) {
            return;
        }

        // Check if the text starts with any of the defined prefixes
        boolean isCommand = false;
        for (String prefix : PREFIX_LIST.list) {
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
        // Send the command to the player network handler
        INSTANCE.delay += 6;
        ScheduleUtil.schedule(() -> {
            MinecraftClient.getInstance().player.networkHandler.sendChatCommand(command);
            INSTANCE.delay -= 6;
        }, INSTANCE.delay);
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
     * Handles a command from the player.
     * 
     * @param player The player who sent the command.
     * @param command The command sent by the player.
     * @return True if the command was handled, false otherwise.
     */
    public boolean handleCommand(ClientPlayerEntity player, String command) {
        String username = player.getGameProfile().getName();
        String head = CommandType.getCommandHead(CommandType.COMMAND, username);
        String[] args = command.split(" ");

        return handleLeaderCommand(player, username, args)
                || handlePartyCommand(player, username, head, args)
                || handleStatusCommand(player, username, head, args);
    }

    /**
     * Handles the leader command logic when a message is received.
     * 
     * @param player The player who sent the command.
     * @param username The username of the player who sent the command.
     * @param args The arguments of the command.
     */
    private boolean handleLeaderCommand(ClientPlayerEntity player, String username, String[] args) {
        // Check if username is whitelisted
        if (NarToggle.getHandler().chat.whitelistLock
                && !JoinWhitelist.WHITE_LIST.list.contains(username)) {
            return false;
        }

        String partyLeader = PartyUtil.getLeader();
        String clientUsername = MinecraftClient.getInstance().getSession().getUsername();
        if (!NarConfig.getHandler().chat.leaderCommands
                || (NarToggle.getHandler().chat.leaderLock && partyLeader == clientUsername)
                || !PartyUtil.isInParty() || !PartyUtil.getLeader().equals(clientUsername)) {
            return false;
        }

        String command = args.length > 0 ? args[0].toLowerCase() : "";
        String arg1 = args.length > 1 ? args[1] : "";

        switch (command) {
            // All invite commands
            case "allinvite":
            case "allinv":
                if (!NarToggle.getHandler().chat.allInvite) {
                    return false;
                }

                scheduleCommand("p settings allinvite");
                return true;
            // Party mute commands
            case "mute":
                if (!NarToggle.getHandler().chat.mute) {
                    return false;
                }

                scheduleCommand("p mute");
                return true;
            // Stream open commands
            case "streamopen":
            case "stream":
                if (!NarToggle.getHandler().chat.stream) {
                    return false;
                }

                String size = ParseUtil.isInteger(arg1) ? arg1 : "10";
                scheduleCommand("stream open " + size);
                return true;
            // Party warp commands
            case "warp":
                if (!NarToggle.getHandler().chat.warp) {
                    return false;
                }

                scheduleCommand("p warp");
                return true;
            // Join instance commands
            case "instance":
            case "join":
                if (!NarToggle.getHandler().chat.instance) {
                    return false;
                }
                // TODO
                return true;
            // Party invite commands
            case "invite":
            case "inv":
                if (!NarToggle.getHandler().chat.invite || arg1.isEmpty()) {
                    return false;
                }

                scheduleCommand("p " + arg1);
                return true;
            // Party kick commands
            case "kick":
                if (!NarToggle.getHandler().chat.kick) {
                    return false;
                }

                String kick = arg1.isEmpty() ? username : arg1;
                scheduleCommand("p kick " + kick);
                return true;
            // Party transfer commands
            case "transfer":
            case "ptme":
            case "pm":
                if (!NarToggle.getHandler().chat.transfer) {
                    return false;
                }

                String transfer = arg1.isEmpty() ? username : arg1;
                scheduleCommand("p transfer " + transfer);
                return true;
            // Party promote commands
            case "promote":
                if (!NarToggle.getHandler().chat.promote) {
                    return false;
                }

                String promote = arg1.isEmpty() ? username : arg1;
                scheduleCommand("p promote " + promote);
                return true;
            // Party demote commands
            case "demote":
                if (!NarToggle.getHandler().chat.demote) {
                    return false;
                }

                String demote = arg1.isEmpty() ? username : arg1;
                scheduleCommand("p demote " + demote);
                return true;
            // Leader help commands
            case "help":
            case "leaderhelp":
            case "lhelp":
                if (!NarToggle.getHandler().chat.leaderHelp) {
                    return false;
                }

                // Build the help message with available leader commands
                StringBuilder helpMessage = new StringBuilder("Leader Commands: ");
                appendCommand(helpMessage, "allinv", NarToggle.getHandler().chat.allInvite);
                appendCommand(helpMessage, "mute", NarToggle.getHandler().chat.mute);
                appendCommand(helpMessage, "stream [max]", NarToggle.getHandler().chat.stream);
                appendCommand(helpMessage, "warp", NarToggle.getHandler().chat.warp);
                appendCommand(helpMessage, "instance [name]", NarToggle.getHandler().chat.instance);
                appendCommand(helpMessage, "invite <player>", NarToggle.getHandler().chat.invite);
                appendCommand(helpMessage, "kick [player]", NarToggle.getHandler().chat.kick);
                appendCommand(helpMessage, "transfer [player]",
                        NarToggle.getHandler().chat.transfer);
                appendCommand(helpMessage, "promote [player]", NarToggle.getHandler().chat.promote);
                appendCommand(helpMessage, "demote [player]", NarToggle.getHandler().chat.demote);
                appendCommand(helpMessage, "lhelp", NarToggle.getHandler().chat.leaderHelp);
                helpMessage.setLength(helpMessage.length() - 2);
                scheduleCommand("pc " + helpMessage);
                return true;
            // Default case for unrecognized commands
            default:
                return false;
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
    private boolean handlePartyCommand(ClientPlayerEntity player, String username, String head,
            String[] args) {
        if (!NarConfig.getHandler().chat.partyCommands) {
            return false;
        }

        String command = args.length > 0 ? args[0].toLowerCase() : "";
        String arg1 = args.length > 1 ? args[1] : "";

        switch (command) {
            // 8ball commands
            case "8ball":
                if (!NarToggle.getHandler().chat.eightBall) {
                    return false;
                }

                String response = EIGHT_BALL[(int) (Math.random() * EIGHT_BALL.length)];
                scheduleCommand(head + " @" + username + ", " + response);
                return true;
            // Coin flip commands
            case "coin":
            case "flip":
            case "coinflip":
            case "cf":
                if (!NarToggle.getHandler().chat.coinFlip) {
                    return false;
                }

                boolean heads = Math.random() < 0.5;
                String result = heads ? "Heads!" : "Tails!";
                scheduleCommand(head + " " + username + " flipped a " + result);
                return true;
            // Dice roll commands
            case "dice":
            case "roll":
                if (!NarToggle.getHandler().chat.diceRoll) {
                    return false;
                }

                int sides = ParseUtil.isInteger(arg1) ? Integer.parseInt(arg1) : 6;
                int roll = (int) (Math.random() * sides) + 1;
                scheduleCommand(head + " " + username + " rolled a " + roll + "!");
                return true;
            // Waifu commands
            case "waifu":
            case "women":
            case "w":
                if (!NarToggle.getHandler().chat.waifu) {
                    return false;
                }

                // Add a waifu image URL to the WAIFUS list based on the category
                String category = arg1.isEmpty() ? "waifu" : arg1.toLowerCase();
                addWaifu(category);

                // Get the last waifu image URL from the list for the specified category
                List<String> waifuList = WAIFUS.getOrDefault(category, WAIFUS.get("waifu"));
                String waifuUrl = waifuList.get(waifuList.size() - 1);
                scheduleCommand(head + " " + waifuUrl);

                return true;
            // Party help commands
            case "help":
            case "partyhelp":
            case "phelp":
                if (!NarToggle.getHandler().chat.partyHelp) {
                    return false;
                }

                // Build the help message with available party commands
                StringBuilder helpMessage = new StringBuilder("Party Commands: ");
                appendCommand(helpMessage, "8ball", NarToggle.getHandler().chat.eightBall);
                appendCommand(helpMessage, "cf", NarToggle.getHandler().chat.coinFlip);
                appendCommand(helpMessage, "dice [sides]", NarToggle.getHandler().chat.diceRoll);
                appendCommand(helpMessage, "waifu", NarToggle.getHandler().chat.waifu);
                appendCommand(helpMessage, "phelp", NarToggle.getHandler().chat.partyHelp);
                scheduleCommand(head + " " + helpMessage);
                return true;

            // Hidden commands
            case "avengers":
                scheduleCommand("p Somxone Kwuromi valyn Serten");
                return true;

            // Default case for unrecognized commands
            default:
                return false;
        }
    }

    /**
     * Handles the status command logic when a message is received.
     * 
     * @param player The player who sent the command.
     * @param username The username of the player who sent the command.
     * @param head The command head for the status commands.
     * @param args The arguments of the command.
     * 
     * @return True if the command was handled, false otherwise.
     */
    private boolean handleStatusCommand(ClientPlayerEntity player, String username, String head,
            String[] args) {
        if (!NarConfig.getHandler().chat.statusCommands) {
            return false;
        }

        String command = args[0];

        switch (command) {
            // Coordinates commands
            case "coords":
            case "waypoint":
            case "xyz":
                if (!NarToggle.getHandler().chat.coords) {
                    return false;
                }

                long x = Math.round(player.getX());
                long y = Math.round(player.getY());
                long z = Math.round(player.getZ());
                String coords = String.format("x: %d, y: %d, z: %d", x, y, z);
                scheduleCommand(head + " " + coords);
                return true;
            // FPS commands
            case "fps":
                if (!NarToggle.getHandler().chat.fps) {
                    return false;
                }

                int fps = MinecraftClient.getInstance().getCurrentFps();
                scheduleCommand(head + " " + fps + " FPS");
                return true;
            // TPS commands
            case "tps":
                if (!NarToggle.getHandler().chat.tps) {
                    return false;
                }

                double tps = ServerStatus.getInstance().getTps();
                scheduleCommand(head + " " + String.format("%.2f TPS", tps));
                return true;
            // Leave party commands
            case "leave":
                if (!NarToggle.getHandler().chat.leave) {
                    return false;
                }

                scheduleCommand("p leave");
                return true;
            // Limbo or lobby commands
            case "limbo":
            case "lobby":
            case "l":
                if (!NarToggle.getHandler().chat.limbo) {
                    return false;
                }

                scheduleCommand("l");
                return true;
            // Ping commands
            case "ping":
                if (!NarToggle.getHandler().chat.ping) {
                    return false;
                }

                int ping = ServerStatus.getInstance().getPing();
                scheduleCommand(command + " " + ping + "ms");
                return true;
            // Playtime commands
            case "playtime":
            case "pt":
                if (!NarToggle.getHandler().chat.playtime) {
                    return false;
                }

                String playtime = PlaytimeWarning.getInstance().formatPlaytime();
                scheduleCommand(head + " " + playtime + " playtime");
                return true;
            // Stats commands
            case "stats":
            case "stat":
                if (!NarToggle.getHandler().chat.stats) {
                    return false;
                }
                // TODO
                return true;
            // Time commands
            case "time":
                if (!NarToggle.getHandler().chat.time) {
                    return false;
                }

                ZonedDateTime now = ZonedDateTime.now();
                DateTimeFormatter formatter =
                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
                scheduleCommand(head + " " + now.format(formatter));
                return true;
            // Status help commands
            case "help":
            case "statushelp":
            case "shelp":
                if (!NarToggle.getHandler().chat.statusHelp) {
                    return false;
                }

                // Build the help message with available status commands
                StringBuilder helpMessage = new StringBuilder("Status Commands: ");
                appendCommand(helpMessage, "coords", NarToggle.getHandler().chat.coords);
                appendCommand(helpMessage, "fps", NarToggle.getHandler().chat.fps);
                appendCommand(helpMessage, "tps", NarToggle.getHandler().chat.tps);
                appendCommand(helpMessage, "leave", NarToggle.getHandler().chat.leave);
                appendCommand(helpMessage, "limbo", NarToggle.getHandler().chat.limbo);
                appendCommand(helpMessage, "ping", NarToggle.getHandler().chat.ping);
                appendCommand(helpMessage, "playtime", NarToggle.getHandler().chat.playtime);
                appendCommand(helpMessage, "stats", NarToggle.getHandler().chat.stats);
                appendCommand(helpMessage, "time", NarToggle.getHandler().chat.time);
                appendCommand(helpMessage, "shelp", NarToggle.getHandler().chat.statusHelp);
                helpMessage.setLength(helpMessage.length() - 2);
                scheduleCommand(head + " " + helpMessage);
                return true;
            // Default case for unrecognized commands
            default:
                return false;
        }
    }
}
