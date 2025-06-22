package net.volcaronitee.volcclient.feature.chat;

import java.util.List;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.volcaronitee.volcclient.config.controller.KeyValueController.KeyValuePair;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.JsonUtil;
import net.volcaronitee.volcclient.util.ListUtil;

/**
 * Handles custom emote mappings for chat messages.
 */
public class CustomEmote {
    private static final JsonObject EMOTE_JSON = JsonUtil.loadTemplate("emotes.json");
    private static final List<KeyValuePair<String, String>> DEFAULT_MAP =
            JsonUtil.parseKeyValuePairs(EMOTE_JSON, "emotes");
    public static final ListUtil EMOTE_MAP = new ListUtil("Emote Map",
            "A list of custom emote mappings to use in chat.", "emote_map.json", null, DEFAULT_MAP);

    static {
        EMOTE_MAP.setIsMap(true);
    }

    /**
     * Registers the custom emote mapping to modify chat messages.
     */
    public static void register() {
        ClientSendMessageEvents.MODIFY_CHAT.register(message -> {
            if (!ConfigUtil.getHandler().chat.customEmotes) {
                return message;
            }

            // Replace each emote key with its corresponding value in the message
            for (KeyValuePair<String, String> pair : EMOTE_MAP.getHandler().map) {
                if (message.contains(pair.getKey())) {
                    message = message.replace(pair.getKey(), pair.getValue());
                }
            }
            return message;
        });
    }
}
