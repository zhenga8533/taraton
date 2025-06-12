package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.VolcClientConfig;

public class MiningConfig {
    public static ConfigCategory create(VolcClientConfig defaults, VolcClientConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("Mining"))

                // Commission Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Commission"))

                        // Commission Completion
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Commission Completion"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays a chat message and title when a commission is completed.")))
                                .binding(defaults.mining.commissionCompletion,
                                        () -> config.mining.commissionCompletion,
                                        newVal -> config.mining.commissionCompletion = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Commission Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Commission Display"))
                                .description(OptionDescription.of(Text
                                        .literal("Displays all active commissions on the screen.")))
                                .binding(defaults.mining.commissionDisplay,
                                        () -> config.mining.commissionDisplay,
                                        newVal -> config.mining.commissionDisplay = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Commission Waypoints
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Commission Waypoints"))
                                .description(OptionDescription.of(Text.literal(
                                        "Renders waypoints to commission locations in the Dwarven Mines.")))
                                .binding(defaults.mining.commissionWaypoints,
                                        () -> config.mining.commissionWaypoints,
                                        newVal -> config.mining.commissionWaypoints = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        .build())

                // Jinx Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Jinx"))

                        // Powder Chest Highlight
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Powder Chest Highlight"))
                                .description(OptionDescription.of(Text.literal(
                                        "Highlights nearby powder chests in the Crystal Hollows. Set the block radius to highlight.")))
                                .binding(defaults.mining.powderChestHighlight,
                                        () -> config.mining.powderChestHighlight,
                                        newVal -> config.mining.powderChestHighlight = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 128).step(4))
                                .build())

                        // Powder Tracker
                        .option(Option.<Integer>createBuilder().name(Text.literal("Powder Tracker"))
                                .description(OptionDescription.of(Text.literal(
                                        "Tracks powder progress on the screen. Sets time in minutes of inactivity before tracking stops.")))
                                .binding(defaults.mining.powderTracker,
                                        () -> config.mining.powderTracker,
                                        newVal -> config.mining.powderTracker = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 10).step(1))
                                .build())

                        .build())

                // Mining Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Mining"))

                        // Pickaxe Ability Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Pickaxe Ability Display"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays all pickaxe abilities on the screen. Displays a chat message and title when any ability goes off cooldown.")))
                                .binding(defaults.mining.pickaxeAbilityDisplay,
                                        () -> config.mining.pickaxeAbilityDisplay,
                                        newVal -> config.mining.pickaxeAbilityDisplay = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Wishing Compass Locator
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Wishing Compass Locator"))
                                .description(OptionDescription.of(Text.literal(
                                        "Renders a guess waypoint to the Wishing Compass location in the Crystal Hollows.")))
                                .binding(defaults.mining.wishingCompassLocator,
                                        () -> config.mining.wishingCompassLocator,
                                        newVal -> config.mining.wishingCompassLocator = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        .build())

                // Shaft Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Shaft"))

                        // Corpse Waypoints
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Corpse Waypoints"))
                                .description(OptionDescription.of(Text.literal(
                                        "Renders waypoints to all nearby corpses in a mineshaft. UAYOR: This feature uses ESP!")))
                                .binding(defaults.mining.corpseWaypoints,
                                        () -> config.mining.corpseWaypoints,
                                        newVal -> config.mining.corpseWaypoints = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Fossil Solver
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Fossil Solver"))
                                .description(OptionDescription.of(Text.literal(
                                        "Automatically reveals fossils in the excavator.")))
                                .binding(defaults.mining.fossilSolver,
                                        () -> config.mining.fossilSolver,
                                        newVal -> config.mining.fossilSolver = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        .build())

                .build();
    }

    // Commission Option Group
    @SerialEntry
    public boolean commissionCompletion = false;

    @SerialEntry
    public boolean commissionDisplay = false;

    @SerialEntry
    public boolean commissionWaypoints = false;

    // Jinx Option Group
    @SerialEntry
    public int powderChestHighlight = 0;

    @SerialEntry
    public int powderTracker = 0;

    // Mining Option Group
    @SerialEntry
    public boolean pickaxeAbilityDisplay = false;

    @SerialEntry
    public boolean wishingCompassLocator = false;

    // Shaft Option Group
    @SerialEntry
    public boolean corpseWaypoints = false;

    @SerialEntry
    public boolean fossilSolver = false;
}
