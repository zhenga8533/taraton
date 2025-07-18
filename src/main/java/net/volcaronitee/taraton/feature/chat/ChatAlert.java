package net.volcaronitee.taraton.feature.chat;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonList;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.FormatUtil;
import net.volcaronitee.taraton.util.ParseUtil;
import net.volcaronitee.taraton.util.ScheduleUtil;
import net.volcaronitee.taraton.util.TitleUtil;

/**
 * Feature that alerts the player when certain chat messages are sent.
 */
public class ChatAlert {
    private static final ChatAlert INSTANCE = new ChatAlert();

    public static final TaratonList CHAT_ALERT_MAP = new TaratonList("Chat Alert Map", Text
            .literal("A list of chat messages to alert on. Use ")
            .append(FormatUtil.createLink("regex101.com", "https://regex101.com"))
            .append(Text.literal(" to test your regex patterns.\n\n\n§lOptions:§r\n\n"
                    + " --fadeIn <ticks> §7Time in ticks needed for the alert to fade in.\n"
                    + " --stay <ticks> §7Time in ticks the alert will stay on screen.\n"
                    + " --fadeOut <ticks> §7Time in ticks needed for the alert to fade out.\n"
                    + " --command <command> §7Executes a command.\n\n\n" + "§f§lExample:§r\n\n"
                    + "ROAR --fadeIn 10 --stay 70 --fadeOut 20 --command /say Hello World")),
            "chat_alert_map.json", new String[] {"Pattern", "Message"});
    static {
        CHAT_ALERT_MAP.setIsMap(true);
        CHAT_ALERT_MAP.setSaveCallback(INSTANCE::onSave);
    }

    private final List<Alert> CHAT_PATTERNS = new java.util.ArrayList<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private ChatAlert() {}

    /**
     * Registers the chat alert handler to listen for incoming messages.
     */
    public static void register() {
        ClientReceiveMessageEvents.GAME.register(INSTANCE::handleChatAlert);
    }

    /**
     * Handles incoming chat messages and checks if they match any configured alerts.
     * 
     * @param message The chat message to check.
     * @param overlay Whether the message is an overlay message.
     */
    private void handleChatAlert(Text message, boolean overlay) {
        if (!FeatureUtil.isEnabled(TaratonConfig.getInstance().chat.chatAlert)) {
            return;
        }

        // Loop through all chat patterns and check if the message matches any of them
        for (Alert alert : CHAT_PATTERNS) {
            if (alert.pattern.matcher(message.getString()).find()) {
                // Set title alert
                if (!alert.message.isEmpty()) {
                    TitleUtil.createTitle(alert.message, "", 0, alert.fadeIn, alert.stay,
                            alert.fadeOut);
                }

                // Schedule the command execution if specified
                if (!alert.command.isEmpty()) {
                    ScheduleUtil.scheduleCommand(alert.command);
                }
                break;
            }
        }
    }

    /**
     * Callback to save the spam patterns when the map is modified.
     */
    private void onSave() {
        CHAT_PATTERNS.clear();
        CHAT_ALERT_MAP.map.forEach((key, value) -> {
            // Split the key into parts to extract the regex pattern and options
            String[] parts = key.split("--");
            Pattern pattern = Pattern.compile(parts[0].trim());
            parts = Arrays.copyOfRange(parts, 1, parts.length);

            // Default values for fadeIn, stay, fadeOut, and command
            String message = value;
            int fadeIn = 10, stay = 70, fadeOut = 20;
            String command = "";

            // Parse the remaining parts for fadeIn, stay, fadeOut, and command
            for (String part : parts) {
                if (part.startsWith("fadeIn ")) {
                    fadeIn = ParseUtil.parseInt(part.substring("fadeIn ".length()).trim());
                    fadeIn = fadeIn <= 0 ? 10 : fadeIn;
                } else if (part.startsWith("stay ")) {
                    stay = ParseUtil.parseInt(part.substring("stay ".length()).trim());
                    stay = stay <= 0 ? 70 : stay;
                } else if (part.startsWith("fadeOut ")) {
                    fadeOut = ParseUtil.parseInt(part.substring("fadeOut ".length()).trim());
                    fadeOut = fadeOut <= 0 ? 20 : fadeOut;
                } else if (part.startsWith("command ")) {
                    command = part.substring("command ".length()).trim();
                    if (command.startsWith("/")) {
                        command = command.substring(1);
                    }
                }
            }

            CHAT_PATTERNS.add(new Alert(pattern, message, fadeIn, stay, fadeOut, command));
        });
    }

    /**
     * Represents an alert configuration for chat messages.
     */
    private class Alert {
        private final Pattern pattern;
        private final String message;
        private final int fadeIn;
        private final int stay;
        private final int fadeOut;
        private final String command;

        /**
         * Constructor for creating a new Alert instance.
         * 
         * @param pattern The regex pattern to match against chat messages.
         * @param message The message to display when the pattern matches.
         * @param fadeIn Time in ticks needed for the alert to fade in.
         * @param stay Time in ticks the alert will stay on screen.
         * @param fadeOut Time in ticks needed for the alert to fade out.
         * @param command The command to execute when the pattern matches.
         */
        public Alert(Pattern pattern, String message, int fadeIn, int stay, int fadeOut,
                String command) {
            this.pattern = pattern;
            this.message = message;
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
            this.command = command;
        }
    }

}
