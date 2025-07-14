package net.volcaronitee.taraton.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonConfig;

/**
 * Configuration for the Dungeons features in Taraton.
 */
public class DungeonsConfig {
    /**
     * Creates a new {@link ConfigCategory} for the Dungeons features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the Dungeons features.
     */
    public static ConfigCategory create(TaratonConfig defaults, TaratonConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("Dungeons"))

                // Chests Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Chests"))

                        // TODO: Croseus Highlight
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Croseus Highlight"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/dungeons/croseus_highlight.webp"))
                                        .text(Text.literal(
                                                "Highlights unopened dungeon chests in Croseus menu."))
                                        .build())
                                .binding(defaults.dungeons.croseusHighlight,
                                        () -> config.dungeons.croseusHighlight,
                                        newVal -> config.dungeons.croseusHighlight = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        // TODO: Dungeon Profit
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Dungeon Profit"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/dungeons/dungeon_profit.webp"))
                                        .text(Text.literal(
                                                "Displays profit of any opened dungeon chest on the screen."))
                                        .build())
                                .binding(defaults.dungeons.dungeonProfit,
                                        () -> config.dungeons.dungeonProfit,
                                        newVal -> config.dungeons.dungeonProfit = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        .build())

                .build();
    }

    // Chests Option Group
    @SerialEntry
    public boolean croseusHighlight = true;

    @SerialEntry
    public boolean dungeonProfit = false;
}
