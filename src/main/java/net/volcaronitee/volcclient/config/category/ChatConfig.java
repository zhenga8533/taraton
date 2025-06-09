package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.VolcClientConfig;

public class ChatConfig {
  public static ConfigCategory create(VolcClientConfig defaults, VolcClientConfig config) {
    return ConfigCategory.createBuilder()
        .name(Text.literal("Chat"))

        // Party Commands
        .option(Option.<Boolean>createBuilder()
            .name(Text.literal("Party Commands"))
            .description(OptionDescription.of(Text.literal("Enable or disable party commands.")))
            .binding(
                defaults.chat.partyCommands,
                () -> config.chat.partyCommands,
                newVal -> config.chat.partyCommands = newVal)
            .controller(VolcClientConfig::createBooleanController)
            .build())

        .build();
  }

  @SerialEntry
  public boolean partyCommands = true;
}
