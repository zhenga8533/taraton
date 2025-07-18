package net.volcaronitee.taraton.util;

import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.feature.chat.AutoCommand;
import net.volcaronitee.taraton.feature.chat.AutoKick;
import net.volcaronitee.taraton.feature.chat.AutoTransfer;
import net.volcaronitee.taraton.feature.chat.ChatAlert;
import net.volcaronitee.taraton.feature.chat.ChatCommands;
import net.volcaronitee.taraton.feature.chat.CustomEmote;
import net.volcaronitee.taraton.feature.chat.JoinMessage;
import net.volcaronitee.taraton.feature.chat.JoinParty;
import net.volcaronitee.taraton.feature.chat.PlaytimeWarning;
import net.volcaronitee.taraton.feature.chat.SpamHider;
import net.volcaronitee.taraton.feature.combat.EntityHighlight;
import net.volcaronitee.taraton.feature.combat.LowHealthWarning;
import net.volcaronitee.taraton.feature.container.ArmorDisplay;
import net.volcaronitee.taraton.feature.container.ContainerPreview;
import net.volcaronitee.taraton.feature.container.SlotBinding;
import net.volcaronitee.taraton.feature.container.WardrobeSwap;
import net.volcaronitee.taraton.feature.crimson_isle.VanquisherWarp;
import net.volcaronitee.taraton.feature.fishing.HookLineAndSinker;
import net.volcaronitee.taraton.feature.general.DeveloperKey;
import net.volcaronitee.taraton.feature.general.ImagePreview;
import net.volcaronitee.taraton.feature.general.ReminderTimer;
import net.volcaronitee.taraton.feature.general.ServerRejoinAlert;
import net.volcaronitee.taraton.feature.general.ServerStatus;
import net.volcaronitee.taraton.feature.general.SkyBlockLevelUpAlert;
import net.volcaronitee.taraton.feature.general.SkyBlockXpAlert;
import net.volcaronitee.taraton.feature.general.UpdateNotification;
import net.volcaronitee.taraton.feature.general.WaypointMaker;
import net.volcaronitee.taraton.feature.general.WidgetDisplay;
import net.volcaronitee.taraton.feature.qol.AutoFusion;
import net.volcaronitee.taraton.feature.qol.AutoSalvage;
import net.volcaronitee.taraton.feature.qol.DragShiftClick;
import net.volcaronitee.taraton.feature.qol.NoMouseReset;
import net.volcaronitee.taraton.feature.qol.RemoveSelfieMode;

public class FeatureUtil {
    public static void init() {
        // General Features
        DeveloperKey.register();
        ImagePreview.register();
        ReminderTimer.register();
        ServerRejoinAlert.register();
        ServerStatus.register();
        UpdateNotification.register();
        WaypointMaker.register();
        WidgetDisplay.register();

        // QOL Features
        AutoFusion.register();
        AutoSalvage.register();
        DragShiftClick.register();
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
        ArmorDisplay.register();
        ContainerPreview.register();
        // Searchbar.register();
        SlotBinding.register();
        WardrobeSwap.register();

        // Combat Features
        EntityHighlight.register();
        LowHealthWarning.register();

        // Fishing Features
        HookLineAndSinker.register();

        // Crimson Isle Features
        VanquisherWarp.register();
    }

    /**
     * Checks if a feature is enabled based on the configuration and global settings.
     * 
     * @param base The base condition to check if the feature should be enabled.
     * @return True if the feature is enabled, false otherwise.
     */
    public static boolean isEnabled(boolean base) {
        // Check if the base condition is true
        if (!base) {
            return false;
        }

        // Check if the mod is enabled globally
        if (!TaratonConfig.getInstance().general.modEnabled) {
            return false;
        }

        // Check for SkyBlock only mode
        if (TaratonConfig.getInstance().general.skyblockOnly
                && !LocationUtil.getMode().equals("SkyBlock")) {
            return false;
        }

        return true;
    }
}
