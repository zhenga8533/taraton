package net.volcaronitee.taraton.config.category;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonConfig;

/**
 * Configuration for the general features in Taraton.
 */
public class GeneralConfig {
    /**
     * Creates a new {@link ConfigCategory} for the general features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the general features.
     */
    public static ConfigCategory create(TaratonConfig defaults, TaratonConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("General"))

                // Essential Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Essential"))

                        // TODO: Mod Enabled
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Mod Enabled"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/general/mod_enabled.webp"))
                                        .text(Text.literal("Enables the mod.")).build())
                                .binding(defaults.general.modEnabled,
                                        () -> config.general.modEnabled,
                                        newVal -> config.general.modEnabled = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        // TODO: SkyBlock Only
                        .option(Option.<Boolean>createBuilder().name(Text.literal("SkyBlock Only"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/general/skyblock_only.webp"))
                                        .text(Text.literal("Enables the mod only in SkyBlock."))
                                        .build())
                                .binding(defaults.general.skyblockOnly,
                                        () -> config.general.skyblockOnly,
                                        newVal -> config.general.skyblockOnly = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        // TODO: Performance Mode
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Performance Mode"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/general/performance_mode.webp"))
                                        .text(Text.literal(
                                                "Enables performance optimizations for better FPS. This causes all feature enabling/disabling to require a restart, including this one."))
                                        .build())
                                .binding(defaults.general.performanceMode,
                                        () -> config.general.performanceMode,
                                        newVal -> config.general.performanceMode = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        // Update Notification
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Update Notification"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/general/update_notification.webp"))
                                        .text(Text.literal(
                                                "Notifies you in chat when a new version of Taraton is available."))
                                        .build())
                                .binding(defaults.general.updateNotification,
                                        () -> config.general.updateNotification,
                                        newVal -> config.general.updateNotification = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        // TODO: Socket Connection
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Socket Connection"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/general/socket_connection.webp"))
                                        .text(Text.literal(
                                                "Enables the socket connection for real-time updates."))
                                        .build())
                                .binding(defaults.general.socketConnection,
                                        () -> config.general.socketConnection,
                                        newVal -> config.general.socketConnection = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        // Discord Link
                        .option(ButtonOption.createBuilder().name(Text.literal("Discord Link"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
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

                        // Image Preview
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Image Preview"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/general/image_preview.webp"))
                                        .text(Text.literal(
                                                "Bypasses the Hypixel image block and renders images. Converts image urls into special strings that can be rendered by other Taraton users."))
                                        .build())
                                .binding(defaults.general.imagePreview,
                                        () -> config.general.imagePreview,
                                        newVal -> config.general.imagePreview = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        // Render Waypoint
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Render Waypoint"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/general/render_waypoint.webp"))
                                        .text(Text.literal(
                                                "Sets time in seconds before waypoints are removed."))
                                        .build())
                                .binding(defaults.general.renderWaypoints,
                                        () -> config.general.renderWaypoints,
                                        newVal -> config.general.renderWaypoints = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 120).step(10))
                                .build())

                        // TODO: Skill Tracker
                        .option(Option.<Integer>createBuilder().name(Text.literal("Skill Tracker"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
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
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/general/skyblock_level_up_alert.webp"))
                                        .text(Text.literal(
                                                "Displays a chat message and title when you level up in SkyBlock."))
                                        .build())
                                .binding(defaults.general.skyblockLevelUpAlert,
                                        () -> config.general.skyblockLevelUpAlert,
                                        newVal -> config.general.skyblockLevelUpAlert = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        // SkyBlock XP Alert
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("SkyBlock XP Alert"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/general/skyblock_xp_alert.webp"))
                                        .text(Text.literal(
                                                "Displays a chat message and title when you gain SkyBlock XP."))
                                        .build())
                                .binding(defaults.general.skyblockXpAlert,
                                        () -> config.general.skyblockXpAlert,
                                        newVal -> config.general.skyblockXpAlert = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        // Widget Display
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Widget Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/general/widget_display.webp"))
                                        .text(Text.literal(
                                                "Displays SkyBlock widgets on the screen. Set which widgets to display using /vc widgets."))
                                        .build())
                                .binding(defaults.general.widgetDisplay,
                                        () -> config.general.widgetDisplay,
                                        newVal -> config.general.widgetDisplay = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        .build())

                // Server Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Server"))

                        // TODO: Fairy Soul Waypoints
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Fairy Soul Waypoints"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
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

                        // Server Rejoin Alert
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Server Rejoin Alert"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/general/server_rejoin_alert.webp"))
                                        .text(Text.literal(
                                                "Alerts you when you are rejoining a server. Tracking refreshes on new Minecraft instances."))
                                        .build())
                                .binding(defaults.general.serverRejoinAlert,
                                        () -> config.general.serverRejoinAlert,
                                        newVal -> config.general.serverRejoinAlert = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        // TODO: Server Status
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Server Status"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/general/server_status.webp"))
                                        .text(Text.literal("Displays various server information."))
                                        .build())
                                .binding(defaults.general.serverStatus,
                                        () -> config.general.serverStatus,
                                        newVal -> config.general.serverStatus = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        .build())

                // Timer Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Timer"))

                        // TODO: Item Cooldown Alert
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Item Cooldown Alert"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/general/item_cooldown_alert.webp"))
                                        .text(Text.literal(
                                                "Alerts you when an item is ready to use. Set item cooldowns using /vc cd."))
                                        .build())
                                .binding(defaults.general.itemCooldownAlert,
                                        () -> config.general.itemCooldownAlert,
                                        newVal -> config.general.itemCooldownAlert = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        // Reminder Timer
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Reminder Timer"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/general/reminder_timer.webp"))
                                        .text(Text.literal(
                                                "Enables the reminder timers. Set reminders using /vc rm."))
                                        .build())
                                .binding(defaults.general.reminderTimer,
                                        () -> config.general.reminderTimer,
                                        newVal -> config.general.reminderTimer = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

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
    public boolean updateNotification = true;

    @SerialEntry
    public boolean socketConnection = true;

    // General Option Group
    @SerialEntry
    public boolean imagePreview = false;

    @SerialEntry
    public int renderWaypoints = 0;

    @SerialEntry
    public int skillTracker = 0;

    @SerialEntry
    public boolean skyblockLevelUpAlert = true;

    @SerialEntry
    public boolean skyblockXpAlert = true;

    @SerialEntry
    public boolean widgetDisplay = true;

    // Server Option Group
    @SerialEntry
    public int fairySoulWaypoints = 0;

    @SerialEntry
    public boolean serverRejoinAlert = true;

    @SerialEntry
    public boolean serverStatus = false;

    // Timer Option Group
    @SerialEntry
    public boolean itemCooldownAlert = true;

    @SerialEntry
    public boolean reminderTimer = false;
}
