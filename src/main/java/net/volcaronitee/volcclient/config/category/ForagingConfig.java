package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.ConfigUtil;

public class ForagingConfig {
    public static ConfigCategory create(ConfigUtil defaults, ConfigUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal("Foraging"))

                // Option Group
                .group(OptionGroup.createBuilder().name(Text.literal(""))

                        // Option
                        .option(Option.<Boolean>createBuilder().name(Text.literal(""))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal("")).build())
                                .binding(defaults.foraging.temp, () -> config.foraging.temp,
                                        newVal -> config.foraging.temp = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                .build();
    }

    // Option Group
    @SerialEntry
    public boolean temp = false;
}
