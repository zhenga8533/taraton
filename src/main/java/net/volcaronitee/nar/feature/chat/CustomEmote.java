package net.volcaronitee.nar.feature.chat;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.text.Text;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.config.controller.KeyValueController.KeyValuePair;
import net.volcaronitee.nar.util.ListUtil;

/**
 * Handles custom emote mappings for chat messages.
 */
public class CustomEmote {
    private static final CustomEmote INSTANCE = new CustomEmote();

    public static final ListUtil EMOTE_MAP = new ListUtil("Emote Map",
            Text.literal("A list of custom emote mappings to use in chat."), "emote_map.json");

    static {
        EMOTE_MAP.setIsMap(true);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private CustomEmote() {}

    /**
     * Registers the custom emote mapping to modify chat messages.
     */
    public static void register() {
        ClientSendMessageEvents.MODIFY_CHAT.register(INSTANCE::handleCustomEmote);
    }

    /**
     * Handles custom emote replacements in chat messages.
     * 
     * @param message The chat message to process.
     * @return The modified chat message with custom emotes replaced.
     */
    private String handleCustomEmote(String message) {
        if (!NarConfig.getHandler().chat.customEmotes) {
            return message;
        }

        // Replace each emote key with its corresponding value in the message
        for (KeyValuePair<String, KeyValuePair<String, Boolean>> pair : EMOTE_MAP
                .getHandler().map) {
            // Skip if the emote is disabled
            if (!pair.getValue().getValue() || !message.contains(pair.getKey())) {
                continue;
            }

            message = message.replace(pair.getKey(), pair.getValue().getKey());
        }

        return message;
    }
}
