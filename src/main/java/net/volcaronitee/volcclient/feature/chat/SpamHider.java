package net.volcaronitee.volcclient.feature.chat;

import java.util.List;
import java.util.regex.Pattern;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.JsonUtil;
import net.volcaronitee.volcclient.util.ListUtil;
import net.volcaronitee.volcclient.util.TextUtil;

/**
 * Feature to filter out spam messages in chat.
 */
public class SpamHider {
    private static final JsonObject SPAM_JSON = JsonUtil.loadTemplate("spam.json");
    private static final List<String> DEFAULT_LIST = JsonUtil.parseList(SPAM_JSON, "spam");
    public static final ListUtil SPAM_LIST = new ListUtil("Spam List",
            Text.literal("A list of spam messages to hide in chat.\n\nUse ")
                    .append(TextUtil.createLink("regex101.com", "https://regex101.com"))
                    .append(Text.literal(" to test your regex patterns.")),
            "spam_list.json", DEFAULT_LIST, null);

    private static List<Pattern> SPAM_PATTERNS = new java.util.ArrayList<>();

    static {
        SPAM_LIST.setSaveCallback(SpamHider::onSave);
        onSave();
    }

    /**
     * Registers the spam hider to filter out spam messages from chat.
     */
    public static void register() {
        ClientReceiveMessageEvents.ALLOW_CHAT
                .register((message, signedMessage, sender, params, timestamp) -> {
                    return !isSpam(message);
                });

        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            return !isSpam(message);
        });
    }

    /**
     * Checks if the given message matches any spam patterns.
     * 
     * @param message The message to check.
     * @return True if the message is considered spam, false otherwise.
     */
    private static boolean isSpam(Text message) {
        if (!ConfigUtil.getHandler().chat.spamHider) {
            return false;
        }

        String text = message.getString();

        for (Pattern pattern : SPAM_PATTERNS) {
            if (pattern.matcher(text).find()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Callback to save the spam patterns when the list is modified.
     */
    private static void onSave() {
        SPAM_PATTERNS.clear();
        for (String pattern : SPAM_LIST.getHandler().list) {
            SPAM_PATTERNS.add(Pattern.compile(pattern));
        }
    }
}
