package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.VolcClientConfig;

public class GeneralConfig {
  public static ConfigCategory create(VolcClientConfig defaults, VolcClientConfig config) {
    return ConfigCategory.createBuilder()
        .name(Text.literal("General"))

        // Essential Option Group
        .group(OptionGroup.createBuilder()
            .name(Text.literal("Essential"))

            // Mod Enabled
            .option(Option.<Boolean>createBuilder()
                .name(Text.literal("Mod Enabled"))
                .description(OptionDescription.of(Text.literal("Enables the mod.")))
                .binding(
                    defaults.general.modEnabled,
                    () -> config.general.modEnabled,
                    newVal -> config.general.modEnabled = newVal)
                .controller(VolcClientConfig::createBooleanController)
                .build())

            // SkyBlock Only
            .option(Option.<Boolean>createBuilder()
                .name(Text.literal("SkyBlock Only"))
                .description(OptionDescription.of(Text.literal("Enables the mod only in SkyBlock.")))
                .binding(
                    defaults.general.skyblockOnly,
                    () -> config.general.skyblockOnly,
                    newVal -> config.general.skyblockOnly = newVal)
                .controller(VolcClientConfig::createBooleanController)
                .build())

            // Socket Connection
            .option(Option.<Boolean>createBuilder()
                .name(Text.literal("Socket Connection"))
                .description(OptionDescription.of(Text.literal("Enables the socket connection for real-time updates.")))
                .binding(
                    defaults.general.socketConnection,
                    () -> config.general.socketConnection,
                    newVal -> config.general.socketConnection = newVal)
                .controller(VolcClientConfig::createBooleanController)
                .build())

            // Discord Link
            .option(ButtonOption.createBuilder()
                .name(Text.literal("Discord Link"))
                .description(OptionDescription.of(Text.literal("Join our Discord server for support and updates.")))
                .action((screen, option) -> {
                  String url = "https://discord.gg/ftxB4kG2tw";
                  net.minecraft.util.Util.getOperatingSystem().open(url);
                })
                .text(Text.literal("Yamete Kudasai"))
                .build())

            .build())

        // General Option Group
        .group(OptionGroup.createBuilder()
            .name(Text.literal("General"))

            // Remove Selfie Mode
            .option(Option.<Boolean>createBuilder()
                .name(Text.literal("Remove Selfie Mode"))
                .description(OptionDescription.of(Text.literal("Removes the third-person perspective from F5.")))
                .binding(
                    defaults.general.removeSelfieMode,
                    () -> config.general.removeSelfieMode,
                    newVal -> config.general.removeSelfieMode = newVal)
                .controller(VolcClientConfig::createBooleanController)
                .build())

            .build())

        .build();
  }

  // Essential Option Group
  @SerialEntry
  public boolean modEnabled = true;

  @SerialEntry
  public boolean skyblockOnly = true;

  @SerialEntry
  public boolean socketConnection = true;

  // General Option Group
  @SerialEntry
  public boolean removeSelfieMode = false;
}
