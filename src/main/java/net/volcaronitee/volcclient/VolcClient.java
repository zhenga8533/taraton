package net.volcaronitee.volcclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.ModContainer;
import net.volcaronitee.volcclient.util.CommandUtil;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.OverlayUtil;

public class VolcClient implements ClientModInitializer {
    public static final String MOD_ID = "volc-client";
    public static final ModContainer MOD_CONTAINER =
            net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer(MOD_ID)
                    .orElseThrow(() -> new IllegalStateException("Mod container not found"));
    public static final String MOD_VERSION =
            MOD_CONTAINER.getMetadata().getVersion().getFriendlyString();

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        CommandUtil.init();
        ConfigUtil.init();
        OverlayUtil.init();
    }
}
