package net.volcaronitee.volcclient.util;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.VolcClient;
import net.volcaronitee.volcclient.config.controller.KeyValueController;
import net.volcaronitee.volcclient.config.controller.KeyValueController.KeyValuePair;

/**
 * Utility class for handling configuration settings in a list format.
 */
public class ListUtil {
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve(VolcClient.MOD_ID + "/lists");

    private String title;
    private Path configPath;
    private ConfigClassHandler<ListUtil> handler;
    private Boolean isMap = false;

    /**
     * Default constructor for ListUtil.
     */
    public ListUtil() {}

    /**
     * Creates a new ListUtil instance with the specified title and configuration path.
     * 
     * @param title The title for the configuration screen.
     * @param configPath The path to the configuration file relative to the config directory.
     */
    public ListUtil(String title, String configPath) {
        this.title = title;
        this.configPath = CONFIG_PATH.resolve(configPath);
        this.handler = ConfigClassHandler.createBuilder(ListUtil.class)
                .serializer(config -> GsonConfigSerializerBuilder.create(config)
                        .setPath(this.configPath).appendGsonBuilder(gsonBuilder -> gsonBuilder
                                .setPrettyPrinting().disableHtmlEscaping().serializeNulls())
                        .build())
                .build();
        this.handler.load();
    }

    /**
     * Creates a command that opens a configuration screen when executed.
     * 
     * @param name The name of the command to be registered.
     * @return A LiteralArgumentBuilder for the command that opens the configuration screen.
     */
    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand(String name) {
        return literal(name).executes(context -> {
            MinecraftClient client = MinecraftClient.getInstance();

            client.send(() -> {
                client.setScreen(createScreen(client.currentScreen));
            });

            return 1;
        });
    }

    /**
     * Creates a configuration category for the ListUtil instance.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A ConfigCategory instance representing the configuration category.
     */
    public ConfigCategory createListCategory(ListUtil defaults, ListUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal(this.title))
                .option(ListOption.<String>createBuilder().name(Text.literal(title))
                        .binding(config.list, () -> config.list, newVal -> config.list = newVal)
                        .controller(StringControllerBuilder::create).initial("").build())
                .build();
    }

    public ConfigCategory createMapCategory(ListUtil defaults, ListUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal(this.title))
                .option(ListOption.<KeyValuePair<String, String>>createBuilder()
                        .name(Text.literal(title))
                        .binding(config.map, () -> config.map, newVal -> config.map = newVal)
                        .controller((option) -> KeyValueController.Builder.create(option)
                                .keyController("Key", StringControllerBuilder::create)
                                .valueController("Value", StringControllerBuilder::create))
                        .initial(new KeyValuePair<>("", "")).build())
                .build();
    }

    /**
     * Creates a configuration screen for the ListUtil instance.
     * 
     * @param parent The parent screen to attach the configuration screen to.
     * @return A new configuration screen for the ListUtil instance.
     */
    public Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.create(handler, (defaults, config, builder) -> {
            builder.title(Text.literal(this.title))
                    .category(isMap ? createMapCategory(defaults, config)
                            : createListCategory(defaults, config))
                    .build();
            return builder;
        }).generateScreen(parent);
    }

    /**
     * Registers a command that opens the whitelist configuration screen.
     * 
     * @param name The name of the command to be registered.
     * @return A LiteralArgumentBuilder for the command that opens the whitelist screen.
     */
    public LiteralArgumentBuilder<FabricClientCommandSource> registerCommand(String name) {
        return literal(name).executes(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) {
                return 0;
            }

            client.send(() -> {
                client.setScreen(createScreen(client.currentScreen));
            });

            return 1;
        });
    }

    /**
     * Sets whether the list is treated as a map.
     * 
     * @param isMap True if the list should be treated as a map, false otherwise.
     */
    public void setIsMap(boolean isMap) {
        this.isMap = isMap;
    }

    @SerialEntry
    public List<String> list = new ArrayList<String>();

    @SerialEntry
    public List<KeyValuePair<String, String>> map = new ArrayList<KeyValuePair<String, String>>();
}
