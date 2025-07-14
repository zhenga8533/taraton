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
 * Configuration for the Foraging features in Taraton.
 */
public class ForagingConfig {
    /**
     * Creates a new {@link ConfigCategory} for the Foraging features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the Foraging features.
     */
    public static ConfigCategory create(TaratonConfig defaults, TaratonConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("Foraging"))

                // Option Group
                .group(OptionGroup.createBuilder().name(Text.literal(""))

                        // TODO: Option
                        .option(Option.<Boolean>createBuilder().name(Text.literal(""))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/template/placeholder.webp"))
                                        .text(Text.literal("")).build())
                                .binding(defaults.foraging.temp, () -> config.foraging.temp,
                                        newVal -> config.foraging.temp = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        .build())

                .build();
    }

    // Option Group
    @SerialEntry
    public boolean temp = false;
}
