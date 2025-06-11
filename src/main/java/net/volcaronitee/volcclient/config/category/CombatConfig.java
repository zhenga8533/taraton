package net.volcaronitee.volcclient.config.category;

import java.awt.Color;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.VolcClientConfig;
import net.volcaronitee.volcclient.config.VolcClientConfig.AnnounceWP;

public class CombatConfig {
    public static ConfigCategory create(VolcClientConfig defaults, VolcClientConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("Combat"))

                // Bestiary Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Bestiary"))

                        // Bestiary Counter
                        .option(Option.<BestiaryCounter>createBuilder()
                                .name(Text.literal("Bestiary Counter"))
                                .description(OptionDescription.of(Text.literal("")))
                                .binding(defaults.combat.bestiaryCounter,
                                        () -> config.combat.bestiaryCounter,
                                        newVal -> config.combat.bestiaryCounter = newVal)
                                .controller(VolcClientConfig::createEnumController).build())

                        // Bestiary Menu
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Bestiary Menu"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays bestiary level as stack size and highlight uncompleted bestiary milestones.")))
                                .binding(defaults.combat.bestiaryMenu,
                                        () -> config.combat.bestiaryMenu,
                                        newVal -> config.combat.bestiaryMenu = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Hitbox Color
                        .option(Option.<Color>createBuilder().name(Text.literal("Hitbox Color"))
                                .description(OptionDescription.of(Text.literal(
                                        "Sets the seed seed used for the color of entity hitboxes.")))
                                .binding(defaults.combat.hitboxColor,
                                        () -> config.combat.hitboxColor,
                                        newVal -> config.combat.hitboxColor = newVal)
                                .controller(
                                        opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                .build())

                        // Kill Tracker
                        .option(Option.<Integer>createBuilder().name(Text.literal("Kill Tracker"))
                                .description(OptionDescription.of(Text.literal(
                                        "Tracks kills progress on the screen. Sets time in minutes of inactivity before tracking stops. Uses the bestiary widget to track kills.")))
                                .binding(defaults.combat.killTracker,
                                        () -> config.combat.killTracker,
                                        newVal -> config.combat.killTracker = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 10).step(1))
                                .build())

                        .build())

                // Combat Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Combat"))

                        // Combo Display
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Combo Display"))
                                .description(OptionDescription.of(Text.literal(
                                        "Removes grandma wolf combo chat messages and displays a combo overlay on the screen.")))
                                .binding(defaults.combat.comboDisplay,
                                        () -> config.combat.comboDisplay,
                                        newVal -> config.combat.comboDisplay = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Damage Tracer
                        .option(Option.<DamageTracer>createBuilder()
                                .name(Text.literal("Damage Tracer"))
                                .description(OptionDescription.of(Text.literal(
                                        "Spams chat with unique damage ticks. This is meant to be used on a training dummy. Optionally provides simple statistics on damage dealt.")))
                                .binding(defaults.combat.damageTracer,
                                        () -> config.combat.damageTracer,
                                        newVal -> config.combat.damageTracer = newVal)
                                .controller(VolcClientConfig::createEnumController).build())

                        // Low Health Warning
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Low Health Warning"))
                                .description(OptionDescription.of(Text.literal(
                                        "Sets the health percentage at which a low health warning title is displayed.")))
                                .binding(defaults.combat.lowHealthWarning,
                                        () -> config.combat.lowHealthWarning,
                                        newVal -> config.combat.lowHealthWarning = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100).step(1))
                                .build())

                        // Mana Drain Range
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Mana Drain Range"))
                                .description(OptionDescription.of(Text.literal(
                                        "Highlights nearby players when a typical mana drain item is held. Displays the number of players in range as a title on the screen.")))
                                .binding(defaults.combat.manaDrainRange,
                                        () -> config.combat.manaDrainRange,
                                        newVal -> config.combat.manaDrainRange = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Ragnarok Detection
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Ragnarok Detection"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays an alert title when Ragnarok Axe finishes casting or is cancelled.")))
                                .binding(defaults.combat.ragnarokDetection,
                                        () -> config.combat.ragnarokDetection,
                                        newVal -> config.combat.ragnarokDetection = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        .build())

                // Slayer Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Slayer"))

                        // Slayer Boss Announce
                        .option(Option.<AnnounceWP>createBuilder()
                                .name(Text.literal("Slayer Boss Announce"))
                                .description(OptionDescription.of(
                                        Text.literal("Sends a chat message on slayer boss spawn.")))
                                .binding(defaults.combat.slayerBossAnnounce,
                                        () -> config.combat.slayerBossAnnounce,
                                        newVal -> config.combat.slayerBossAnnounce = newVal)
                                .controller(VolcClientConfig::createEnumController).build())

                        // Slayer Boss Highlight
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Slayer Boss Highlight"))
                                .description(OptionDescription.of(Text.literal(
                                        "Highlights nearby slayer bosses. This includes bosses spawned by other players.")))
                                .binding(defaults.combat.slayerBossHighlight,
                                        () -> config.combat.slayerBossHighlight,
                                        newVal -> config.combat.slayerBossHighlight = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Slayer Miniboss Announce
                        .option(Option.<AnnounceWP>createBuilder()
                                .name(Text.literal("Slayer Miniboss Announce"))
                                .description(OptionDescription.of(Text
                                        .literal("Sends a chat message on slayer miniboss spawn.")))
                                .binding(defaults.combat.slayerMinibossAnnounce,
                                        () -> config.combat.slayerMinibossAnnounce,
                                        newVal -> config.combat.slayerMinibossAnnounce = newVal)
                                .controller(VolcClientConfig::createEnumController).build())

                        // Slayer Miniboss Highlight
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Slayer Miniboss Highlight"))
                                .description(OptionDescription.of(Text.literal(
                                        "Highlights nearby slayer minibosses. This includes minibosses spawned by other players.")))
                                .binding(defaults.combat.slayerMinibossHighlight,
                                        () -> config.combat.slayerMinibossHighlight,
                                        newVal -> config.combat.slayerMinibossHighlight = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Slayer Spawn Warning
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Slayer Spawn Warning"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays a warning title when a slayer boss is about to spawn. Sets the slayer quest completion percentage at which the warning is displayed.")))
                                .binding(defaults.combat.slayerSpawnWarning,
                                        () -> config.combat.slayerSpawnWarning,
                                        newVal -> config.combat.slayerSpawnWarning = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100).step(1))
                                .build())

                        .build())

                .build();
    }

    // Bestiary Option Group
    @SerialEntry
    public BestiaryCounter bestiaryCounter = BestiaryCounter.OFF;

    public enum BestiaryCounter implements NameableEnum {
        OFF, CUMULATIVE, WORLD;

        @Override
        public Text getDisplayName() {
            return switch (this) {
                case OFF -> Text.literal("Disabled");
                case CUMULATIVE -> Text.literal("Cumulative");
                case WORLD -> Text.literal("World");
            };
        }
    }

    @SerialEntry
    public boolean bestiaryMenu = false;

    @SerialEntry
    public Color hitboxColor = new Color(255, 255, 255, 255);

    @SerialEntry
    public int killTracker = 0;

    // Combat Option Group
    @SerialEntry
    public boolean comboDisplay = false;

    @SerialEntry
    public DamageTracer damageTracer = DamageTracer.OFF;

    public enum DamageTracer implements NameableEnum {
        OFF, SIMPLE, ANALYTIC;

        @Override
        public Text getDisplayName() {
            return switch (this) {
                case OFF -> Text.literal("Disabled");
                case SIMPLE -> Text.literal("Simple");
                case ANALYTIC -> Text.literal("Analytic");
            };
        }
    }

    @SerialEntry
    public int lowHealthWarning = 0;

    @SerialEntry
    public boolean manaDrainRange = false;

    @SerialEntry
    public boolean ragnarokDetection = false;

    // Slayer Option Group
    @SerialEntry
    public AnnounceWP slayerBossAnnounce = AnnounceWP.OFF;

    @SerialEntry
    public boolean slayerBossHighlight = false;

    @SerialEntry
    public AnnounceWP slayerMinibossAnnounce = AnnounceWP.OFF;

    @SerialEntry
    public boolean slayerMinibossHighlight = false;

    @SerialEntry
    public int slayerSpawnWarning = 0;
}
