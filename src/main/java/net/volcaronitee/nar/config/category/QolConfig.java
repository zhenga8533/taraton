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
 * Configuration class for Quality of Life (QOL) features in NotARat.
 */
public class QolConfig {
    /**
     * Creates a new {@link ConfigCategory} for the QOL features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the QOL features.
     */
    public static ConfigCategory create(NarConfig defaults, NarConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("QOL"))

                // Visibility Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Visibility"))

                        // Hide Potion Effects
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Hide Potion Effects"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/qol/hide_potion_effects.webp"))
                                        .text(Text.literal(
                                                "Hides potion effects from the HUD and inventory."))
                                        .build())
                                .binding(defaults.qol.hidePotionEffects,
                                        () -> config.qol.hidePotionEffects,
                                        newVal -> config.qol.hidePotionEffects = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        .build())

                .build();
    }

    // Visibility Option Group
    @SerialEntry
    public boolean hidePotionEffects = false;
}
