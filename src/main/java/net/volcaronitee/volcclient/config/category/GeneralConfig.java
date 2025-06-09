package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.VolcClientConfig;

public class GeneralConfig {
  public static ConfigCategory create(VolcClientConfig defaults, VolcClientConfig config) {
    return ConfigCategory.createBuilder()
        .name(Text.literal("General"))

        // Mod Enabled
        .option(Option.<Boolean>createBuilder()
            .name(Text.literal("Mod Enabled"))
            .description(OptionDescription.of(Text.translatable("Enable or disable the mod.")))
            .binding(
                defaults.general.modEnabled,
                () -> config.general.modEnabled,
                newVal -> config.general.modEnabled = newVal)
            .controller(VolcClientConfig::createBooleanController)
            .build())

        .build();
  }

  @SerialEntry
  public boolean modEnabled = true;
}
