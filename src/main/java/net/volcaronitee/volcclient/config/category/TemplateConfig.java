package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.VolcClientConfig;

public class TemplateConfig {
    public static ConfigCategory create(VolcClientConfig defaults, VolcClientConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("Category"))

                // Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Group"))

                        // Option
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Option"))
                                .description(OptionDescription.of(Text.literal("Description")))
                                .binding(defaults.template.temp, () -> config.template.temp,
                                        newVal -> config.template.temp = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        .build())

                .build();
    }

    // Option Group
    @SerialEntry
    public boolean temp = false;
}
