package net.volcaronitee.volcclient.feature.chat;

import java.util.List;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.volcaronitee.volcclient.config.controller.KeyValueController.KeyValuePair;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.ListUtil;
import net.volcaronitee.volcclient.util.TextUtil;

/**
 * Feature that alerts the player when certain chat messages are sent.
 */
public class ChatAlert {
    private static final ChatAlert INSTANCE = new ChatAlert();

    public static final ListUtil CHAT_ALERT_MAP = new ListUtil("Chat Alert Map",
            Text.literal("A list of chat messages to alert on.\n\nUse ")
                    .append(TextUtil.getInstance().createLink("regex101.com",
                            "https://regex101.com"))
                    .append(Text.literal(" to test your regex patterns.")),
            "chat_alert_map.json");

    private static List<Pair<Pattern, String>> CHAT_PATTERNS = new java.util.ArrayList<>();

    static {
        CHAT_ALERT_MAP.setIsMap(true);
        CHAT_ALERT_MAP.setSaveCallback(INSTANCE::onSave);
    }

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
        if (!ConfigUtil.getHandler().chat.chatAlert) {
            return;
        }

        // Loop through all chat patterns and check if the message matches any of them
        for (Pair<Pattern, String> entry : CHAT_PATTERNS) {
            Pattern pattern = entry.getLeft();
            String alertMessage = entry.getRight();

            if (pattern.matcher(message.getString()).find()) {
                MinecraftClient.getInstance().inGameHud.setTitle(Text.literal(alertMessage));
                break;
            }
        }
    }

    /**
     * Callback to save the spam patterns when the map is modified.
     */
    private void onSave() {
        CHAT_PATTERNS.clear();
        for (KeyValuePair<String, KeyValuePair<String, Boolean>> pattern : CHAT_ALERT_MAP
                .getHandler().map) {
            // Skip patterns that are disabled
            if (!pattern.getValue().getValue()) {
                continue;
            }

            CHAT_PATTERNS
                    .add(new Pair<>(Pattern.compile(pattern.getKey(), Pattern.CASE_INSENSITIVE),
                            pattern.getValue().getKey()));
        }
    }
}
