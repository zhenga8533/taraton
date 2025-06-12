package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.ConfigUtil.AnnounceWP;

public class EventsConfig {
    public static ConfigCategory create(ConfigUtil defaults, ConfigUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal("Events"))

                // Diana Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Diana"))

                        // Burrow Detection
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Burrow Detection"))
                                .description(OptionDescription.of(Text.literal(
                                        "Renders waypoints on nearby burrows and displays a chat message.")))
                                .binding(defaults.events.burrowDetection,
                                        () -> config.events.burrowDetection,
                                        newVal -> config.events.burrowDetection = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Burrow Waypoints
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Burrow Waypoints"))
                                .description(OptionDescription.of(Text.literal(
                                        "Renders the best guess of a burrow location using ancestral spade particals and sounds.")))
                                .binding(defaults.events.burrowWaypoints,
                                        () -> config.events.burrowWaypoints,
                                        newVal -> config.events.burrowWaypoints = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Inquisitor Announce
                        .option(Option.<AnnounceWP>createBuilder()
                                .name(Text.literal("Inquisitor Announce"))
                                .description(OptionDescription.of(
                                        Text.literal("Sends a chat message on inquisitor spawn.")))
                                .binding(defaults.events.inquisitorAnnounce,
                                        () -> config.events.inquisitorAnnounce,
                                        newVal -> config.events.inquisitorAnnounce = newVal)
                                .controller(ConfigUtil::createEnumController).build())

                        // Inquisitor Detection
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Inquisitor Detection"))
                                .description(OptionDescription.of(Text.literal(
                                        "Highlights nearby inquisitors. This includes inquisitors spawned by other players.")))
                                .binding(defaults.events.inquisitorDetection,
                                        () -> config.events.inquisitorDetection,
                                        newVal -> config.events.inquisitorDetection = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                // Hoppity Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Hoppity"))

                        // Chocolate Factory Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Chocolate Factory Display"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays Chocolate Factory production statistics on the screen.")))
                                .binding(defaults.events.chocolateFactoryDisplay,
                                        () -> config.events.chocolateFactoryDisplay,
                                        newVal -> config.events.chocolateFactoryDisplay = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Egg Timers
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Egg Timers"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays timers for upcoming rabbit eggs on the screen.")))
                                .binding(defaults.events.eggTimers, () -> config.events.eggTimers,
                                        newVal -> config.events.eggTimers = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Egg Waypoints
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Egg Waypoints"))
                                .description(OptionDescription.of(
                                        Text.literal("Renders waypoints on nearby rabbit eggs.")))
                                .binding(defaults.events.eggWaypoints,
                                        () -> config.events.eggWaypoints,
                                        newVal -> config.events.eggWaypoints = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Rabbit Worker Highlight
                        .option(Option.<RabbitWorkerHighlight>createBuilder()
                                .name(Text.literal("Rabbit Worker Highlight"))
                                .description(OptionDescription.of(Text.literal(
                                        "Highlights the most efficient upgrade ratio in the Chocolate Factory menu.")))
                                .binding(defaults.events.rabbitWorkerHighlight,
                                        () -> config.events.rabbitWorkerHighlight,
                                        newVal -> config.events.rabbitWorkerHighlight = newVal)
                                .controller(ConfigUtil::createEnumController).build())

                        // Stray Rabbit Alert
                        .option(Option.<StrayRabbitAlert>createBuilder()
                                .name(Text.literal("Stray Rabbit Alert"))
                                .description(OptionDescription.of(Text.literal(
                                        "Alerts you when a stray rabbit appears in the Chocolate Factory menu.")))
                                .binding(defaults.events.strayRabbitAlert,
                                        () -> config.events.strayRabbitAlert,
                                        newVal -> config.events.strayRabbitAlert = newVal)
                                .controller(ConfigUtil::createEnumController).build())

                        .build())

                // SkyBlock Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("SkyBlock"))

                        // Bingo Card Display
                        .option(Option.<BingoCardDisplay>createBuilder()
                                .name(Text.literal("Bingo Card Display"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays the current bingo card goals on the screen.")))
                                .binding(defaults.events.bingoCardDisplay,
                                        () -> config.events.bingoCardDisplay,
                                        newVal -> config.events.bingoCardDisplay = newVal)
                                .controller(ConfigUtil::createEnumController).build())

                        // Calendar Time
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Calendar Time"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays the real world start and end times of SkyBlock events in the calendar.")))
                                .binding(defaults.events.calendarTime,
                                        () -> config.events.calendarTime,
                                        newVal -> config.events.calendarTime = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                .build();
    }

    // Diana Option Group
    @SerialEntry
    public boolean burrowDetection = false;

    @SerialEntry
    public boolean burrowWaypoints = false;

    @SerialEntry
    public AnnounceWP inquisitorAnnounce = AnnounceWP.OFF;

    @SerialEntry
    public boolean inquisitorDetection = false;

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
