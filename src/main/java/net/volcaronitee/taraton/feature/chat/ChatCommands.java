package net.volcaronitee.taraton.feature.chat;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonData;
import net.volcaronitee.taraton.config.TaratonJson;
import net.volcaronitee.taraton.config.TaratonList;
import net.volcaronitee.taraton.config.TaratonToggle;
import net.volcaronitee.taraton.feature.general.ServerStatus;
import net.volcaronitee.taraton.feature.general.WidgetDisplay;
import net.volcaronitee.taraton.feature.general.WidgetDisplay.Widget;
import net.volcaronitee.taraton.util.OverlayUtil.LineContent;
import net.volcaronitee.taraton.util.ParseUtil;
import net.volcaronitee.taraton.util.PartyUtil;
import net.volcaronitee.taraton.util.RequestUtil;
import net.volcaronitee.taraton.util.ScheduleUtil;

/**
 * Feature for handling chat commands in the game.
 */
public class ChatCommands {
    private static final ChatCommands INSTANCE = new ChatCommands();

    public static final TaratonList PREFIX_LIST = new TaratonList("Prefix List",
            Text.literal("A list of prefixes to detect for chat commands."), "prefix_list.json",
            new String[] {"Prefix"});
    public static final TaratonList AVENGER_LIST = new TaratonList("The Avengers",
            Text.literal("DUN DUN DUNDUN"), "avenger_list.json", new String[] {"Username"});

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

    // Waifu image URLs categorized by type
    private static final Map<String, List<String>> WAIFUS = new HashMap<>();
    private static final Set<String> WAIFU_CATEGORIES = Set.of("waifu", "neko", "shinobu",
            "megumin", "bully", "cuddle", "cry", "hug", "awoo", "kiss", "lick", "pat", "smug",
            "bonk", "yeet", "blush", "smile", "wave", "highfive", "handhold", "nom", "bite",
            "glomp", "slap", "kill", "kick", "happy", "wink", "trap", "blowjob");
    private static final Set<String> WAIFU_NSFW = Set.of("nsfw", "x");

    // Instances
    private static final String[] FLOORS = {"one", "two", "three", "four", "five", "six", "seven"};
    private static final String[] TIERS = {"basic", "hot", "burning", "fiery", "infernal"};

    // Stats widget
    private static final Widget STATS_WIDGET = new Widget("Stats", () -> false);

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
                    return "taraton echo";
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
     * 
     * @param cat The category of the waifu image (e.g., "waifu", "neko", etc.).
     */
    private void addWaifu(String cat) {
        // Determine category and type
        String category = WAIFU_CATEGORIES.contains(cat) ? cat : "waifu";
        String type = TaratonData.getData().get("nsfw").getAsBoolean() && WAIFU_NSFW.contains(cat)
                ? "nsfw"
                : "sfw";

        // Fetch a waifu image URL from the API and add it to the list
        RequestUtil.get("https://api.waifu.pics/" + type + "/" + category).thenAccept(response -> {
            if (response != null) {
                try {
                    JsonObject json = TaratonJson.GSON.fromJson(response, JsonObject.class);
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
        for (String key : WAIFUS.keySet()) {
            if (WAIFUS.get(key).size() > 10) {
                WAIFUS.get(key).remove(0);
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

        if (TaratonToggle.getHandler().chat.allChat && allMatcher.matches()) {
            username = allMatcher.group(1);
            chatMessage = allMatcher.group(2);
            commandType = CommandType.ALL;
        } else if (TaratonToggle.getHandler().chat.guildChat && guildMatcher.matches()) {
            username = guildMatcher.group(1);
            chatMessage = guildMatcher.group(2);
            commandType = CommandType.GUILD;
        } else if (TaratonToggle.getHandler().chat.partyChat && partyMatcher.matches()) {
            username = partyMatcher.group(1);
            chatMessage = partyMatcher.group(2);
            commandType = CommandType.PARTY;
        } else if (TaratonToggle.getHandler().chat.privateChat && privateMatcher.matches()) {
            username = privateMatcher.group(1);
            chatMessage = privateMatcher.group(2);
            commandType = CommandType.PRIVATE;
        } else {
            return;
        }

        // Check if username is blacklisted
        if (TaratonToggle.getHandler().chat.blacklistLock
                && AutoKick.BLACK_LIST.list.contains(username)) {
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
        if (TaratonToggle.getHandler().chat.whitelistLock
                && !JoinParty.WHITE_LIST.list.contains(username)) {
            return false;
        }

        String partyLeader = PartyUtil.getLeader();
        String clientUsername = MinecraftClient.getInstance().getSession().getUsername();
        if (!TaratonConfig.getHandler().chat.leaderCommands
                || (TaratonToggle.getHandler().chat.leaderLock && partyLeader == clientUsername)
                || !PartyUtil.isInParty() || !PartyUtil.getLeader().equals(clientUsername)) {
            return false;
        }

        String command = args.length > 0 ? args[0].toLowerCase() : "";
        String arg1 = args.length > 1 ? args[1] : "";

        switch (command) {
            // All invite commands
            case "allinvite":
            case "allinv":
                if (!TaratonToggle.getHandler().chat.allInvite) {
                    return false;
                }

                ScheduleUtil.scheduleCommand("p settings allinvite");
                return true;
            // Party mute commands
            case "mute":
                if (!TaratonToggle.getHandler().chat.mute) {
                    return false;
                }

                ScheduleUtil.scheduleCommand("p mute");
                return true;
            // Stream open commands
            case "streamopen":
            case "stream":
                if (!TaratonToggle.getHandler().chat.stream) {
                    return false;
                }

                String size = ParseUtil.isInteger(arg1) ? arg1 : "10";
                ScheduleUtil.scheduleCommand("stream open " + size);
                return true;
            // Party warp commands
            case "warp":
                if (!TaratonToggle.getHandler().chat.warp) {
                    return false;
                }

                ScheduleUtil.scheduleCommand("p warp");
                return true;
            // Join instance commands
            case "instance":
            case "join":
                if (!TaratonToggle.getHandler().chat.instance) {
                    return false;
                }

                // Check if the argument is valid
                char l1 = arg1.isEmpty() ? ' ' : arg1.charAt(0);
                String l2 = arg1.length() > 1 ? String.valueOf(arg1.charAt(1)) : "";
                int num = ParseUtil.isInteger(l2) ? Integer.parseInt(l2) : -1;
                if (num == -1) {
                    return false;
                }

                // Handle different instance commands
                if (l1 == 'm' && num <= FLOORS.length) {
                    String floor = FLOORS[num - 1];
                    ScheduleUtil.scheduleCommand("joininstance master_catacombs_floor_" + floor);
                } else if (l1 == 'f' && num <= FLOORS.length) {
                    String floor = FLOORS[num - 1];
                    ScheduleUtil.scheduleCommand("joininstance catacombs_floor_" + floor);
                } else if (l1 == 't' && num <= TIERS.length) {
                    String tier = TIERS[num - 1];
                    ScheduleUtil.scheduleCommand("joininstance kuudra_" + tier);
                } else {
                    return false;
                }

                return true;
            // Party invite commands
            case "invite":
            case "inv":
                if (!TaratonToggle.getHandler().chat.invite || arg1.isEmpty()) {
                    return false;
                }

                ScheduleUtil.scheduleCommand("p " + arg1);
                return true;
            // Party kick commands
            case "kick":
                if (!TaratonToggle.getHandler().chat.kick) {
                    return false;
                }

                String kick = arg1.isEmpty() ? username : arg1;
                ScheduleUtil.scheduleCommand("p kick " + kick);
                return true;
            // Party transfer commands
            case "transfer":
            case "ptme":
            case "pm":
                if (!TaratonToggle.getHandler().chat.transfer) {
                    return false;
                }

                String transfer = arg1.isEmpty() ? username : arg1;
                ScheduleUtil.scheduleCommand("p transfer " + transfer);
                return true;
            // Party promote commands
            case "promote":
                if (!TaratonToggle.getHandler().chat.promote) {
                    return false;
                }

                String promote = arg1.isEmpty() ? username : arg1;
                ScheduleUtil.scheduleCommand("p promote " + promote);
                return true;
            // Party demote commands
            case "demote":
                if (!TaratonToggle.getHandler().chat.demote) {
                    return false;
                }

                String demote = arg1.isEmpty() ? username : arg1;
                ScheduleUtil.scheduleCommand("p demote " + demote);
                return true;
            // Leader help commands
            case "help":
            case "leaderhelp":
            case "lhelp":
                if (!TaratonToggle.getHandler().chat.leaderHelp) {
                    return false;
                }

                // Build the help message with available leader commands
                StringBuilder helpMessage = new StringBuilder("Leader Commands: ");
                appendCommand(helpMessage, "allinv", TaratonToggle.getHandler().chat.allInvite);
                appendCommand(helpMessage, "mute", TaratonToggle.getHandler().chat.mute);
                appendCommand(helpMessage, "stream [max]", TaratonToggle.getHandler().chat.stream);
                appendCommand(helpMessage, "warp", TaratonToggle.getHandler().chat.warp);
                appendCommand(helpMessage, "instance [name]",
                        TaratonToggle.getHandler().chat.instance);
                appendCommand(helpMessage, "invite <player>",
                        TaratonToggle.getHandler().chat.invite);
                appendCommand(helpMessage, "kick [player]", TaratonToggle.getHandler().chat.kick);
                appendCommand(helpMessage, "transfer [player]",
                        TaratonToggle.getHandler().chat.transfer);
                appendCommand(helpMessage, "promote [player]",
                        TaratonToggle.getHandler().chat.promote);
                appendCommand(helpMessage, "demote [player]",
                        TaratonToggle.getHandler().chat.demote);
                appendCommand(helpMessage, "lhelp", TaratonToggle.getHandler().chat.leaderHelp);
                helpMessage.setLength(helpMessage.length() - 2);
                ScheduleUtil.scheduleCommand("pc " + helpMessage);
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
        if (!TaratonConfig.getHandler().chat.partyCommands) {
            return false;
        }

        String command = args.length > 0 ? args[0].toLowerCase() : "";
        String arg1 = args.length > 1 ? args[1] : "";

        switch (command) {
            // 8ball commands
            case "8ball":
                if (!TaratonToggle.getHandler().chat.eightBall) {
                    return false;
                }

                String response = EIGHT_BALL[(int) (Math.random() * EIGHT_BALL.length)];
                ScheduleUtil.scheduleCommand(head + " @" + username + ", " + response);
                return true;
            // Coin flip commands
            case "coin":
            case "flip":
            case "coinflip":
            case "cf":
                if (!TaratonToggle.getHandler().chat.coinFlip) {
                    return false;
                }

                boolean heads = Math.random() < 0.5;
                String result = heads ? "Heads!" : "Tails!";
                ScheduleUtil.scheduleCommand(head + " " + username + " flipped a " + result);
                return true;
            // Dice roll commands
            case "dice":
            case "roll":
                if (!TaratonToggle.getHandler().chat.diceRoll) {
                    return false;
                }

                int sides = ParseUtil.isInteger(arg1) ? Integer.parseInt(arg1) : 6;
                int roll = (int) (Math.random() * sides) + 1;
                ScheduleUtil.scheduleCommand(head + " " + username + " rolled a " + roll + "!");
                return true;
            // Waifu commands
            case "waifu":
            case "women":
            case "w":
                if (!TaratonToggle.getHandler().chat.waifu) {
                    return false;
                }

                // Add a waifu image URL to the WAIFUS list based on the category
                String category = arg1.isEmpty() ? "waifu" : arg1.toLowerCase();
                addWaifu(category);

                // Get the last waifu image URL from the list for the specified category
                List<String> waifuList = WAIFUS.getOrDefault(category, WAIFUS.get("waifu"));
                String waifuUrl = waifuList.get(waifuList.size() - 1);
                ScheduleUtil.scheduleCommand(head + " " + waifuUrl);

                return true;
            // Party help commands
            case "help":
            case "partyhelp":
            case "phelp":
                if (!TaratonToggle.getHandler().chat.partyHelp) {
                    return false;
                }

                // Build the help message with available party commands
                StringBuilder helpMessage = new StringBuilder("Party Commands: ");
                appendCommand(helpMessage, "8ball", TaratonToggle.getHandler().chat.eightBall);
                appendCommand(helpMessage, "cf", TaratonToggle.getHandler().chat.coinFlip);
                appendCommand(helpMessage, "dice [sides]",
                        TaratonToggle.getHandler().chat.diceRoll);
                appendCommand(helpMessage, "waifu", TaratonToggle.getHandler().chat.waifu);
                appendCommand(helpMessage, "phelp", TaratonToggle.getHandler().chat.partyHelp);
                ScheduleUtil.scheduleCommand(head + " " + helpMessage);
                return true;

            // Hidden commands
            case "avengers":
            case "avenger":
                // Schedule the avengers in groups of five
                List<String> avengersList = new ArrayList<>(AVENGER_LIST.list);
                int groupSize = 5;
                for (int i = 0; i < avengersList.size(); i += groupSize) {
                    List<String> group =
                            avengersList.subList(i, Math.min(i + groupSize, avengersList.size()));
                    String avengers = String.join(" ", group);
                    ScheduleUtil.scheduleCommand("p " + avengers);
                }
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
        if (!TaratonConfig.getHandler().chat.statusCommands) {
            return false;
        }

        String command = args[0];

        switch (command) {
            // Coordinates commands
            case "coords":
            case "waypoint":
            case "xyz":
                if (!TaratonToggle.getHandler().chat.coords) {
                    return false;
                }

                long x = Math.round(player.getX());
                long y = Math.round(player.getY());
                long z = Math.round(player.getZ());
                String coords = String.format("x: %d, y: %d, z: %d", x, y, z);
                ScheduleUtil.scheduleCommand(head + " " + coords);
                return true;
            // FPS commands
            case "fps":
                if (!TaratonToggle.getHandler().chat.fps) {
                    return false;
                }

                int fps = MinecraftClient.getInstance().getCurrentFps();
                ScheduleUtil.scheduleCommand(head + " " + fps + " FPS");
                return true;
            // TPS commands
            case "tps":
                if (!TaratonToggle.getHandler().chat.tps) {
                    return false;
                }

                double tps = ServerStatus.getInstance().getTps();
                ScheduleUtil.scheduleCommand(head + " " + String.format("%.2f TPS", tps));
                return true;
            // Leave party commands
            case "leave":
                if (!TaratonToggle.getHandler().chat.leave) {
                    return false;
                }

                ScheduleUtil.scheduleCommand("p leave");
                return true;
            // Limbo or lobby commands
            case "limbo":
            case "lobby":
            case "l":
                if (!TaratonToggle.getHandler().chat.limbo) {
                    return false;
                }

                ScheduleUtil.scheduleCommand("l");
                return true;
            // Ping commands
            case "ping":
                if (!TaratonToggle.getHandler().chat.ping) {
                    return false;
                }

                int ping = ServerStatus.getInstance().getPing();
                ScheduleUtil.scheduleCommand(command + " " + ping + "ms");
                return true;
            // Playtime commands
            case "playtime":
            case "pt":
                if (!TaratonToggle.getHandler().chat.playtime) {
                    return false;
                }

                String playtime = PlaytimeWarning.getInstance().formatPlaytime();
                ScheduleUtil.scheduleCommand(head + " " + playtime + " playtime");
                return true;
            // Stats commands
            case "stats":
            case "stat":
                if (!TaratonToggle.getHandler().chat.stats) {
                    return false;
                }

                // Update the stats widget with current player stats
                WidgetDisplay.getInstance().updateWidgets(List.of(STATS_WIDGET));
                List<String> stats = new ArrayList<>();

                // Collect stats from the widget lines
                for (LineContent line : STATS_WIDGET.getLines()) {
                    String text = line.getText();
                    if (text != null && !text.isEmpty()) {
                        stats.add(text);
                    }
                }

                // Format the stats message
                String statsMessage = String.join(", ", stats);
                if (statsMessage.isEmpty()) {
                    statsMessage = "No stats available.";
                }

                ScheduleUtil.scheduleCommand(head + " " + statsMessage);
                return true;
            // Time commands
            case "time":
                if (!TaratonToggle.getHandler().chat.time) {
                    return false;
                }

                ZonedDateTime now = ZonedDateTime.now();
                DateTimeFormatter formatter =
                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
                ScheduleUtil.scheduleCommand(head + " " + now.format(formatter));
                return true;
            // Status help commands
            case "help":
            case "statushelp":
            case "shelp":
                if (!TaratonToggle.getHandler().chat.statusHelp) {
                    return false;
                }

                // Build the help message with available status commands
                StringBuilder helpMessage = new StringBuilder("Status Commands: ");
                appendCommand(helpMessage, "coords", TaratonToggle.getHandler().chat.coords);
                appendCommand(helpMessage, "fps", TaratonToggle.getHandler().chat.fps);
                appendCommand(helpMessage, "tps", TaratonToggle.getHandler().chat.tps);
                appendCommand(helpMessage, "leave", TaratonToggle.getHandler().chat.leave);
                appendCommand(helpMessage, "limbo", TaratonToggle.getHandler().chat.limbo);
                appendCommand(helpMessage, "ping", TaratonToggle.getHandler().chat.ping);
                appendCommand(helpMessage, "playtime", TaratonToggle.getHandler().chat.playtime);
                appendCommand(helpMessage, "stats", TaratonToggle.getHandler().chat.stats);
                appendCommand(helpMessage, "time", TaratonToggle.getHandler().chat.time);
                appendCommand(helpMessage, "shelp", TaratonToggle.getHandler().chat.statusHelp);
                helpMessage.setLength(helpMessage.length() - 2);
                ScheduleUtil.scheduleCommand(head + " " + helpMessage);
                return true;
            // Default case for unrecognized commands
            default:
                return false;
        }
    }
}
