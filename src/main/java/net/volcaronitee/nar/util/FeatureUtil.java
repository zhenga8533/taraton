package net.volcaronitee.nar.util;

import net.volcaronitee.nar.feature.chat.ChatAlert;
import net.volcaronitee.nar.feature.chat.ChatCommands;
import net.volcaronitee.nar.feature.chat.CustomEmote;
import net.volcaronitee.nar.feature.chat.JoinWhitelist;
import net.volcaronitee.nar.feature.chat.PlaytimeWarning;
import net.volcaronitee.nar.feature.chat.SpamHider;
import net.volcaronitee.nar.feature.combat.EntityHighlight;
import net.volcaronitee.nar.feature.fishing.HookLineAndSinker;
import net.volcaronitee.nar.feature.general.ImagePreview;
import net.volcaronitee.nar.feature.general.NoMouseReset;
import net.volcaronitee.nar.feature.general.RemoveSelfieMode;
import net.volcaronitee.nar.feature.general.ServerRejoinAlert;
import net.volcaronitee.nar.feature.general.ServerStatus;
import net.volcaronitee.nar.feature.general.SkyBlockLevelUpAlert;
import net.volcaronitee.nar.feature.general.SkyBlockXpAlert;
import net.volcaronitee.nar.feature.general.WaypointMaker;
import net.volcaronitee.nar.feature.general.WidgetDisplay;

public class FeatureUtil {
    public static void init() {
        // General Features
        ImagePreview.register();
        NoMouseReset.register();
        RemoveSelfieMode.register();
        ServerRejoinAlert.register();
        ServerStatus.register();
        WaypointMaker.register();
        WidgetDisplay.register();

        // Chat Features
        ChatAlert.register();
        ChatCommands.register();
        CustomEmote.register();
        JoinWhitelist.register();
        PlaytimeWarning.register();
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
