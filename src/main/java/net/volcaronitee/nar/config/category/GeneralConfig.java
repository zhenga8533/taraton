package net.volcaronitee.nar.config.category;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.config.NarConfig;

/**
 * Configuration for the general features in NotARat.
 */
public class GeneralConfig {
    /**
     * Creates a new {@link ConfigCategory} for the general features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the general features.
     */
    public static ConfigCategory create(NarConfig defaults, NarConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("General"))

                // Essential Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Essential"))

                        // TODO: Mod Enabled
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Mod Enabled"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/mod_enabled.webp"))
                                        .text(Text.literal("Enables the mod.")).build())
                                .binding(defaults.general.modEnabled,
                                        () -> config.general.modEnabled,
                                        newVal -> config.general.modEnabled = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: SkyBlock Only
                        .option(Option.<Boolean>createBuilder().name(Text.literal("SkyBlock Only"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/skyblock_only.webp"))
                                        .text(Text.literal("Enables the mod only in SkyBlock."))
                                        .build())
                                .binding(defaults.general.skyblockOnly,
                                        () -> config.general.skyblockOnly,
                                        newVal -> config.general.skyblockOnly = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Performance Mode
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Performance Mode"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/performance_mode.webp"))
                                        .text(Text.literal(
                                                "Enables performance optimizations for better FPS. This causes all feature enabling/disabling to require a restart, including this one."))
                                        .build())
                                .binding(defaults.general.performanceMode,
                                        () -> config.general.performanceMode,
                                        newVal -> config.general.performanceMode = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Socket Connection
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Socket Connection"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/socket_connection.webp"))
                                        .text(Text.literal(
                                                "Enables the socket connection for real-time updates."))
                                        .build())
                                .binding(defaults.general.socketConnection,
                                        () -> config.general.socketConnection,
                                        newVal -> config.general.socketConnection = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // Discord Link
                        .option(ButtonOption.createBuilder().name(Text.literal("Discord Link"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/discord_link.webp"))
                                        .text(Text.literal(
                                                "Join our Discord server for support and updates."))
                                        .build())
                                .action((screen, option) -> {
                                    String url = "https://discord.gg/ftxB4kG2tw";
                                    net.minecraft.util.Util.getOperatingSystem().open(url);
                                }).text(Text.literal("Yamete Kudasai")).build())

                        .build())

                // General Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("General"))

                        // TODO: Image Bypass
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Image Bypass"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/image_bypass.webp"))
                                        .text(Text.literal(
                                                "Bypasses the Hypixel image block. Converts image urls into special strings that can be rendered by other NAR users."))
                                        .build())
                                .binding(defaults.general.imageBypass,
                                        () -> config.general.imageBypass,
                                        newVal -> config.general.imageBypass = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // Remove Selfie Mode
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Remove Selfie Mode"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/remove_selfie_mode.webp"))
                                        .text(Text.literal(
                                                "Removes the first person mode from F5 perspective toggle."))
                                        .build())
                                .binding(defaults.general.removeSelfieMode,
                                        () -> config.general.removeSelfieMode,
                                        newVal -> config.general.removeSelfieMode = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Skill Tracker
                        .option(Option.<Integer>createBuilder().name(Text.literal("Skill Tracker"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/skill_tracker.webp"))
                                        .text(Text.literal(
                                                "Tracks skill progress on the screen. Sets time in minutes of inactivity needed before tracking stops."))
                                        .build())
                                .binding(defaults.general.skillTracker,
                                        () -> config.general.skillTracker,
                                        newVal -> config.general.skillTracker = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 10).step(1))
                                .build())

                        // SkyBlock Level Up Alert
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("SkyBlock Level Up Alert"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/skyblock_level_up_alert.webp"))
                                        .text(Text.literal(
                                                "Displays a chat message and title when you level up in SkyBlock."))
                                        .build())
                                .binding(defaults.general.skyblockLevelUpAlert,
                                        () -> config.general.skyblockLevelUpAlert,
                                        newVal -> config.general.skyblockLevelUpAlert = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // SkyBlock XP Alert
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("SkyBlock XP Alert"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/skyblock_xp_alert.webp"))
                                        .text(Text.literal(
                                                "Displays a chat message and title when you gain SkyBlock XP."))
                                        .build())
                                .binding(defaults.general.skyblockXpAlert,
                                        () -> config.general.skyblockXpAlert,
                                        newVal -> config.general.skyblockXpAlert = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Waypoint Timeout
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Waypoint Timeout"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/waypoint_timeout.webp"))
                                        .text(Text.literal(
                                                "Sets time in seconds before waypoints are removed."))
                                        .build())
                                .binding(defaults.general.waypointTimeout,
                                        () -> config.general.waypointTimeout,
                                        newVal -> config.general.waypointTimeout = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 120).step(10))
                                .build())

                        // Widget Display
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Widget Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/widget_display.webp"))
                                        .text(Text.literal(
                                                "Displays SkyBlock widgets on the screen. Set which widgets to display using /vc widgets."))
                                        .build())
                                .binding(defaults.general.widgetDisplay,
                                        () -> config.general.widgetDisplay,
                                        newVal -> config.general.widgetDisplay = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        .build())

                // Server Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Server"))

                        // TODO: Fairy Soul Waypoints
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Fairy Soul Waypoints"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/fairy_soul_waypoints.webp"))
                                        .text(Text.literal(
                                                "Renders waypoints of Fairy Soul locations. Sets maximum distance for waypoints to be rendered."))
                                        .build())
                                .binding(defaults.general.fairySoulWaypoints,
                                        () -> config.general.fairySoulWaypoints,
                                        newVal -> config.general.fairySoulWaypoints = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 128).step(4))
                                .build())

                        // TODO: Hide Far Entities
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Hide Far Entities"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/hide_far_entities.webp"))
                                        .text(Text.literal(
                                                "Sets the maximum distance an entity can be before it is hidden."))
                                        .build())
                                .binding(defaults.general.hideFarEntities,
                                        () -> config.general.hideFarEntities,
                                        newVal -> config.general.hideFarEntities = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 128).step(4))
                                .build())

                        // TODO: Hide Close Players
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Hide Close Players"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/hide_close_players.webp"))
                                        .text(Text.literal(
                                                "Sets the minimum distance a player can be before they are hidden."))
                                        .build())
                                .binding(defaults.general.hideClosePlayers,
                                        () -> config.general.hideClosePlayers,
                                        newVal -> config.general.hideClosePlayers = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 128).step(4))
                                .build())

                        // Hide All Particles
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Hide All Particles"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/hide_all_particles.webp"))
                                        .text(Text.literal("Hides all particles in the game."))
                                        .build())
                                .binding(defaults.general.hideAllParticles,
                                        () -> config.general.hideAllParticles,
                                        newVal -> config.general.hideAllParticles = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // Server Rejoin Alert
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Server Rejoin Alert"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/server_rejoin_alert.webp"))
                                        .text(Text.literal(
                                                "Alerts you when you are rejoining a server. Tracking refreshes on new Minecraft instances."))
                                        .build())
                                .binding(defaults.general.serverRejoinAlert,
                                        () -> config.general.serverRejoinAlert,
                                        newVal -> config.general.serverRejoinAlert = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Server Status
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Server Status"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/server_status.webp"))
                                        .text(Text.literal("Displays various server information."))
                                        .build())
                                .binding(defaults.general.serverStatus,
                                        () -> config.general.serverStatus,
                                        newVal -> config.general.serverStatus = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        .build())

                // Timer Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Timer"))

                        // TODO: Item Cooldown Alert
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Item Cooldown Alert"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/item_cooldown_alert.webp"))
                                        .text(Text.literal(
                                                "Alerts you when an item is ready to use. Set item cooldowns using /vc cd."))
                                        .build())
                                .binding(defaults.general.itemCooldownAlert,
                                        () -> config.general.itemCooldownAlert,
                                        newVal -> config.general.itemCooldownAlert = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Reminder Text
                        .option(Option.<String>createBuilder().name(Text.literal("Reminder Text"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/reminder_text.webp"))
                                        .text(Text.literal("Sets the text for the reminder alert."))
                                        .build())
                                .binding(defaults.general.reminderText,
                                        () -> config.general.reminderText,
                                        newVal -> config.general.reminderText = newVal)
                                .controller(opt -> StringControllerBuilder.create(opt)).build())

                        // TODO: Reminder Timer
                        .option(Option.<Integer>createBuilder().name(Text.literal("Reminder Timer"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/reminder_timer.webp"))
                                        .text(Text.literal(
                                                "Sets the time in seconds for the reminder alert."))
                                        .build())
                                .binding(defaults.general.reminderTimer,
                                        () -> config.general.reminderTimer,
                                        newVal -> config.general.reminderTimer = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 3600).step(30))
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
    public boolean performanceMode = false;

    @SerialEntry
    public boolean socketConnection = true;

    // General Option Group
    @SerialEntry
    public boolean imageBypass = false;

    @SerialEntry
    public boolean removeSelfieMode = false;

    @SerialEntry
    public int skillTracker = 0;

    @SerialEntry
    public boolean skyblockLevelUpAlert = true;

    @SerialEntry
    public boolean skyblockXpAlert = true;

    @SerialEntry
    public int waypointTimeout = 0;

    @SerialEntry
    public boolean widgetDisplay = true;

    // Server Option Group
    @SerialEntry
    public int fairySoulWaypoints = 0;

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

    // Timer Option Group
    @SerialEntry
    public boolean itemCooldownAlert = true;

    @SerialEntry
    public String reminderText = "";

    @SerialEntry
    public int reminderTimer = 0;
}
