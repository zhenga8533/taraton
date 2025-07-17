package net.volcaronitee.taraton.config;

import java.nio.file.Path;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.toggle.ChatToggle;
import net.volcaronitee.taraton.config.toggle.GeneralToggle;

/**
 * Utility class for handling configuration toggles in Taraton.
 */
public class TaratonToggle {
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve(Taraton.MOD_ID + "/toggle.json");
    private static final ConfigClassHandler<TaratonToggle> HANDLER =
            ConfigClassHandler.createBuilder(TaratonToggle.class)
                    .serializer(config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(CONFIG_PATH).appendGsonBuilder(gsonBuilder -> gsonBuilder
                                    .setPrettyPrinting().disableHtmlEscaping().serializeNulls())
                            .build())
                    .build();

    /**
     * Initializes the configuration toggles handler for Taraton.
     */
    public static void init() {
        HANDLER.load();
    }

    /**
     * Gets the configuration toggles handler for Taraton.
     * 
     * @return The ConfigClassHandler for Taraton toggles.
     */
    public static TaratonToggle getInstance() {
        return HANDLER.instance();
    }

    /**
     * Creates a configuration screen for Taraton.
     * 
     * @param parent The parent screen to attach the configuration screen to.
     * @return A new configuration screen for Taraton.
     */
    public static Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, (defaults, config, builder) -> {
            builder.title(Text.literal("Taraton Toggles"))
                    .category(GeneralToggle.create(defaults, config))
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
