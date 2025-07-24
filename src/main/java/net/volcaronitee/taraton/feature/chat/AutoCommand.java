package net.volcaronitee.taraton.feature.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonJson;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.ScheduleUtil;

/**
 * Feature for automatically suggesting corrections for mistyped commands in the chat.
 */
public class AutoCommand {
    private static final AutoCommand INSTANCE = new AutoCommand();

    private static final int MAX_DISTANCE = 2;
    private static final int CORRECTION_COOLDOWN_MS = 3000;

    private static final Pattern UNKNOWN_COMMAND_PATTERN =
            Pattern.compile("Unknown command\\. Type \"/help\" for help\\. \\('(.*?)'\\)");

    private static final String FILE_DIR = "data";
    private static final String FILE_NAME = "dictionary.json";

    private JsonObject dictionary;
    private Map<Integer, Set<String>> commandsByLength;
    private String lastCommand;
    private long lastCorrectionTime;

    /**
     * Private constructor to prevent instantiation. Use the static register method to set up the
     * AutoCommand feature.
     */
    private AutoCommand() {}

    /**
     * Registers the AutoCommand feature, loading the command dictionary and setting up event
     * listeners.
     */
    public static void register() {
        // Load the command dictionary from the JSON file.
        INSTANCE.dictionary = TaratonJson.registerJson(FILE_DIR, FILE_NAME).getJsonObject();
        INSTANCE.initializeCommands();

        // Register listeners for sent commands and received game messages.
        ClientSendMessageEvents.COMMAND.register(INSTANCE::onCommandSend);
        ClientReceiveMessageEvents.GAME.register(INSTANCE::correctCommand);
    }

    /**
     * Initializes the command dictionary by grouping commands based on their character length.
     */
    public void initializeCommands() {
        // Group all known commands by their character length for faster searching.
        commandsByLength = new HashMap<>();
        for (String command : dictionary.keySet()) {
            commandsByLength.computeIfAbsent(command.length(), k -> new HashSet<>()).add(command);
        }
    }

    /**
     * Handles the event when a command is sent in the chat. It updates the command's usage count
     * 
     * @param message The command message sent by the user, which is expected to start with a slash.
     */
    private void onCommandSend(String message) {
        // When a command is sent, update its usage count in the dictionary.
        String key = message.toLowerCase();
        int count = 0;
        if (dictionary.has(key)) {
            JsonPrimitive primitive = dictionary.getAsJsonPrimitive(key);
            if (primitive != null && primitive.isNumber()) {
                count = primitive.getAsInt();
            }
        }
        dictionary.addProperty(key, count + 1);

        // Store the most recently sent command for potential correction.
        lastCommand = message;

        // Ensure the command is in the length-based map.
        commandsByLength.computeIfAbsent(key.length(), k -> new HashSet<>()).add(key);
    }

    /**
     * Corrects a mistyped command by suggesting the closest valid command based on the
     * 
     * @param message The message received from the game, which may contain an "Unknown command"
     *        error.
     * @param overlay True if the message is an overlay (e.g., a chat message), false if it's a
     *        regular game message.
     */
    private void correctCommand(Text message, boolean overlay) {
        // Exit if the feature is disabled or the message is an overlay.
        if (overlay
                || !FeatureUtil.isEnabled(TaratonConfig.getInstance().chat.autocorrectCommand)) {
            return;
        }

        // Prevent correction loops by enforcing a cooldown after an attempt.
        if (System.currentTimeMillis() - lastCorrectionTime < CORRECTION_COOLDOWN_MS) {
            return;
        }

        // Check if the message is an "Unknown command" error and if we have a command to correct.
        String command = message.getString();
        if (!UNKNOWN_COMMAND_PATTERN.matcher(command).find() || lastCommand == null) {
            return;
        }

        // Initialize variables to track the best command suggestion.
        String bestSuggestion = null;
        int minFoundDistance = MAX_DISTANCE + 1;
        int maxFoundCount = -1;
        int commandLength = lastCommand.length();

        // Iterate through commands with lengths close to the mistyped command's length.
        for (int len = Math.max(1, commandLength - MAX_DISTANCE); len <= commandLength
                + MAX_DISTANCE; len++) {

            Set<String> potentialCommands = commandsByLength.get(len);
            if (potentialCommands == null) {
                continue;
            }

            // For each potential command, calculate its distance from the mistyped one.
            for (String key : potentialCommands) {
                int distance = levenshteinDistance(lastCommand, key);

                // If a close match is found, check if it's a better suggestion than the current
                // best.
                if (distance != -1 && distance < MAX_DISTANCE + 1) {
                    int currentKeyCount = dictionary.has(key) ? dictionary.get(key).getAsInt() : 0;

                    // A suggestion is "better" if its distance is smaller, or if the distance is
                    // the same
                    // but it has been used more frequently.
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

        // If a suitable suggestion was found, notify the user and execute it.
        if (bestSuggestion != null) {
            Taraton.sendMessage(Text.literal("Did you mean: ").formatted(Formatting.GRAY)
                    .append(Text.literal(bestSuggestion).formatted(Formatting.YELLOW)));
            ScheduleUtil.scheduleCommand(bestSuggestion);

            // Start the cooldown timer.
            lastCorrectionTime = System.currentTimeMillis();
        }
    }

    /**
     * Called by the CommandSuggestorMixin to get custom command suggestions.
     * 
     * @param input The current text in the chat input field.
     * @return A list of suggestions based on frequency and matching input.
     */
    private static List<Map.Entry<String, Integer>> getRankedSuggestions(String input) {
        // Assume a feature toggle exists for this functionality.
        if (!FeatureUtil.isEnabled(TaratonConfig.getInstance().chat.autocompleteCommand)) {
            return Collections.emptyList();
        }

        String query = input.toLowerCase();

        // Find all commands in the dictionary that start with the query.
        Map<String, Integer> candidates = new HashMap<>();
        for (String cmd : INSTANCE.dictionary.keySet()) {
            if (cmd.startsWith(query)) {
                candidates.put(cmd, INSTANCE.dictionary.get(cmd).getAsInt());
            }
        }

        // Sort the found commands by their usage count, descending, and take the top 5.
        return candidates.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Combines the original command suggestions with our custom, frequency-based ones.
     * 
     * @param input The full string from the text input.
     * @param originalSuggestions The suggestions provided by Minecraft.
     * @return A new Suggestions object with the combined and sorted list.
     */
    public static Suggestions combineSuggestions(String input, Suggestions originalSuggestions) {
        // Get our custom suggestions based on frequency.
        List<Map.Entry<String, Integer>> rankedSuggestions = getRankedSuggestions(input);
        if (rankedSuggestions.isEmpty()) {
            return originalSuggestions;
        }

        // Convert the ranked strings into official Suggestion objects
        List<Suggestion> customSuggestions = rankedSuggestions.stream()
                .map(entry -> new Suggestion(originalSuggestions.getRange(), entry.getKey()))
                .collect(Collectors.toList());

        // Combine the lists, avoiding duplicates.
        List<Suggestion> combined = new ArrayList<>(customSuggestions);
        Set<String> customSuggestionTexts = new HashSet<>();
        for (Suggestion s : customSuggestions) {
            customSuggestionTexts.add(s.getText());
        }

        for (Suggestion suggestion : originalSuggestions.getList()) {
            if (!customSuggestionTexts.contains(suggestion.getText())) {
                combined.add(suggestion);
            }
        }

        // Return a new Suggestions object that includes our custom entries.
        return Suggestions.create(input, combined);
    }

    /**
     * Calculates the Levenshtein distance between two strings, which is the minimum number of edit
     * operations required to change one string into the other.
     * 
     * @param s1 The first string to compare.
     * @param s2 The second string to compare.
     * @return The Levenshtein distance between the two strings, or -1 if the distance exceeds
     *         MAX_DISTANCE.
     */
    public static int levenshteinDistance(String s1, String s2) {
        // Exit early if the length difference is greater than the max allowed distance.
        int m = s1.length();
        int n = s2.length();
        if (Math.abs(m - n) > MAX_DISTANCE) {
            return -1;
        }

        // Use a dynamic programming table to calculate the distance.
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        // Fill the table row by row.
        for (int i = 1; i <= m; i++) {
            int minInRow = Integer.MAX_VALUE;
            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
                minInRow = Math.min(minInRow, dp[i][j]);
            }

            // Optimization: if the smallest distance in a row already exceeds the max, we can stop.
            if (i > MAX_DISTANCE && minInRow > MAX_DISTANCE) {
                return -1;
            }
        }

        // Return the final calculated distance, or -1 if it's outside the allowed threshold.
        int finalDistance = dp[m][n];
        return (finalDistance <= MAX_DISTANCE) ? finalDistance : -1;
    }
}
