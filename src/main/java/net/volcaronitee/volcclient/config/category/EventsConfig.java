package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.volcclient.VolcClient;
import net.volcaronitee.volcclient.util.ConfigUtil;

/**
 * Configuration for the Events features in VolcClient.
 */
public class EventsConfig {
    /**
     * Creates a new {@link ConfigCategory} for the Events features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the Events features.
     */
    public static ConfigCategory create(ConfigUtil defaults, ConfigUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal("Events"))

                // Hoppity Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Hoppity"))

                        // TODO: Chocolate Factory Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Chocolate Factory Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/events/chocolate_factory_display.webp"))
                                        .text(Text.literal(
                                                "Displays Chocolate Factory production statistics on the screen."))
                                        .build())
                                .binding(defaults.events.chocolateFactoryDisplay,
                                        () -> config.events.chocolateFactoryDisplay,
                                        newVal -> config.events.chocolateFactoryDisplay = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Egg Timers
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Egg Timers"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/events/egg_timers.webp"))
                                        .text(Text.literal(
                                                "Displays timers for upcoming rabbit eggs on the screen."))
                                        .build())
                                .binding(defaults.events.eggTimers, () -> config.events.eggTimers,
                                        newVal -> config.events.eggTimers = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Egg Waypoints
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Egg Waypoints"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/events/egg_waypoints.webp"))
                                        .text(Text.literal(
                                                "Renders waypoints on nearby rabbit eggs."))
                                        .build())
                                .binding(defaults.events.eggWaypoints,
                                        () -> config.events.eggWaypoints,
                                        newVal -> config.events.eggWaypoints = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Rabbit Worker Highlight
                        .option(Option.<RabbitWorkerHighlight>createBuilder()
                                .name(Text.literal("Rabbit Worker Highlight"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/events/rabbit_worker_highlight.webp"))
                                        .text(Text.literal(
                                                "Highlights the most efficient upgrade ratio in the Chocolate Factory menu."))
                                        .build())
                                .binding(defaults.events.rabbitWorkerHighlight,
                                        () -> config.events.rabbitWorkerHighlight,
                                        newVal -> config.events.rabbitWorkerHighlight = newVal)
                                .controller(ConfigUtil::createEnumController).build())

                        // TODO: Stray Rabbit Alert
                        .option(Option.<StrayRabbitAlert>createBuilder()
                                .name(Text.literal("Stray Rabbit Alert"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/events/stray_rabbit_alert.webp"))
                                        .text(Text.literal(
                                                "Alerts you when a stray rabbit appears in the Chocolate Factory menu."))
                                        .build())
                                .binding(defaults.events.strayRabbitAlert,
                                        () -> config.events.strayRabbitAlert,
                                        newVal -> config.events.strayRabbitAlert = newVal)
                                .controller(ConfigUtil::createEnumController).build())

                        .build())

                // SkyBlock Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("SkyBlock"))

                        // TODO: Bingo Card Display
                        .option(Option.<BingoCardDisplay>createBuilder()
                                .name(Text.literal("Bingo Card Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/events/bingo_card_display.webp"))
                                        .text(Text.literal(
                                                "Displays the current bingo card goals on the screen."))
                                        .build())
                                .binding(defaults.events.bingoCardDisplay,
                                        () -> config.events.bingoCardDisplay,
                                        newVal -> config.events.bingoCardDisplay = newVal)
                                .controller(ConfigUtil::createEnumController).build())

                        // TODO: Calendar Time
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Calendar Time"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/events/calendar_time.webp"))
                                        .text(Text.literal(
                                                "Displays the real world start and end times of SkyBlock events in the calendar."))
                                        .build())
                                .binding(defaults.events.calendarTime,
                                        () -> config.events.calendarTime,
                                        newVal -> config.events.calendarTime = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                .build();
    }

    // Hoppity Option Group
    @SerialEntry
    public boolean chocolateFactoryDisplay = false;

    @SerialEntry
    public boolean eggTimers = false;

    @SerialEntry
    public boolean eggWaypoints = false;

    @SerialEntry
    public RabbitWorkerHighlight rabbitWorkerHighlight = RabbitWorkerHighlight.OFF;

    public enum RabbitWorkerHighlight implements NameableEnum {
        OFF, ALL, WORKERS, NO_TOWER;

        @Override
        public Text getDisplayName() {
            return switch (this) {
                case OFF -> Text.literal("Disabled");
                case ALL -> Text.literal("All Upgrades");
                case WORKERS -> Text.literal("Workers Only");
                case NO_TOWER -> Text.literal("No Tower");
            };
        }
    }

    @SerialEntry
    public StrayRabbitAlert strayRabbitAlert = StrayRabbitAlert.OFF;

    public enum StrayRabbitAlert implements NameableEnum {
        OFF, ALL, GOLD;

        @Override
        public Text getDisplayName() {
            return switch (this) {
                case OFF -> Text.literal("Disabled");
                case ALL -> Text.literal("All Strays");
                case GOLD -> Text.literal("Golds Only");
            };
        }
    }

    // SkyBlock Option Group
    @SerialEntry
    public BingoCardDisplay bingoCardDisplay = BingoCardDisplay.OFF;

    public enum BingoCardDisplay implements NameableEnum {
        OFF, ALL, PERSONAL, COMMUNITY;

        @Override
        public Text getDisplayName() {
            return switch (this) {
                case OFF -> Text.literal("Disabled");
                case ALL -> Text.literal("All Goals");
                case PERSONAL -> Text.literal("Personal Goals");
                case COMMUNITY -> Text.literal("Community Goals");
            };
        }
    }

    @SerialEntry
    public boolean calendarTime = false;
}
