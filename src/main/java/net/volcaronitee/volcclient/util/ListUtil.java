package net.volcaronitee.volcclient.util;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.OptionDescription;
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
import net.minecraft.util.Formatting;
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
    private Text description;
    private Path configPath;
    private Runnable saveCallback;

    private ConfigClassHandler<ListUtil> handler;
    private Boolean isMap = false;

    @SerialEntry
    public List<String> list = new ArrayList<String>();
    private List<String> defaultList = new ArrayList<String>();

    @SerialEntry
    public List<KeyValuePair<String, String>> map = new ArrayList<KeyValuePair<String, String>>();
    private List<KeyValuePair<String, String>> defaultMap =
            new ArrayList<KeyValuePair<String, String>>();

    /**
     * Default constructor for ListUtil.
     */
    public ListUtil() {}

    /**
     * Creates a new ListUtil instance with the specified title and configuration path.
     * 
     * @param title The title for the configuration screen.
     * @param description A brief description of the configuration.
     * @param configPath The path to the configuration file relative to the config directory.
     * @param defaultList An optional list of default values to initialize the list.
     * @param defaultMap An optional list of key-value pairs to initialize the map.
     */
    public ListUtil(String title, Text description, String configPath, List<String> defaultList,
            List<KeyValuePair<String, String>> defaultMap) {
        this.title = title;
        this.description = description;
        this.configPath = CONFIG_PATH.resolve(configPath);

        // Create handler for this ListUtil instance
        this.handler = ConfigClassHandler.createBuilder(ListUtil.class)
                .serializer(config -> GsonConfigSerializerBuilder.create(config)
                        .setPath(this.configPath).appendGsonBuilder(gsonBuilder -> gsonBuilder
                                .setPrettyPrinting().disableHtmlEscaping().serializeNulls())
                        .build())
                .build();

        // Initialize default values
        this.defaultList = defaultList;
        this.defaultMap = defaultMap;
        createDefaults(defaultList, defaultMap);

        this.handler.load();
    }

    /**
     * Gets the handler for this ListUtil instance.
     * 
     * @return The ConfigClassHandler for this ListUtil instance.
     */
    public ListUtil getHandler() {
        return handler.instance();
    }

    /**
     * Sets a callback to be invoked when the configuration is saved.
     * 
     * @param saveCallback The callback to be executed when the configuration is saved.
     */
    public void setSaveCallback(Runnable saveCallback) {
        this.saveCallback = saveCallback;
    }

    /**
     * Creates default values for the list and map if the configuration file does not exist.
     * 
     * @param defaultList The default list of strings to initialize if the config file is missing.
     * @param defaultMap The default list of key-value pairs to initialize if the config file is
     *        missing.
     */
    public void createDefaults(List<String> defaultList,
            List<KeyValuePair<String, String>> defaultMap) {
        if (!Files.exists(this.configPath)) {
            // Ensure the parent directory exists
            try {
                Files.createDirectories(this.configPath.getParent());
            } catch (IOException e) {
                VolcClient.LOGGER.error("Failed to create config directory for " + this.configPath,
                        e);
            }

            // Set default values and save the configuration
            JsonObject defaultConfig = new JsonObject();
            if (defaultList != null) {
                JsonArray listArray = new JsonArray();
                for (String item : defaultList) {
                    listArray.add(item);
                }
                defaultConfig.add("list", listArray);
            }
            if (defaultMap != null) {
                JsonArray mapArray = new JsonArray();
                for (KeyValuePair<String, String> pair : defaultMap) {
                    JsonObject pairObject = new JsonObject();
                    pairObject.addProperty("key", pair.getKey());
                    pairObject.addProperty("value", pair.getValue());
                    mapArray.add(pairObject);
                }
                defaultConfig.add("map", mapArray);
            }

            // Save the default configuration to the file
            try (BufferedWriter writer = Files.newBufferedWriter(this.configPath)) {
                JsonUtil.GSON.toJson(defaultConfig, writer);
                VolcClient.LOGGER.info("Default config written to: " + this.configPath);
            } catch (IOException e) {
                VolcClient.LOGGER.error("Failed to write default config to " + this.configPath, e);
            }
        }
    }

    /**
     * Resets the configuration by deleting the configuration file and recreating the defaults.
     */
    public void reset() {
        // Delete the configuration file
        try {
            Files.deleteIfExists(this.configPath);
            VolcClient.LOGGER.info("Configuration file reset: " + this.configPath);
        } catch (IOException e) {
            VolcClient.LOGGER.error("Failed to reset configuration file: " + this.configPath, e);
        }

        // Recreate the defaults
        createDefaults(this.defaultList, this.defaultMap);
        this.handler.load();
    }

    /**
     * Sets whether the list is treated as a map.
     * 
     * @param isMap True if the list should be treated as a map, false otherwise.
     */
    public void setIsMap(boolean isMap) {
        this.isMap = isMap;
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
                    .save(() -> {
                        if (saveCallback != null) {
                            saveCallback.run();
                        }
                        handler.save();
                    }).build();
            return builder;
        }).generateScreen(parent);
    }

    /**
     * Creates a command that opens the whitelist configuration screen.
     * 
     * @param name The name of the command to be registered.
     * @return A LiteralArgumentBuilder for the command that opens the whitelist screen.
     */
    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand(String name) {
        return literal(name).executes(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) {
                return 0;
            }

            client.send(() -> {
                client.setScreen(createScreen(client.currentScreen));
            });

            return 1;
        }).then(literal("reset").executes(context -> {
            reset();
            context.getSource().sendFeedback(TextUtil.MOD_TITLE.copy()
                    .append(Text.literal(" List reset successfully.").formatted(Formatting.GREEN)));
            return 1;
        }));
    }

    /**
     * Creates a configuration category for the ListUtil instance.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A ConfigCategory instance representing the configuration category.
     */
    public ConfigCategory createListCategory(ListUtil defaults, ListUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal(title))
                .option(ListOption.<String>createBuilder().name(Text.literal(title))
                        .description(OptionDescription.createBuilder().text(description).build())
                        .binding(config.list, () -> config.list, newVal -> config.list = newVal)
                        .controller(StringControllerBuilder::create).initial("").build())
                .build();
    }

    /**
     * Creates a configuration category for a map-like structure in the ListUtil instance.
     * 
     * @param defaults The default configuration values for the map.
     * @param config The current configuration values for the map.
     * @return A ConfigCategory instance representing the map configuration category.
     */
    public ConfigCategory createMapCategory(ListUtil defaults, ListUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal(title))
                .option(ListOption.<KeyValuePair<String, String>>createBuilder()
                        .name(Text.literal(title))
                        .description(OptionDescription.createBuilder().text(description).build())
                        .binding(config.map, () -> config.map, newVal -> config.map = newVal)
                        .controller((option) -> KeyValueController.Builder.create(option)
                                .keyController("Key", StringControllerBuilder::create)
                                .valueController("Value", StringControllerBuilder::create))
                        .initial(new KeyValuePair<>("", "")).build())
                .build();
    }
}
