package net.volcaronitee.volcclient.feature.chat;

import java.util.List;
import com.google.gson.JsonObject;
import net.volcaronitee.volcclient.config.controller.KeyValueController.KeyValuePair;
import net.volcaronitee.volcclient.util.JsonUtil;
import net.volcaronitee.volcclient.util.ListUtil;

public class CustomEmote {
    private static final JsonObject EMOTE_JSON = JsonUtil.loadTemplate("emotes.json");
    private static final List<KeyValuePair<String, String>> DEFAULT_MAP =
            JsonUtil.parseKeyValuePairs(EMOTE_JSON, "emotes");
    public static final ListUtil EMOTE_MAP = new ListUtil("Emote Map",
            "A list of custom emote mappings to use in chat.", "emote_map.json", null, DEFAULT_MAP);

    static {
        EMOTE_MAP.setIsMap(true);
    }
}
