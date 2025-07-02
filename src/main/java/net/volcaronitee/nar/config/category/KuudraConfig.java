package net.volcaronitee.nar.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.config.NarConfig;

/**
 * Configuration for the Kuudra features in NotARat.
 */
public class KuudraConfig {
    /**
     * Creates a new {@link ConfigCategory} for the Kuudra features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the Kuudra features.
     */
    public static ConfigCategory create(NarConfig defaults, NarConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("Kuudra"))

                // Kuudra Profit Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Kuudra Profit"))

                        // TODO: Kuudra Profit
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Kuudra Profit"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/kuudra/kuudra_profit.webp"))
                                        .text(Text.literal(
                                                "Displays profit of any opened Kuudra chest on the screen."))
                                        .build())
                                .binding(defaults.kuudra.kuudraProfit,
                                        () -> config.kuudra.kuudraProfit,
                                        newVal -> config.kuudra.kuudraProfit = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Kuudra Profit Tracker
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra Profit Tracker"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/kuudra/kuudra_profit_tracker.webp"))
                                        .text(Text.literal("Tracks Kuudra profit gains over time."))
                                        .build())
                                .binding(defaults.kuudra.kuudraProfitTracker,
                                        () -> config.kuudra.kuudraProfitTracker,
                                        newVal -> config.kuudra.kuudraProfitTracker = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Tabasco Included
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Tabasco Included"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/kuudra/tabasco_included.webp"))
                                        .text(Text.literal(
                                                "Include Tabasco crafting in the profit calculations."))
                                        .build())
                                .binding(defaults.kuudra.tabascoIncluded,
                                        () -> config.kuudra.tabascoIncluded,
                                        newVal -> config.kuudra.tabascoIncluded = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        .build())

                // Kuudra Run Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Kuudra Run"))

                        // TODO: Kuudra Alerts
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Kuudra Alerts"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/kuudra/kuudra_alerts.webp"))
                                        .text(Text.literal(
                                                "Alerts for various Kuudra splits and events. Set tracked events using /vc toggles."))
                                        .build())
                                .binding(defaults.kuudra.kuudraAlerts,
                                        () -> config.kuudra.kuudraAlerts,
                                        newVal -> config.kuudra.kuudraAlerts = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Kuudra Crate Waypoints
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra Crate Waypoints"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/kuudra/kuudra_crate_waypoints.webp"))
                                        .text(Text.literal(
                                                "Displays waypoints for nearby Kuudra crates."))
                                        .build())
                                .binding(defaults.kuudra.kuudraCrateWaypoints,
                                        () -> config.kuudra.kuudraCrateWaypoints,
                                        newVal -> config.kuudra.kuudraCrateWaypoints = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Kuudra Fresh Tracking
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra Fresh Tracking"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/kuudra/kuudra_fresh_tracking.webp"))
                                        .text(Text.literal(
                                                "Sends chat message to party when fresh tools activates. Also tracks other players' fresh tools when announced."))
                                        .build())
                                .binding(defaults.kuudra.kuudraFreshTracking,
                                        () -> config.kuudra.kuudraFreshTracking,
                                        newVal -> config.kuudra.kuudraFreshTracking = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Kuudra HP Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra HP Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/kuudra/kuudra_hp_display.webp"))
                                        .text(Text.literal("Render Kuudra's HP bar on the screen."))
                                        .build())
                                .binding(defaults.kuudra.kuudraHpDisplay,
                                        () -> config.kuudra.kuudraHpDisplay,
                                        newVal -> config.kuudra.kuudraHpDisplay = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Kuudra Spawn Alert
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra Spawn Alert"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/kuudra/kuudra_spawn_alert.webp"))
                                        .text(Text.literal(
                                                "Displays a title for where Kuudra spawns in P4. UAYOR: Uses ESP!"))
                                        .build())
                                .binding(defaults.kuudra.kuudraSpawnAlert,
                                        () -> config.kuudra.kuudraSpawnAlert,
                                        newVal -> config.kuudra.kuudraSpawnAlert = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Kuudra Splits Timer
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra Splits Timer"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/kuudra/kuudra_splits_timer.webp"))
                                        .text(Text.literal(
                                                "Displays time taken for each Kuudra split on the screen. See Kuudra split records using /vc ks."))
                                        .build())
                                .binding(defaults.kuudra.kuudraSplitsTimer,
                                        () -> config.kuudra.kuudraSplitsTimer,
                                        newVal -> config.kuudra.kuudraSplitsTimer = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Kuudra Supply Piles
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra Supply Piles"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/kuudra/kuudra_supply_piles.webp"))
                                        .text(Text.literal(
                                                "Displays waypoints for nearby incomplete supply piles."))
                                        .build())
                                .binding(defaults.kuudra.kuudraSupplyPiles,
                                        () -> config.kuudra.kuudraSupplyPiles,
                                        newVal -> config.kuudra.kuudraSupplyPiles = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        .build())

                .build();
    }

    // Kuudra Profit Option Group
    @SerialEntry
    public boolean kuudraProfit = false;

    @SerialEntry
    public boolean kuudraProfitTracker = false;

    @SerialEntry
    public boolean tabascoIncluded = false;

    // Kuudra Run Option Group
    @SerialEntry
    public boolean kuudraAlerts = false;

    @SerialEntry
    public boolean kuudraCrateWaypoints = false;

    @SerialEntry
    public boolean kuudraFreshTracking = false;

    @SerialEntry
    public boolean kuudraHpDisplay = false;

    @SerialEntry
    public boolean kuudraSpawnAlert = false;

    @SerialEntry
    public boolean kuudraSplitsTimer = false;

    @SerialEntry
    public boolean kuudraSupplyPiles = false;
}
