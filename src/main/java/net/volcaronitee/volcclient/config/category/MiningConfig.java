package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.volcclient.VolcClient;
import net.volcaronitee.volcclient.util.ConfigUtil;

/**
 * Configuration for the Mining features in VolcClient.
 */
public class MiningConfig {
    /**
     * Creates a new {@link ConfigCategory} for the Mining features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the Mining features.
     */
    public static ConfigCategory create(ConfigUtil defaults, ConfigUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal("Mining"))

                // Commission Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Commission"))

                        // TODO: Commission Completion
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Commission Completion"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/mining/commission_completion.webp"))
                                        .text(Text.literal(
                                                "Displays a chat message and title when a commission is completed."))
                                        .build())
                                .binding(defaults.mining.commissionCompletion,
                                        () -> config.mining.commissionCompletion,
                                        newVal -> config.mining.commissionCompletion = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Commission Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Commission Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/mining/commission_display.webp"))
                                        .text(Text.literal(
                                                "Displays all active commissions on the screen."))
                                        .build())
                                .binding(defaults.mining.commissionDisplay,
                                        () -> config.mining.commissionDisplay,
                                        newVal -> config.mining.commissionDisplay = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Commission Waypoints
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Commission Waypoints"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/mining/commission_waypoints.webp"))
                                        .text(Text.literal(
                                                "Renders waypoints to commission locations in the Dwarven Mines."))
                                        .build())
                                .binding(defaults.mining.commissionWaypoints,
                                        () -> config.mining.commissionWaypoints,
                                        newVal -> config.mining.commissionWaypoints = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                // Jinx Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Jinx"))

                        // TODO: Powder Tracker
                        .option(Option.<Integer>createBuilder().name(Text.literal("Powder Tracker"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/mining/powder_tracker.webp"))
                                        .text(Text.literal(
                                                "Tracks powder progress on the screen. Sets time in minutes of inactivity before tracking stops."))
                                        .build())
                                .binding(defaults.mining.powderTracker,
                                        () -> config.mining.powderTracker,
                                        newVal -> config.mining.powderTracker = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 10).step(1))
                                .build())

                        .build())

                // Mining Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Mining"))

                        // TODO: Pickaxe Ability Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Pickaxe Ability Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/mining/pickaxe_ability_display.webp"))
                                        .text(Text.literal(
                                                "Displays all pickaxe abilities on the screen. Displays a chat message and title when any ability goes off cooldown."))
                                        .build())
                                .binding(defaults.mining.pickaxeAbilityDisplay,
                                        () -> config.mining.pickaxeAbilityDisplay,
                                        newVal -> config.mining.pickaxeAbilityDisplay = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Wishing Compass Locator
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Wishing Compass Locator"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/mining/wishing_compass_locator.webp"))
                                        .text(Text.literal(
                                                "Renders a guess waypoint to the Wishing Compass location in the Crystal Hollows."))
                                        .build())
                                .binding(defaults.mining.wishingCompassLocator,
                                        () -> config.mining.wishingCompassLocator,
                                        newVal -> config.mining.wishingCompassLocator = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                // Shaft Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Shaft"))

                        // TODO: Corpse Waypoints
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Corpse Waypoints"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/mining/corpse_waypoints.webp"))
                                        .text(Text.literal(
                                                "Renders waypoints to all nearby corpses in a mineshaft. UAYOR: This feature uses ESP!"))
                                        .build())
                                .binding(defaults.mining.corpseWaypoints,
                                        () -> config.mining.corpseWaypoints,
                                        newVal -> config.mining.corpseWaypoints = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Fossil Solver
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Fossil Solver"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/mining/fossil_solver.webp"))
                                        .text(Text.literal(
                                                "Automatically reveals fossils in the excavator."))
                                        .build())
                                .binding(defaults.mining.fossilSolver,
                                        () -> config.mining.fossilSolver,
                                        newVal -> config.mining.fossilSolver = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

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
