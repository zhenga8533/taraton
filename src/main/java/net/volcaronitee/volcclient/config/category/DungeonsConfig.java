package net.volcaronitee.volcclient.config.category;

import java.awt.Color;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.VolcClientConfig;

public class DungeonsConfig {
    public static ConfigCategory create(VolcClientConfig defaults, VolcClientConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("Dungeons"))

                // Chests Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Chests"))

                        // Croseus Highlight
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Croseus Highlight"))
                                .description(OptionDescription.of(Text.literal(
                                        "Highlights unopened dungeon chests in Croseus menu.")))
                                .binding(defaults.dungeons.croseusHighlight,
                                        () -> config.dungeons.croseusHighlight,
                                        newVal -> config.dungeons.croseusHighlight = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Dungeon Profit
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Dungeon Profit"))
                                .description(OptionDescription.of(Text.literal(
                                        "Shows profit of any opened dungeon chest on the screen.")))
                                .binding(defaults.dungeons.dungeonProfit,
                                        () -> config.dungeons.dungeonProfit,
                                        newVal -> config.dungeons.dungeonProfit = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        .build())

                // Star Detection Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Star Detection"))

                        // Star Mob Highlight
                        .option(Option.<StarMobHighlight>createBuilder()
                                .name(Text.literal("Star Mob Highlight"))
                                .description(OptionDescription.of(
                                        Text.literal("Highlights nearby star mobs in dungeons.")))
                                .binding(defaults.dungeons.starMobHighlight,
                                        () -> config.dungeons.starMobHighlight,
                                        newVal -> config.dungeons.starMobHighlight = newVal)
                                .controller(VolcClientConfig::createEnumController).build())

                        // Star Highlight Color
                        .option(Option.<Color>createBuilder()
                                .name(Text.literal("Star Highlight Color"))
                                .description(OptionDescription
                                        .of(Text.literal("Color of the star mob highlight.")))
                                .binding(defaults.dungeons.starHighligColor,
                                        () -> config.dungeons.starHighligColor,
                                        newVal -> config.dungeons.starHighligColor = newVal)
                                .controller(
                                        opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                .build())

                        .build())

                .build();
    }

    // Chests Option Group
    @SerialEntry
    public boolean croseusHighlight = false;

    @SerialEntry
    public boolean dungeonProfit = false;

    // Star Detection Option Group
    @SerialEntry
    public StarMobHighlight starMobHighlight = StarMobHighlight.OFF;

    public enum StarMobHighlight implements NameableEnum {
        OFF, HIGHLIGHT, BOX, OUTLINE;

        @Override
        public Text getDisplayName() {
            return switch (this) {
                case OFF -> Text.literal("Disabled");
                case HIGHLIGHT -> Text.literal("Highlight");
                case BOX -> Text.literal("Box");
                case OUTLINE -> Text.literal("Outline");
            };
        }
    }

    @SerialEntry
    public Color starHighligColor = new Color(255, 0, 0, 255);
}
