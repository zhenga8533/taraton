package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
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
                                        "Tracks skill progress on the screen. Sets time in seconds of inactivity needed before tracking stops.")))
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
                                        "Displays SkyBlock widgets on the screen. Set which widgets to display using /vc widgets.")))
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
                                        .of(Text.literal("Highlights fairy soul locations.")))
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

                        // Hide All Particles
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Hide All Particles"))
                                .description(OptionDescription
                                        .of(Text.literal("Hides all particles in the game.")))
                                .binding(defaults.general.hideAllParticles,
                                        () -> config.general.hideAllParticles,
                                        newVal -> config.general.hideAllParticles = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Server Rejoin Alert
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Server Rejoin Alert"))
                                .description(OptionDescription.of(Text
                                        .literal("Alerts you when you are rejoining a server.")))
                                .binding(defaults.general.serverRejoinAlert,
                                        () -> config.general.serverRejoinAlert,
                                        newVal -> config.general.serverRejoinAlert = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Server Status
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Server Status"))
                                .description(OptionDescription
                                        .of(Text.literal("Displays various server information.")))
                                .binding(defaults.general.serverStatus,
                                        () -> config.general.serverStatus,
                                        newVal -> config.general.serverStatus = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Stats Display
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Stats Display"))
                                .description(OptionDescription
                                        .of(Text.literal("Displays various player stats.")))
                                .binding(defaults.general.statsDisplay,
                                        () -> config.general.statsDisplay,
                                        newVal -> config.general.statsDisplay = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        .build())

                // Timer Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Timer"))

                        // Item Cooldown Alert
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Item Cooldown Alert"))
                                .description(OptionDescription.of(Text.literal(
                                        "Alerts you when an item is ready to use. Set item cooldowns using /vc cd.")))
                                .binding(defaults.general.itemCooldownAlert,
                                        () -> config.general.itemCooldownAlert,
                                        newVal -> config.general.itemCooldownAlert = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Reminder Text
                        .option(Option.<String>createBuilder().name(Text.literal("Reminder Text"))
                                .description(OptionDescription
                                        .of(Text.literal("Sets the text for the reminder alert.")))
                                .binding(defaults.general.reminderText,
                                        () -> config.general.reminderText,
                                        newVal -> config.general.reminderText = newVal)
                                .controller(opt -> StringControllerBuilder.create(opt)).build())

                        // Reminder Timer
                        .option(Option.<Integer>createBuilder().name(Text.literal("Reminder Timer"))
                                .description(OptionDescription.of(Text.literal(
                                        "Sets the time in seconds for the reminder alert.")))
                                .binding(defaults.general.reminderTimer,
                                        () -> config.general.reminderTimer,
                                        newVal -> config.general.reminderTimer = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 3600).step(30))
                                .build())

                        .build())

                // Yapping Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Yapping"))

                        // Autocomplete Command
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Autocomplete Command"))
                                .description(OptionDescription.of(Text.literal(
                                        "Enables the autocomplete command feature. Autocompletes commands in chat.")))
                                .binding(defaults.general.autocompleteCommand,
                                        () -> config.general.autocompleteCommand,
                                        newVal -> config.general.autocompleteCommand = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Autocorrect Command
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Autocorrect Command"))
                                .description(OptionDescription.of(Text.literal(
                                        "Enables the autocorrect command feature. Autocorrects commands in chat.")))
                                .binding(defaults.general.autocorrectCommand,
                                        () -> config.general.autocorrectCommand,
                                        newVal -> config.general.autocorrectCommand = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Custom Emotes
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Custom Emotes"))
                                .description(OptionDescription.of(Text.literal(
                                        "Allows the use of MVP++ emotes in chat. Customize your emotes using /vc emotes.")))
                                .binding(defaults.general.customEmotes,
                                        () -> config.general.customEmotes,
                                        newVal -> config.general.customEmotes = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // SkyBlock XP Alert
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("SkyBlock XP Alert"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays a chat message and title when you gain SkyBlock XP.")))
                                .binding(defaults.general.skyblockXpAlert,
                                        () -> config.general.skyblockXpAlert,
                                        newVal -> config.general.skyblockXpAlert = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

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

    @SerialEntry
    public boolean hideAllParticles = false;

    @SerialEntry
    public boolean serverRejoinAlert = true;

    @SerialEntry
    public boolean serverStatus = false;

    @SerialEntry
    public boolean statsDisplay = false;

    // Timer Option Group
    @SerialEntry
    public boolean itemCooldownAlert = true;

    @SerialEntry
    public String reminderText = "";

    @SerialEntry
    public int reminderTimer = 0;

    // Yapping Option Group
    @SerialEntry
    public boolean autocompleteCommand = true;

    @SerialEntry
    public boolean autocorrectCommand = true;

    @SerialEntry
    public boolean customEmotes = true;

    @SerialEntry
    public boolean skyblockXpAlert = true;
}
