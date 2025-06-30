package net.volcaronitee.nar.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.util.ConfigUtil;

public class FishingConfig {
    /**
     * Creates a new {@link ConfigCategory} for the Foraging features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the Foraging features.
     */
    public static ConfigCategory create(ConfigUtil defaults, ConfigUtil config) {
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
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                .build();
    }

    // Fishing Option Group
    @SerialEntry
    public boolean hookLineAndSinker = false;
}
