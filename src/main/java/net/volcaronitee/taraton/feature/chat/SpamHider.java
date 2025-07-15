package net.volcaronitee.taraton.feature.chat;

import java.util.List;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonList;
import net.volcaronitee.taraton.util.FormatUtil;

/**
 * Feature to filter out spam messages in chat.
 */
public class SpamHider {
    private static final SpamHider INSTANCE = new SpamHider();

    public static final TaratonList SPAM_LIST = new TaratonList("Spam List",
            Text.literal("A list of spam messages to hide in chat.\n\nUse ")
                    .append(FormatUtil.createLink("regex101.com", "https://regex101.com"))
                    .append(Text.literal(" to test your regex patterns.")),
            "spam_list.json", new String[] {"Pattern"});
    static {
        SPAM_LIST.setSaveCallback(INSTANCE::onSave);
    }

    private static List<Pattern> SPAM_PATTERNS = new java.util.ArrayList<>();

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
        if (!TaratonConfig.getHandler().chat.spamHider || overlay) {
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
        for (String pattern : SPAM_LIST.list) {
            SPAM_PATTERNS.add(Pattern.compile(pattern));
        }
    }
}
