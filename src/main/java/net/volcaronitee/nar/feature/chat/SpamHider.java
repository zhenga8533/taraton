package net.volcaronitee.nar.feature.chat;

import java.util.List;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.config.NarList;
import net.volcaronitee.nar.config.controller.KeyValueController.KeyValuePair;
import net.volcaronitee.nar.util.helper.Formatter;

/**
 * Feature to filter out spam messages in chat.
 */
public class SpamHider {
    private static final SpamHider INSTANCE = new SpamHider();

    public static final NarList SPAM_LIST = new NarList("Spam List",
            Text.literal("A list of spam messages to hide in chat.\n\nUse ")
                    .append(Formatter.createLink("regex101.com", "https://regex101.com"))
                    .append(Text.literal(" to test your regex patterns.")),
            "spam_list.json");

    private static List<Pattern> SPAM_PATTERNS = new java.util.ArrayList<>();

    static {
        SPAM_LIST.setSaveCallback(INSTANCE::onSave);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private SpamHider() {}

    /**
     * Registers the spam hider to filter out spam messages from chat.
     */
    public static void register() {
        ClientReceiveMessageEvents.ALLOW_CHAT
                .register((message, signedMessage, sender, params, timestamp) -> {
                    return INSTANCE.allowMessage(message, false);
                });
        ClientReceiveMessageEvents.ALLOW_GAME.register(INSTANCE::allowMessage);
    }

    /**
     * Checks if a message is considered spam based on the configured patterns.
     * 
     * @param message The message to check.
     * @param overlay Whether the message is an overlay message.
     * @return True if the message is not spam, false if it is spam.
     */
    private boolean allowMessage(Text message, boolean overlay) {
        if (!NarConfig.getHandler().chat.spamHider || overlay) {
            return true;
        }

        String text = message.getString();

        for (Pattern pattern : SPAM_PATTERNS) {
            if (pattern.matcher(text).find()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Callback to save the spam patterns when the list is modified.
     */
    private void onSave() {
        SPAM_PATTERNS.clear();
        for (KeyValuePair<String, Boolean> pattern : SPAM_LIST.getHandler().list) {
            // Skip patterns that are disabled
            if (!pattern.getValue()) {
                continue;
            }

            SPAM_PATTERNS.add(Pattern.compile(pattern.getKey()));
        }
    }
}
