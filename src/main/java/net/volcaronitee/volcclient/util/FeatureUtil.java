package net.volcaronitee.volcclient.util;

import net.volcaronitee.volcclient.feature.chat.CustomEmote;
import net.volcaronitee.volcclient.feature.chat.JoinWhitelist;
import net.volcaronitee.volcclient.feature.chat.PlaytimeWarning;
import net.volcaronitee.volcclient.feature.chat.SpamHider;
import net.volcaronitee.volcclient.feature.general.RemoveSelfieMode;

public class FeatureUtil {
    public static void init() {
        // General Features
        RemoveSelfieMode.register();

        // Chat Features
        CustomEmote.register();
        JoinWhitelist.register();
        PlaytimeWarning.register();
        SpamHider.register();
    }
}
