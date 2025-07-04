package net.volcaronitee.nar.feature.chat;

import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.config.NarList;
import net.volcaronitee.nar.util.ScheduleUtil;
import net.volcaronitee.nar.util.helper.Formatter;

/**
 * Feature that alerts the player when certain chat messages are sent.
 */
public class ChatAlert {
    private static final ChatAlert INSTANCE = new ChatAlert();

    public static final NarList CHAT_ALERT_MAP = new NarList("Chat Alert Map",
            Text.literal("A list of chat messages to alert on. Use ")
                    .append(Formatter.createLink("regex101.com", "https://regex101.com"))
                    .append(Text.literal(" to test your regex patterns.\n\n\n§lOptions:§r\n\n"
                            + " --command [command] §7Executes a command.\n")),
            "chat_alert_map.json", INSTANCE::onSave);

    private static List<Pair<Pattern, String>> CHAT_PATTERNS = new java.util.ArrayList<>();

    private Queue<String> commandQueue = new java.util.LinkedList<>();
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
        for (Pair<Pattern, String> entry : CHAT_PATTERNS) {
            Pattern pattern = entry.getLeft();
            String[] args = entry.getRight().split("--");

            String alertMessage = "";
            String command = "";

            for (String arg : args) {
                if (arg.startsWith("command ")) {
                    // Extract the command from the argument
                    command = arg.substring("command ".length()).trim();
                    if (command.startsWith("/")) {
                        command = command.substring(1);
                    }
                } else if (arg == args[0]) {
                    // The first argument is the alert message
                    alertMessage = arg.trim();
                }
            }

            if (pattern.matcher(message.getString()).find()) {
                // If an alert message is specified, display it to the player
                if (!alertMessage.isEmpty()) {
                    MinecraftClient.getInstance().inGameHud.setTitle(Text.literal(alertMessage));
                }

                // If a command is specified, execute it
                if (!command.isEmpty()) {
                    INSTANCE.commandDelay += 4;
                    commandQueue.add(command);
                    ScheduleUtil.schedule(() -> {
                        MinecraftClient.getInstance().player.networkHandler
                                .sendChatCommand(INSTANCE.commandQueue.poll());
                        INSTANCE.commandDelay -= 4;
                    }, INSTANCE.commandDelay);
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
            CHAT_PATTERNS.add(new Pair<>(Pattern.compile(key), value));
        });
    }
}
