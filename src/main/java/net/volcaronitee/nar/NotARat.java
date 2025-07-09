package net.volcaronitee.nar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.config.NarData;
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
import net.volcaronitee.nar.util.TitleUtil;
import net.volcaronitee.nar.util.helper.Contract;

public class NotARat implements ClientModInitializer {
    public static final String MOD_ID = "nar";
    public static final Text MOD_TITLE = Text.literal("[").formatted(Formatting.DARK_GRAY)
            .append(Text.literal("NotARat").formatted(Formatting.GOLD, Formatting.BOLD))
            .append(Text.literal("]").formatted(Formatting.DARK_GRAY));
    public static final ModContainer MOD_CONTAINER =
            net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer(MOD_ID)
                    .orElseThrow(() -> new IllegalStateException("Mod container not found"));
    public static final String MOD_VERSION =
            MOD_CONTAINER.getMetadata().getVersion().getFriendlyString();

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        Contract.init();

        LocationUtil.init();
        NarConfig.init();
        NarData.init();
        NarToggle.init();
        OverlayUtil.init();
        PartyUtil.init();
        PlayerUtil.init();
        ScheduleUtil.init();
        TablistUtil.init();
        TickUtil.init();
        TitleUtil.init();

        FeatureUtil.init();
        CommandUtil.init();
    }
}
