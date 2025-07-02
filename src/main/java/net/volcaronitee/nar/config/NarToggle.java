package net.volcaronitee.nar.config;

import java.nio.file.Path;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.config.toggle.ChatToggle;
import net.volcaronitee.nar.config.toggle.GeneralToggle;

/**
 * Utility class for handling configuration toggles in NAR.
 */
public class NarToggle {
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve(NotARat.MOD_ID + "/toggle.json");
    private static final ConfigClassHandler<NarToggle> HANDLER =
            ConfigClassHandler.createBuilder(NarToggle.class)
                    .serializer(config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(CONFIG_PATH).appendGsonBuilder(gsonBuilder -> gsonBuilder
                                    .setPrettyPrinting().disableHtmlEscaping().serializeNulls())
                            .build())
                    .build();

    /**
     * Initializes the configuration toggles handler for NAR.
     */
    public static void init() {
        HANDLER.load();
    }

    /**
     * Gets the configuration toggles handler for NAR.
     * 
     * @return The ConfigClassHandler for NAR toggles.
     */
    public static NarToggle getHandler() {
        return HANDLER.instance();
    }

    /**
     * Creates a configuration screen for NAR.
     * 
     * @param parent The parent screen to attach the configuration screen to.
     * @return A new configuration screen for NAR.
     */
    public static Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, (defaults, config, builder) -> {
            builder.title(Text.literal("NAR Config")).category(ChatToggle.create(defaults, config));

            return builder;
        }).generateScreen(parent);
    }

    // Toggle Categories
    @SerialEntry
    public ChatToggle chat = new ChatToggle();

    @SerialEntry
    public GeneralToggle general = new GeneralToggle();
}
