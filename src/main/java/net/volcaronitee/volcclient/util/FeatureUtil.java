package net.volcaronitee.volcclient.util;

import net.volcaronitee.volcclient.feature.chat.PlaytimeWarning;
import net.volcaronitee.volcclient.feature.general.RemoveSelfieMode;

public class FeatureUtil {
    public static void init() {
        // General Features
        RemoveSelfieMode.register();

        // Chat Features
        PlaytimeWarning.register();
    }
}
