package net.volcaronitee.volcclient.util;

import net.volcaronitee.volcclient.feature.chat.ChatAlert;
import net.volcaronitee.volcclient.feature.chat.ChatCommands;
import net.volcaronitee.volcclient.feature.chat.CustomEmote;
import net.volcaronitee.volcclient.feature.chat.JoinWhitelist;
import net.volcaronitee.volcclient.feature.chat.PlaytimeWarning;
import net.volcaronitee.volcclient.feature.chat.SpamHider;
import net.volcaronitee.volcclient.feature.combat.EntityHighlight;
import net.volcaronitee.volcclient.feature.fishing.HookLineAndSinker;
import net.volcaronitee.volcclient.feature.general.RemoveSelfieMode;
import net.volcaronitee.volcclient.feature.general.ServerStatus;
import net.volcaronitee.volcclient.feature.general.SkyBlockLevelUpAlert;
import net.volcaronitee.volcclient.feature.general.SkyBlockXpAlert;
import net.volcaronitee.volcclient.feature.general.WidgetDisplay;

public class FeatureUtil {
    public static void init() {
        // General Features
        RemoveSelfieMode.register();
        WidgetDisplay.register();

        // Chat Features
        ChatAlert.register();
        ChatCommands.register();
        CustomEmote.register();
        JoinWhitelist.register();
        PlaytimeWarning.register();
        ServerStatus.register();
        SpamHider.register();
        SkyBlockLevelUpAlert.register();
        SkyBlockXpAlert.register();

        // Container Features
        // Searchbar.register();

        // Combat Features
        EntityHighlight.register();

        // Fishing Features
        HookLineAndSinker.register();
    }
}
