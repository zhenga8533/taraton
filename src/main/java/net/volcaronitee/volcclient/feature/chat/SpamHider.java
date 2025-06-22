package net.volcaronitee.volcclient.feature.chat;

import java.util.List;
import com.google.gson.JsonObject;
import net.volcaronitee.volcclient.util.JsonUtil;
import net.volcaronitee.volcclient.util.ListUtil;

public class SpamHider {
    private static final JsonObject SPAM_JSON = JsonUtil.loadTemplate("spam.json");
    private static final List<String> DEFAULT_LIST = JsonUtil.parseList(SPAM_JSON, "spam");
    public static final ListUtil EMOTE_MAP = new ListUtil("Spam Map",
            "A list of spam messages to hide in chat.", "spam_map.json", DEFAULT_LIST, null);
}
