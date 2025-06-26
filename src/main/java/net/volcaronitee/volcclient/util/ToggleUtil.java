package net.volcaronitee.volcclient.util;

import java.nio.file.Path;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.VolcClient;
import net.volcaronitee.volcclient.config.toggle.ChatToggle;
import net.volcaronitee.volcclient.config.toggle.GeneralToggle;

/**
 * Utility class for handling configuration toggles in Volc Client.
 */
public class ToggleUtil {
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve(VolcClient.MOD_ID + "/toggle.json");
    private static final ConfigClassHandler<ToggleUtil> HANDLER =
            ConfigClassHandler.createBuilder(ToggleUtil.class)
                    .serializer(config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(CONFIG_PATH).appendGsonBuilder(gsonBuilder -> gsonBuilder
                                    .setPrettyPrinting().disableHtmlEscaping().serializeNulls())
                            .build())
                    .build();

    /**
     * Initializes the configuration toggles handler for Volc Client.
     */
    public static void init() {
        HANDLER.load();
    }

    /**
     * Gets the configuration toggles handler for Volc Client.
     * 
     * @return The ConfigClassHandler for Volc Client toggles.
     */
    public static ToggleUtil getHandler() {
        return HANDLER.instance();
    }

    /**
     * Creates a configuration screen for Volc Client.
     * 
     * @param parent The parent screen to attach the configuration screen to.
     * @return A new configuration screen for Volc Client.
     */
    public static Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, (defaults, config, builder) -> {
            builder.title(Text.literal("Volc Client Config"))
                    .category(ChatToggle.create(defaults, config));

            return builder;
        }).generateScreen(parent);
    }

    // Toggle Categories
    @SerialEntry
    public ChatToggle chat = new ChatToggle();

    @SerialEntry
    public GeneralToggle general = new GeneralToggle();
}
