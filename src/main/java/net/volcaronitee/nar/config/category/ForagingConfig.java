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

/**
 * Configuration for the Foraging features in NotARat.
 */
public class ForagingConfig {
    /**
     * Creates a new {@link ConfigCategory} for the Foraging features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the Foraging features.
     */
    public static ConfigCategory create(NarConfig defaults, NarConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("Foraging"))

                // Option Group
                .group(OptionGroup.createBuilder().name(Text.literal(""))

                        // TODO: Option
                        .option(Option.<Boolean>createBuilder().name(Text.literal(""))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/template/placeholder.webp"))
                                        .text(Text.literal("")).build())
                                .binding(defaults.foraging.temp, () -> config.foraging.temp,
                                        newVal -> config.foraging.temp = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        .build())

                .build();
    }

    // Option Group
    @SerialEntry
    public boolean temp = false;
}
