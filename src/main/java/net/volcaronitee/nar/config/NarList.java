package net.volcaronitee.nar.config;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.config.controller.KeyValueController;
import net.volcaronitee.nar.config.controller.KeyValueController.KeyValuePair;

/**
 * Utility class for handling configuration settings in a list format.
 */
public class NarList {
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve(NotARat.MOD_ID + "/lists");

    static {
        // Ensure the configuration directory exists
        try {
            Files.createDirectories(CONFIG_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create configuration directory: " + CONFIG_PATH,
                    e);
        }
    }

    private String title;
    private Text description;
    private String fileName;
    private Runnable saveCallback;

    private ConfigClassHandler<NarList> handler;
    private Boolean isMap = false;

    public final Set<String> list = new java.util.HashSet<>();
    @SerialEntry
    private List<KeyValuePair<String, Boolean>> listConfig =
            new ArrayList<KeyValuePair<String, Boolean>>();

    public final Map<String, String> map = new java.util.HashMap<>();
    @SerialEntry
    private List<KeyValuePair<String, KeyValuePair<String, Boolean>>> mapConfig =
            new ArrayList<KeyValuePair<String, KeyValuePair<String, Boolean>>>();

    /**
     * Default constructor for ListUtil.
     */
    public NarList() {}

    /**
     * Constructor for NarList with parameters to initialize the title, description, file name,
     * isMap flag, and a save callback.
     * 
     * @param title The title of the list configuration.
     * @param description The description of the list configuration.
     * @param fileName The name of the file where the configuration will be saved.
     * @param isMap Flag indicating if the configuration is a map-like structure.
     * @param saveCallback A callback function to be executed when the configuration is saved.
     */
    public NarList(String title, Text description, String fileName, boolean isMap,
            Runnable saveCallback) {
        this.title = title;
        this.description = description;
        this.fileName = fileName;
        this.isMap = isMap;
        this.saveCallback = saveCallback;

        // Create handler for this ListUtil instance
        this.handler = ConfigClassHandler.createBuilder(NarList.class)
                .serializer(config -> GsonConfigSerializerBuilder.create(config)
                        .setPath(CONFIG_PATH.resolve(fileName))
                        .appendGsonBuilder(gsonBuilder -> gsonBuilder.setPrettyPrinting()
                                .disableHtmlEscaping().serializeNulls())
                        .build())
                .build();

        loadDefault();
        MinecraftClient.getInstance().send(() -> {
            handler.load();
            onSave(handler.instance());
        });
    }

    /**
     * Constructor for NarList with parameters to initialize the title, description, file name, and
     * a save callback, without the isMap flag.
     * 
     * @param title The title of the list configuration.
     * @param description The description of the list configuration.
     * @param fileName The name of the file where the configuration will be saved.
     * @param saveCallback A callback function to be executed when the configuration is saved.
     */
    public NarList(String title, Text description, String fileName, Runnable saveCallback) {
        this(title, description, fileName, false, saveCallback);
    }

    /**
     * Constructor for NarList with parameters to initialize the title, description, and file name,
     * without the isMap flag and save callback.
     * 
     * @param title The title of the list configuration.
     * @param description The description of the list configuration.
     * @param fileName The name of the file where the configuration will be saved.
     */
    public NarList(String title, Text description, String fileName) {
        this(title, description, fileName, false, null);
    }

    /**
     * Constructor for NarList with parameters to initialize the title, description, file name, and
     * isMap flag, without a save callback.
     * 
     * @param title The title of the list configuration.
     * @param description The description of the list configuration.
     * @param fileName The name of the file where the configuration will be saved.
     * @param isMap Flag indicating if the configuration is a map-like structure.
     */
    public NarList(String title, Text description, String fileName, boolean isMap) {
        this(title, description, fileName, isMap, null);
    }

    /**
     * Gets the handler for this ListUtil instance.
     * 
     * @return The ConfigClassHandler for this ListUtil instance.
     */
    public NarList getHandler() {
        return handler.instance();
    }

    /**
     * Creates default configuration values for the ListUtil instance.
     */
    public void loadDefault() {
        Path configPath = CONFIG_PATH.resolve(fileName);
        if (Files.exists(configPath)) {
            return;
        }

        // Load the template file from resources
        try (InputStream templateStream =
                NarJson.class.getResourceAsStream("/json/lists/" + fileName)) {
            if (templateStream == null) {
                return;
            }

            // Copy the template file to the configuration path
            Files.copy(templateStream, configPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load template for list config: " + fileName, e);
        }
    }

    /**
     * Resets the configuration by deleting the configuration file and recreating the defaults.
     */
    public void reset() {
        // Delete the configuration file
        try {
            Files.deleteIfExists(CONFIG_PATH.resolve(fileName));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete list config file: " + fileName, e);
        }

        // Recreate the defaults
        loadDefault();
    }

    /**
     * Creates a configuration screen for the ListUtil instance.
     * 
     * @param parent The parent screen to attach the configuration screen to.
     * @return A new configuration screen for the ListUtil instance.
     */
    public Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.create(handler, (defaults, config, builder) -> {
            builder.title(Text.literal(title)).category(isMap ? createMapCategory(defaults, config)
                    : createListCategory(defaults, config)).save(() -> {
                        onSave(config);
                    }).build();
            return builder;
        }).generateScreen(parent);
    }

    /**
     * Handles the saving of the configuration.
     * 
     * @param config The configuration object containing the current settings.
     */
    private void onSave(NarList config) {
        // Update the static lists based on the configuration
        list.clear();
        for (KeyValuePair<String, Boolean> pair : config.listConfig) {
            if (pair.getValue()) {
                list.add(pair.getKey());
            }
        }

        map.clear();
        for (KeyValuePair<String, KeyValuePair<String, Boolean>> pair : config.mapConfig) {
            if (pair.getValue().getValue()) {
                map.put(pair.getKey(), pair.getValue().getKey());
            }
        }

        // Call save callback if it is set
        if (saveCallback != null) {
            saveCallback.run();
        }

        handler.save();
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
            context.getSource().sendFeedback(NotARat.MOD_TITLE.copy()
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
    public ConfigCategory createListCategory(NarList defaults, NarList config) {
        return ConfigCategory.createBuilder().name(Text.literal(title))
                .option(ListOption.<KeyValuePair<String, Boolean>>createBuilder()
                        .name(Text.literal(title))
                        .description(OptionDescription.createBuilder().text(description).build())
                        .binding(config.listConfig, () -> config.listConfig,
                                newVal -> config.listConfig = newVal)
                        .controller((option) -> KeyValueController.Builder.create(option)
                                .keyController("Key", StringControllerBuilder::create)
                                .valueController("Enabled", TickBoxControllerBuilder::create))
                        .initial(new KeyValuePair<>("", true)).build())
                .build();
    }

    /**
     * Creates a configuration category for a map-like structure in the ListUtil instance.
     * 
     * @param defaults The default configuration values for the map.
     * @param config The current configuration values for the map.
     * @return A ConfigCategory instance representing the map configuration category.
     */
    public ConfigCategory createMapCategory(NarList defaults, NarList config) {
        return ConfigCategory.createBuilder().name(Text.literal(title)).option(ListOption
                .<KeyValuePair<String, KeyValuePair<String, Boolean>>>createBuilder()
                .name(Text.literal(title))
                .description(OptionDescription.createBuilder().text(description).build())
                .binding(config.mapConfig, () -> config.mapConfig,
                        newVal -> config.mapConfig = newVal)
                .controller((option) -> KeyValueController.Builder.create(option)
                        .keyController("Key", StringControllerBuilder::create).valueController(null,
                                (subOption) -> KeyValueController.Builder.create(subOption)
                                        .keyController("Value", StringControllerBuilder::create)
                                        .valueController("Enabled",
                                                TickBoxControllerBuilder::create)))
                .initial(new KeyValuePair<>("", new KeyValuePair<>("", true))).build()).build();
    }
}
