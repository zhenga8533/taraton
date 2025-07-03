package net.volcaronitee.nar.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.config.NarConfig;

public class FishingConfig {
    /**
     * Creates a new {@link ConfigCategory} for the Foraging features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the Foraging features.
     */
    public static ConfigCategory create(NarConfig defaults, NarConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("Fishing"))

                // Fishing Option Group
                .group(OptionGroup.createBuilder().name(Text.literal(""))

                        // Hook Line and Sinker
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Hook, Line, and Sinker"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/fishing/hook_line_and_sinker.webp"))
                                        .text(Text.literal("Hehehe.")).build())
                                .binding(defaults.fishing.hookLineAndSinker,
                                        () -> config.fishing.hookLineAndSinker,
                                        newVal -> config.fishing.hookLineAndSinker = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Hotspot Locator
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Hotspot Locator"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/fishing/hotspot_locator.webp"))
                                        .text(Text.literal("Finds nearby fishing hotspots."))
                                        .build())
                                .binding(defaults.fishing.hotspotLocator,
                                        () -> config.fishing.hotspotLocator,
                                        newVal -> config.fishing.hotspotLocator = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        .build())

                .build();
    }

    // Fishing Option Group
    @SerialEntry
    public boolean hookLineAndSinker = false;

    @SerialEntry
    public boolean hotspotLocator = false;
}
