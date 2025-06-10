package net.volcaronitee.volcclient.config;

import java.nio.file.Path;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.category.ChatConfig;
import net.volcaronitee.volcclient.config.category.CombatConfig;
import net.volcaronitee.volcclient.config.category.ContainerConfig;
import net.volcaronitee.volcclient.config.category.CrimsonIslesConfig;
import net.volcaronitee.volcclient.config.category.DungeonConfig;
import net.volcaronitee.volcclient.config.category.EconomyConfig;
import net.volcaronitee.volcclient.config.category.EventConfig;
import net.volcaronitee.volcclient.config.category.FarmingConfig;
import net.volcaronitee.volcclient.config.category.ForagingConfig;
import net.volcaronitee.volcclient.config.category.GeneralConfig;
import net.volcaronitee.volcclient.config.category.KuudraConfig;
import net.volcaronitee.volcclient.config.category.MiningConfig;
import net.volcaronitee.volcclient.config.category.RiftConfig;

public class VolcClientConfig {
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("volcclient/config.json");
    private static final ConfigClassHandler<VolcClientConfig> HANDLER =
            ConfigClassHandler.createBuilder(VolcClientConfig.class)
                    .serializer(config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(CONFIG_PATH).appendGsonBuilder(gsonBuilder -> gsonBuilder
                                    .setPrettyPrinting().disableHtmlEscaping().serializeNulls())
                            .build())
                    .build();

    public static void init() {
        HANDLER.load();
    }

    public static Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, (defaults, config, builder) -> {
            builder.title(Text.literal("Volc Client Config"))
                    .category(GeneralConfig.create(defaults, config))
                    .category(ChatConfig.create(defaults, config));

            return builder;
        }).generateScreen(parent);
    }

    public static BooleanControllerBuilder createBooleanController(Option<Boolean> opt) {
        return BooleanControllerBuilder.create(opt).yesNoFormatter().coloured(true);
    }

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
    public EventConfig event = new EventConfig();

    @SerialEntry
    public CrimsonIslesConfig crimsonIsles = new CrimsonIslesConfig();

    @SerialEntry
    public DungeonConfig dungeon = new DungeonConfig();

    @SerialEntry
    public KuudraConfig kuudra = new KuudraConfig();

    @SerialEntry
    public RiftConfig rift = new RiftConfig();
}
