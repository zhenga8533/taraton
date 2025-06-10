package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.VolcClientConfig;

public class RiftConfig {
    public static ConfigCategory create(VolcClientConfig defaults, VolcClientConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("Rift"))

                // Option Group
                .group(OptionGroup.createBuilder().name(Text.literal(""))

                        // Option
                        .option(Option.<Boolean>createBuilder().name(Text.literal(""))
                                .description(OptionDescription.of(Text.literal("")))
                                .binding(defaults.rift.temp, () -> config.rift.temp,
                                        newVal -> config.rift.temp = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        .build())

                .build();
    }

    // Option Group
    @SerialEntry
    public boolean temp = false;
}
