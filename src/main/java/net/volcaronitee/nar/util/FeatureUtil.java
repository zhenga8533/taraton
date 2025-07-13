package net.volcaronitee.nar.util;

import net.volcaronitee.nar.feature.chat.AutoCommand;
import net.volcaronitee.nar.feature.chat.AutoKick;
import net.volcaronitee.nar.feature.chat.AutoTransfer;
import net.volcaronitee.nar.feature.chat.ChatAlert;
import net.volcaronitee.nar.feature.chat.ChatCommands;
import net.volcaronitee.nar.feature.chat.CustomEmote;
import net.volcaronitee.nar.feature.chat.JoinMessage;
import net.volcaronitee.nar.feature.chat.JoinParty;
import net.volcaronitee.nar.feature.chat.PlaytimeWarning;
import net.volcaronitee.nar.feature.chat.SpamHider;
import net.volcaronitee.nar.feature.combat.EntityHighlight;
import net.volcaronitee.nar.feature.combat.LowHealthWarning;
import net.volcaronitee.nar.feature.crimson_isle.AttributeSalvager;
import net.volcaronitee.nar.feature.crimson_isle.VanquisherWarp;
import net.volcaronitee.nar.feature.fishing.HookLineAndSinker;
import net.volcaronitee.nar.feature.general.ImagePreview;
import net.volcaronitee.nar.feature.general.ReminderTimer;
import net.volcaronitee.nar.feature.general.ServerRejoinAlert;
import net.volcaronitee.nar.feature.general.ServerStatus;
import net.volcaronitee.nar.feature.general.SkyBlockLevelUpAlert;
import net.volcaronitee.nar.feature.general.SkyBlockXpAlert;
import net.volcaronitee.nar.feature.general.WaypointMaker;
import net.volcaronitee.nar.feature.general.WidgetDisplay;
import net.volcaronitee.nar.feature.qol.NoMouseReset;
import net.volcaronitee.nar.feature.qol.RemoveSelfieMode;

public class FeatureUtil {
    public static void init() {
        // General Features
        ImagePreview.register();
        ReminderTimer.register();
        ServerRejoinAlert.register();
        ServerStatus.register();
        WaypointMaker.register();
        WidgetDisplay.register();

        // QOL Features
        NoMouseReset.register();
        RemoveSelfieMode.register();

        // Chat Features
        AutoCommand.register();
        AutoKick.register();
        AutoTransfer.register();
        ChatAlert.register();
        ChatCommands.register();
        CustomEmote.register();
        JoinMessage.register();
        JoinParty.register();
        PlaytimeWarning.register();
        SpamHider.register();
        SkyBlockLevelUpAlert.register();
        SkyBlockXpAlert.register();

        // Container Features
        // Searchbar.register();

        // Combat Features
        EntityHighlight.register();
        LowHealthWarning.register();

        // Fishing Features
        HookLineAndSinker.register();

        // Crimson Isle Features
        AttributeSalvager.register();
        VanquisherWarp.register();
    }
}
