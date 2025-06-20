package net.volcaronitee.volcclient.util;

import java.nio.file.Path;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.VolcClient;
import net.volcaronitee.volcclient.config.category.ChatConfig;
import net.volcaronitee.volcclient.config.category.CombatConfig;
import net.volcaronitee.volcclient.config.category.ContainerConfig;
import net.volcaronitee.volcclient.config.category.CrimsonIslesConfig;
import net.volcaronitee.volcclient.config.category.DungeonsConfig;
import net.volcaronitee.volcclient.config.category.EconomyConfig;
import net.volcaronitee.volcclient.config.category.EventsConfig;
import net.volcaronitee.volcclient.config.category.FarmingConfig;
import net.volcaronitee.volcclient.config.category.ForagingConfig;
import net.volcaronitee.volcclient.config.category.GeneralConfig;
import net.volcaronitee.volcclient.config.category.KuudraConfig;
import net.volcaronitee.volcclient.config.category.MiningConfig;
import net.volcaronitee.volcclient.config.category.RiftConfig;
import net.volcaronitee.volcclient.config.category.TemplateConfig;

/**
 * Utility class for handling configuration settings.
 */
public class ConfigUtil {
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve(VolcClient.MOD_ID + "/config.json");
    private static final ConfigClassHandler<ConfigUtil> HANDLER =
            ConfigClassHandler.createBuilder(ConfigUtil.class)
                    .serializer(config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(CONFIG_PATH).appendGsonBuilder(gsonBuilder -> gsonBuilder
                                    .setPrettyPrinting().disableHtmlEscaping().serializeNulls())
                            .build())
                    .build();

    /**
     * Initializes the configuration handler for Volc Client.
     */
    public static void init() {
        HANDLER.load();
    }

    /**
     * Gets the configuration handler for Volc Client.
     * 
     * @return The ConfigClassHandler for Volc Client.
     */
    public static ConfigUtil getHandler() {
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
                    .category(GeneralConfig.create(defaults, config))
                    .category(ChatConfig.create(defaults, config))
                    .category(ContainerConfig.create(defaults, config))
                    .category(EconomyConfig.create(defaults, config))
                    .category(CombatConfig.create(defaults, config))
                    .category(MiningConfig.create(defaults, config))
                    .category(FarmingConfig.create(defaults, config))
                    .category(ForagingConfig.create(defaults, config))
                    .category(EventsConfig.create(defaults, config))
                    .category(CrimsonIslesConfig.create(defaults, config))
                    .category(DungeonsConfig.create(defaults, config))
                    .category(KuudraConfig.create(defaults, config))
                    .category(RiftConfig.create(defaults, config));

            return builder;
        }).generateScreen(parent);
    }

    /**
     * Creates a boolean controller for a given option.
     * 
     * @param opt The option to create a boolean controller for.
     * @return A BooleanControllerBuilder for the specified option.
     */
    public static BooleanControllerBuilder createBooleanController(Option<Boolean> opt) {
        return BooleanControllerBuilder.create(opt).yesNoFormatter().coloured(true);
    }

    /**
     * Creates a boolean controller for a given option with custom true and false strings.
     * 
     * @param opt The option to create a boolean controller for.
     * @param trueString The string to display when the option is true.
     * @param falseString The string to display when the option is false.
     * @return A BooleanControllerBuilder for the specified option with custom strings.
     */
    public static BooleanControllerBuilder createBooleanController(Option<Boolean> opt,
            String trueString, String falseString) {
        return BooleanControllerBuilder.create(opt)
                .formatValue(val -> val ? Text.literal(trueString) : Text.literal(falseString))
                .coloured(true);
    }

    /**
     * Creates an enum controller for a given option.
     * 
     * @param <E> The type of the enum.
     * @param opt The option to create an enum controller for.
     * @return An EnumControllerBuilder for the specified option.
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> EnumControllerBuilder<E> createEnumController(Option<E> opt) {
        return EnumControllerBuilder.create(opt)
                .enumClass((Class<E>) opt.stateManager().get().getClass());
    }

    // Configuration Categories
    @SerialEntry
    public TemplateConfig template = new TemplateConfig();

    @SerialEntry
    public GeneralConfig general = new GeneralConfig();

    @SerialEntry
    public ChatConfig chat = new ChatConfig();

    @SerialEntry
    public ContainerConfig container = new ContainerConfig();

    @SerialEntry
    public EconomyConfig economy = new EconomyConfig();

    @SerialEntry
    public CombatConfig combat = new CombatConfig();

    @SerialEntry
    public MiningConfig mining = new MiningConfig();

    @SerialEntry
    public FarmingConfig farming = new FarmingConfig();

    @SerialEntry
    public ForagingConfig foraging = new ForagingConfig();

    @SerialEntry
    public EventsConfig events = new EventsConfig();

    @SerialEntry
    public CrimsonIslesConfig crimsonIsles = new CrimsonIslesConfig();

    @SerialEntry
    public DungeonsConfig dungeons = new DungeonsConfig();

    @SerialEntry
    public KuudraConfig kuudra = new KuudraConfig();

    @SerialEntry
    public RiftConfig rift = new RiftConfig();

    // Shared enums
    public enum AnnounceWP implements NameableEnum {
        OFF, ALL, PARTY, PRIVATE;

        @Override
        public Text getDisplayName() {
            return switch (this) {
                case OFF -> Text.literal("Disabled");
                case ALL -> Text.literal("All Chat");
                case PARTY -> Text.literal("Party Chat");
                case PRIVATE -> Text.literal("Private Chat");
            };
        }
    }
}
