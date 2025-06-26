package net.volcaronitee.volcclient.util;

import net.volcaronitee.volcclient.feature.chat.ChatCommands;
import net.volcaronitee.volcclient.feature.chat.CustomEmote;
import net.volcaronitee.volcclient.feature.chat.JoinWhitelist;
import net.volcaronitee.volcclient.feature.chat.PlaytimeWarning;
import net.volcaronitee.volcclient.feature.chat.SpamHider;
import net.volcaronitee.volcclient.feature.general.RemoveSelfieMode;
import net.volcaronitee.volcclient.feature.general.ServerStatus;
import net.volcaronitee.volcclient.feature.general.SkyBlockLevelUpAlert;
import net.volcaronitee.volcclient.feature.general.SkyBlockXpAlert;

public class FeatureUtil {
    public static void init() {
        // General Features
        RemoveSelfieMode.register();

        // Chat Features
        ChatCommands.register();
        CustomEmote.register();
        JoinWhitelist.register();
        PlaytimeWarning.register();
        ServerStatus.register();
        SpamHider.register();
        SkyBlockLevelUpAlert.register();
        SkyBlockXpAlert.register();
    }
}
