package net.volcaronitee.nar.feature.chat;

import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.config.NarList;
import net.volcaronitee.nar.mixin.accessor.InGameHudAccessor;
import net.volcaronitee.nar.util.ScheduleUtil;
import net.volcaronitee.nar.util.helper.Formatter;
import net.volcaronitee.nar.util.helper.Parser;

/**
 * Feature that alerts the player when certain chat messages are sent.
 */
public class ChatAlert {
    private static final ChatAlert INSTANCE = new ChatAlert();

    public static final NarList CHAT_ALERT_MAP = new NarList("Chat Alert Map", Text
            .literal("A list of chat messages to alert on. Use ")
            .append(Formatter.createLink("regex101.com", "https://regex101.com"))
            .append(Text.literal(" to test your regex patterns.\n\n\n§lOptions:§r\n\n"
                    + " --fadeIn <ticks> §7Time in ticks needed for the alert to fade in.\n"
                    + " --stay <ticks> §7Time in ticks the alert will stay on screen.\n"
                    + " --fadeOut <ticks> §7Time in ticks needed for the alert to fade out.\n"
                    + " --command <command> §7Executes a command.\n\n\n" + "§f§lExample:§r\n\n"
                    + "ROAR --fadeIn 10 --stay 70 --fadeOut 20 --command /say Hello World")),
            "chat_alert_map.json", INSTANCE::onSave);

    static {
        CHAT_ALERT_MAP.setIsMap(true);
    }

    private final List<Alert> CHAT_PATTERNS = new java.util.ArrayList<>();

    private final Queue<String> COMMAND_QUEUE = new java.util.LinkedList<>();
    private int commandDelay = 0;

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
        if (!NarConfig.getHandler().chat.chatAlert) {
            return;
        }

        // Loop through all chat patterns and check if the message matches any of them
        for (Alert alert : CHAT_PATTERNS) {
            if (alert.pattern.matcher(message.getString()).find()) {
                // Set title alert
                if (!alert.message.isEmpty()) {
                    MinecraftClient client = MinecraftClient.getInstance();
                    client.inGameHud.setTitleTicks(alert.fadeIn, alert.stay, alert.fadeOut);
                    Text title = Text.literal(alert.message);
                    client.inGameHud.setTitle(title);

                    ScheduleUtil.schedule(() -> {
                        if (((InGameHudAccessor) client.inGameHud).getTitle() == title) {
                            client.inGameHud.setDefaultTitleFade();
                        }
                    }, alert.fadeIn + alert.stay + alert.fadeOut - 1);
                }

                // Schedule the command execution if specified
                if (!alert.command.isEmpty()) {
                    commandDelay += 4;
                    COMMAND_QUEUE.add(alert.command);
                    ScheduleUtil.schedule(() -> {
                        MinecraftClient.getInstance().player.networkHandler
                                .sendChatCommand(COMMAND_QUEUE.poll());
                        commandDelay -= 4;
                    }, commandDelay);
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
            String[] parts = value.split("--");
            String message = parts[0].trim();
            int fadeIn = 10, stay = 70, fadeOut = 20;
            String command = "";

            for (String part : parts) {
                if (part.startsWith("fadeIn ")) {
                    fadeIn = Parser.parseInt(part.substring("fadeIn ".length()).trim());
                    fadeIn = fadeIn <= 0 ? 10 : fadeIn;
                } else if (part.startsWith("stay ")) {
                    stay = Parser.parseInt(part.substring("stay ".length()).trim());
                    stay = stay <= 0 ? 70 : stay;
                } else if (part.startsWith("fadeOut ")) {
                    fadeOut = Parser.parseInt(part.substring("fadeOut ".length()).trim());
                    fadeOut = fadeOut <= 0 ? 20 : fadeOut;
                } else if (part.startsWith("command ")) {
                    command = part.substring("command ".length()).trim();
                    if (command.startsWith("/")) {
                        command = command.substring(1);
                    }
                }
            }

            CHAT_PATTERNS
                    .add(new Alert(Pattern.compile(key), message, fadeIn, stay, fadeOut, command));
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
