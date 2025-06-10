package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.VolcClientConfig;

public class GeneralConfig {
    public static ConfigCategory create(VolcClientConfig defaults, VolcClientConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("General"))

                // Essential Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Essential"))

                        // Mod Enabled
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Mod Enabled"))
                                .description(OptionDescription.of(Text.literal("Enables the mod.")))
                                .binding(defaults.general.modEnabled,
                                        () -> config.general.modEnabled,
                                        newVal -> config.general.modEnabled = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // SkyBlock Only
                        .option(Option.<Boolean>createBuilder().name(Text.literal("SkyBlock Only"))
                                .description(OptionDescription
                                        .of(Text.literal("Enables the mod only in SkyBlock.")))
                                .binding(defaults.general.skyblockOnly,
                                        () -> config.general.skyblockOnly,
                                        newVal -> config.general.skyblockOnly = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Socket Connection
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Socket Connection"))
                                .description(OptionDescription.of(Text.literal(
                                        "Enables the socket connection for real-time updates.")))
                                .binding(defaults.general.socketConnection,
                                        () -> config.general.socketConnection,
                                        newVal -> config.general.socketConnection = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Discord Link
                        .option(ButtonOption.createBuilder().name(Text.literal("Discord Link"))
                                .description(OptionDescription.of(Text.literal(
                                        "Join our Discord server for support and updates.")))
                                .action((screen, option) -> {
                                    String url = "https://discord.gg/ftxB4kG2tw";
                                    net.minecraft.util.Util.getOperatingSystem().open(url);
                                }).text(Text.literal("Yamete Kudasai")).build())

                        .build())

                // General Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("General"))

                        // Remove Selfie Mode
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Remove Selfie Mode"))
                                .description(OptionDescription.of(Text.literal(
                                        "Removes the first person mode from F5 perspective toggle.")))
                                .binding(defaults.general.removeSelfieMode,
                                        () -> config.general.removeSelfieMode,
                                        newVal -> config.general.removeSelfieMode = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Skill Tracker
                        .option(Option.<Integer>createBuilder().name(Text.literal("Skill Tracker"))
                                .description(OptionDescription.of(Text.literal(
                                        "Tracks skill progress as a GUI element. Sets time in seconds of inactivity needed before tracking stops.")))
                                .binding(defaults.general.skillTracker,
                                        () -> config.general.skillTracker,
                                        newVal -> config.general.skillTracker = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 10).step(1))
                                .build())

                        // Waypoint Timeout
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Waypoint Timeout"))
                                .description(OptionDescription.of(Text.literal(
                                        "Sets time in seconds before waypoints are removed.")))
                                .binding(defaults.general.waypointTimeout,
                                        () -> config.general.waypointTimeout,
                                        newVal -> config.general.waypointTimeout = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 120).step(1))
                                .build())

                        // Widget Display
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Widget Display"))
                                .description(OptionDescription.of(Text.literal(
                                        "Enables the widget display. Displays any widget in /vc wgl.")))
                                .binding(defaults.general.widgetDisplay,
                                        () -> config.general.widgetDisplay,
                                        newVal -> config.general.widgetDisplay = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        .build())

                // Server Option Group

                .group(OptionGroup.createBuilder().name(Text.literal("Server"))

                        // Fairy Soul Waypoints
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Fairy Soul Waypoints"))
                                .description(OptionDescription
                                        .of(Text.literal("Enables Fairy Soul waypoints.")))
                                .binding(defaults.general.fairySoulWaypoints,
                                        () -> config.general.fairySoulWaypoints,
                                        newVal -> config.general.fairySoulWaypoints = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Hide Far Entities
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Hide Far Entities"))
                                .description(OptionDescription.of(Text.literal(
                                        "Sets the maximum distance an entity can be before it is hidden.")))
                                .binding(defaults.general.hideFarEntities,
                                        () -> config.general.hideFarEntities,
                                        newVal -> config.general.hideFarEntities = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 128).step(1))
                                .build())

                        // Hide Close Players
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Hide Close Players"))
                                .description(OptionDescription.of(Text.literal(
                                        "Sets the minimum distance a player can be before they are hidden.")))
                                .binding(defaults.general.hideClosePlayers,
                                        () -> config.general.hideClosePlayers,
                                        newVal -> config.general.hideClosePlayers = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 128).step(1))
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

    @SerialEntry
    public int skillTracker = 0;

    @SerialEntry
    public int waypointTimeout = 0;

    @SerialEntry
    public boolean widgetDisplay = true;

    // Server Option Group
    @SerialEntry
    public boolean fairySoulWaypoints = false;

    @SerialEntry
    public int hideFarEntities = 0;

    @SerialEntry
    public int hideClosePlayers = 0;
}
