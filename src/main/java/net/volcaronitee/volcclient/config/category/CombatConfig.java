package net.volcaronitee.volcclient.config.category;

import java.awt.Color;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.VolcClientConfig;

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
    public boolean manaDrainRanger = false;

    @SerialEntry
    public boolean ragnarokDetection = false;

    // Slayer Option Group
    @SerialEntry
    public AnnounceSlayer announceSlayerBoss = AnnounceSlayer.OFF;

    @SerialEntry
    public AnnounceSlayer announceSlayerMiniboss = AnnounceSlayer.OFF;

    public enum AnnounceSlayer implements NameableEnum {
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

    @SerialEntry
    public boolean slayerBossHighlight = false;

    @SerialEntry
    public boolean slayerMinibossHighlight = false;

    @SerialEntry
    public int slayerSpawnWarning = 0;
}
