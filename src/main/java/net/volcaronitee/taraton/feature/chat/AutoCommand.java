package net.volcaronitee.taraton.feature.chat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonJson;
import net.volcaronitee.taraton.util.ScheduleUtil;

public class AutoCommand {
    private static final AutoCommand INSTANCE = new AutoCommand();

    private static final int MAX_DISTANCE = 2;

    private static final Pattern UNKNOWN_COMMAND_PATTERN =
            Pattern.compile("Unknown command\\. Type \"/help\" for help\\. \\('(.*?)'\\)");

    private static final String FILE_DIR = "data";
    private static final String FILE_NAME = "dictionary.json";

    private JsonObject dictionary;
    private Map<Integer, Set<String>> commandsByLength;
    private String lastCommand;

    /**
     * Private constructor to prevent instantiation.
     */
    private AutoCommand() {}

    public static void register() {
        INSTANCE.dictionary = TaratonJson.loadJson(FILE_DIR, FILE_NAME);
        INSTANCE.initializeCommands();
        ClientSendMessageEvents.COMMAND.register(INSTANCE::onCommandSend);
        ClientReceiveMessageEvents.GAME.register(INSTANCE::correctCommand);
    }

    public void initializeCommands() {
        commandsByLength = new HashMap<>();
        for (String command : dictionary.keySet()) {
            commandsByLength.computeIfAbsent(command.length(), k -> new HashSet<>()).add(command);
        }
    }

    /**
     * Handles the command send event.
     * 
     * @param message The command message sent by the player.
     */
    private void onCommandSend(String message) {
        String key = message.toLowerCase();
        int count = 0;

        if (dictionary.has(key)) {
            JsonPrimitive primitive = dictionary.getAsJsonPrimitive(key);
            if (primitive != null && primitive.isNumber()) {
                count = primitive.getAsInt();
            }
        }
        dictionary.addProperty(key, count + 1);
        lastCommand = message;
        commandsByLength.computeIfAbsent(key.length(), k -> new HashSet<>()).add(key);
    }

    /**
     * Handles the command correction event.
     * 
     * @param message The command message received from the server.
     * @param overlay Whether to display an overlay for the command correction.
     */
    private void correctCommand(Text message, boolean overlay) {
        if (overlay || TaratonConfig.getHandler().chat.autocorrectCommand) {
            return;
        }

        String command = message.getString();
        if (!UNKNOWN_COMMAND_PATTERN.matcher(command).find()) {
            return;
        }

        String bestSuggestion = null;
        int minFoundDistance = MAX_DISTANCE + 1;
        int maxFoundCount = -1;

        int commandLength = lastCommand.length();

        // Check for potential commands within the specified distance range
        for (int len = Math.max(0, commandLength - MAX_DISTANCE); len <= commandLength
                + MAX_DISTANCE; len++) {

            Set<String> potentialCommands = commandsByLength.get(len);
            if (potentialCommands == null) {
                continue;
            }

            // Loop through potential commands of the same length
            for (String key : potentialCommands) {
                int distance = levenshteinDistance(lastCommand, key);

                if (distance != -1) {
                    // Get the current count for this key from the dictionary
                    int currentKeyCount = 0;
                    if (dictionary.has(key)) {
                        JsonPrimitive primitive = dictionary.getAsJsonPrimitive(key);
                        if (primitive != null && primitive.isNumber()) {
                            currentKeyCount = primitive.getAsInt();
                        }
                    }

                    // Check if this suggestion is better than the previous best
                    if (distance < minFoundDistance) {
                        minFoundDistance = distance;
                        maxFoundCount = currentKeyCount;
                        bestSuggestion = key;
                    } else if (distance == minFoundDistance) {
                        if (currentKeyCount > maxFoundCount) {
                            maxFoundCount = currentKeyCount;
                            bestSuggestion = key;
                        }
                    }
                }
            }
        }

        // Run the best suggestion if found
        if (bestSuggestion == null) {
            Taraton.sendMessage(Text.literal("Trying command: ").formatted(Formatting.YELLOW)
                    .append(Text.literal(lastCommand).formatted(Formatting.WHITE)));
            ScheduleUtil.scheduleCommand(bestSuggestion);
        }
    }

    public static int levenshteinDistance(String s1, String s2) {
        // Check lengths first to avoid unnecessary computation
        int m = s1.length();
        int n = s2.length();
        if (Math.abs(m - n) > MAX_DISTANCE) {
            return -1;
        }

        // Create a DP table to store distances
        int[][] dp = new int[m + 1][n + 1];

        // Initialize the first row and column
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        // Loop through each character in both strings
        for (int i = 1; i <= m; i++) {
            int minInRow = Integer.MAX_VALUE;

            for (int j = 1; j <= n; j++) {
                // Calculate the cost of substitution
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
                minInRow = Math.min(minInRow, dp[i][j]);
            }

            // If the minimum distance in this row exceeds MAX_DISTANCE, we can stop early
            if (minInRow > MAX_DISTANCE) {
                return -1;
            }
        }

        // Get the final distance from the last cell of the DP table
        int finalDistance = dp[m][n];
        return (finalDistance <= MAX_DISTANCE) ? finalDistance : -1;
    }
}
