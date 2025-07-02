package net.volcaronitee.nar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.ModContainer;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.config.NarToggle;
import net.volcaronitee.nar.util.CommandUtil;
import net.volcaronitee.nar.util.FeatureUtil;
import net.volcaronitee.nar.util.LocationUtil;
import net.volcaronitee.nar.util.OverlayUtil;
import net.volcaronitee.nar.util.PartyUtil;
import net.volcaronitee.nar.util.PlayerUtil;
import net.volcaronitee.nar.util.ScheduleUtil;
import net.volcaronitee.nar.util.TablistUtil;
import net.volcaronitee.nar.util.TickUtil;

public class NotARat implements ClientModInitializer {
    public static final String MOD_ID = "nar";
    public static final ModContainer MOD_CONTAINER =
            net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer(MOD_ID)
                    .orElseThrow(() -> new IllegalStateException("Mod container not found"));
    public static final String MOD_VERSION =
            MOD_CONTAINER.getMetadata().getVersion().getFriendlyString();

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        CommandUtil.init();
        NarConfig.init();
        FeatureUtil.init();
        LocationUtil.init();
        OverlayUtil.init();
        PartyUtil.init();
        PlayerUtil.init();
        ScheduleUtil.init();
        TablistUtil.init();
        NarToggle.init();
        TickUtil.init();
    }
}
