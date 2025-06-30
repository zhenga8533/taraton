package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.volcclient.VolcClient;
import net.volcaronitee.volcclient.util.ConfigUtil;

/**
 * Configuration for the Combat features in VolcClient.
 */
public class CombatConfig {
    /**
     * Creates a new {@link ConfigCategory} for the Combat features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the Combat features.
     */
    public static ConfigCategory create(ConfigUtil defaults, ConfigUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal("Combat"))

                // Bestiary Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Bestiary"))

                        // TODO: Bestiary Counter
                        .option(Option.<BestiaryCounter>createBuilder()
                                .name(Text.literal("Bestiary Counter"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/combat/bestiary_counter.webp"))
                                        .text(Text.literal(
                                                "Displays total bestiary kill count. Uses tab widgets to track kills."))
                                        .build())
                                .binding(defaults.combat.bestiaryCounter,
                                        () -> config.combat.bestiaryCounter,
                                        newVal -> config.combat.bestiaryCounter = newVal)
                                .controller(ConfigUtil::createEnumController).build())

                        // TODO: Bestiary Menu
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Bestiary Menu"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/combat/bestiary_menu.webp"))
                                        .text(Text.literal(
                                                "Displays bestiary level as stack size and highlight uncompleted bestiary milestones."))
                                        .build())
                                .binding(defaults.combat.bestiaryMenu,
                                        () -> config.combat.bestiaryMenu,
                                        newVal -> config.combat.bestiaryMenu = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Entity Counter
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Entity Counter"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/combat/entity_counter.webp"))
                                        .text(Text.literal(
                                                "Displays the number of entities highlighted in the world based on the entity list."))
                                        .build())
                                .binding(defaults.combat.entityCounter,
                                        () -> config.combat.entityCounter,
                                        newVal -> config.combat.entityCounter = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Entity Highlight
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Entity Highlight"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/combat/entity_highlight.webp"))
                                        .text(Text.literal(
                                                "Highlights entities in the world based on the entity list."))
                                        .build())
                                .binding(defaults.combat.entityHighlight,
                                        () -> config.combat.entityHighlight,
                                        newVal -> config.combat.entityHighlight = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Kill Tracker
                        .option(Option.<Integer>createBuilder().name(Text.literal("Kill Tracker"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/combat/kill_tracker.webp"))
                                        .text(Text.literal(
                                                "Tracks kills progress on the screen. Sets time in minutes of inactivity before tracking stops. Uses the bestiary widget to track kills."))
                                        .build())
                                .binding(defaults.combat.killTracker,
                                        () -> config.combat.killTracker,
                                        newVal -> config.combat.killTracker = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 10).step(1))
                                .build())

                        .build())

                // Combat Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Combat"))

                        // TODO: Combo Display
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Combo Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/combat/combo_display.webp"))
                                        .text(Text.literal(
                                                "Removes grandma wolf combo chat messages and displays a combo overlay on the screen."))
                                        .build())
                                .binding(defaults.combat.comboDisplay,
                                        () -> config.combat.comboDisplay,
                                        newVal -> config.combat.comboDisplay = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Damage Tracer
                        .option(Option.<DamageTracer>createBuilder()
                                .name(Text.literal("Damage Tracer"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/combat/damage_tracer.webp"))
                                        .text(Text.literal(
                                                "Spams chat with unique damage ticks. This is meant to be used on a training dummy. Optionally provides simple statistics on damage dealt."))
                                        .build())
                                .binding(defaults.combat.damageTracer,
                                        () -> config.combat.damageTracer,
                                        newVal -> config.combat.damageTracer = newVal)
                                .controller(ConfigUtil::createEnumController).build())

                        // TODO: Low Health Warning
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Low Health Warning"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/combat/low_health_warning.webp"))
                                        .text(Text.literal(
                                                "Sets the health percentage at which a low health warning title is displayed."))
                                        .build())
                                .binding(defaults.combat.lowHealthWarning,
                                        () -> config.combat.lowHealthWarning,
                                        newVal -> config.combat.lowHealthWarning = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100).step(5))
                                .build())

                        // TODO: Mana Drain Range
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Mana Drain Range"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/combat/mana_drain_range.webp"))
                                        .text(Text.literal(
                                                "Highlights nearby players when a typical mana drain item is held. Displays the number of players in range as a title on the screen."))
                                        .build())
                                .binding(defaults.combat.manaDrainRange,
                                        () -> config.combat.manaDrainRange,
                                        newVal -> config.combat.manaDrainRange = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Ragnarok Detection
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Ragnarok Detection"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/combat/ragnarok_detection.webp"))
                                        .text(Text.literal(
                                                "Displays an alert title when Ragnarok Axe finishes casting or is cancelled."))
                                        .build())
                                .binding(defaults.combat.ragnarokDetection,
                                        () -> config.combat.ragnarokDetection,
                                        newVal -> config.combat.ragnarokDetection = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                // Slayer Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Slayer"))

                        // TODO: Slayer Spawn Warning
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Slayer Spawn Warning"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/combat/slayer_spawn_warning.webp"))
                                        .text(Text.literal(
                                                "Displays a warning title when a slayer boss is about to spawn. Sets the slayer quest completion percentage at which the warning is displayed."))
                                        .build())
                                .binding(defaults.combat.slayerSpawnWarning,
                                        () -> config.combat.slayerSpawnWarning,
                                        newVal -> config.combat.slayerSpawnWarning = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100).step(5))
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
    public boolean bestiaryMenu = true;

    @SerialEntry
    public boolean entityCounter = false;

    @SerialEntry
    public boolean entityHighlight = true;

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
    public boolean ragnarokDetection = true;

    // Slayer Option Group
    @SerialEntry
    public int slayerSpawnWarning = 0;
}
