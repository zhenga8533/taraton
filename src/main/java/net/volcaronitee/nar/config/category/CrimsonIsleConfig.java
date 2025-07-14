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
 * Configuration for the Crimson Isle features in NotARat.
 */
public class CrimsonIsleConfig {
    /**
     * Creates a new {@link ConfigCategory} for the Crimson Isle features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the Crimson Isle features.
     */
    public static ConfigCategory create(NarConfig defaults, NarConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("Crimson Isle"))

                // Crimson Isle Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Crimson Isle"))

                        // Vanquisher Warp
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Vanquisher Warp"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/crimson_isle/vanquisher_warp.webp"))
                                        .text(Text.literal(
                                                "Warps vanquisher swap party to your lobby when you spawn a vanquisher. Set the players in your party using /vc vanq."))
                                        .build())
                                .binding(defaults.crimsonIsle.vanquisherWarp,
                                        () -> config.crimsonIsle.vanquisherWarp,
                                        newVal -> config.crimsonIsle.vanquisherWarp = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        .build())

                // Fishing Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Fishing"))

                        // TODO: Golden Fish Timer
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Golden Fish Timer"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/crimson_isle/golden_fish_timer.webp"))
                                        .text(Text.literal(
                                                "Displays a timer for the golden trophy fish on the screen."))
                                        .build())
                                .binding(defaults.crimsonIsle.goldenFishTimer,
                                        () -> config.crimsonIsle.goldenFishTimer,
                                        newVal -> config.crimsonIsle.goldenFishTimer = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Trophy Fisher Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Trophy Fish Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/crimson_isle/trophy_fisher_display.webp"))
                                        .text(Text.literal(
                                                "Displays the session trophy fishing progress on the screen."))
                                        .build())
                                .binding(defaults.crimsonIsle.trophyFishDisplay,
                                        () -> config.crimsonIsle.trophyFishDisplay,
                                        newVal -> config.crimsonIsle.trophyFishDisplay = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        .build())

                // Kuudra Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Kuudra"))

                        // TODO: Kuudra Profit
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Kuudra Profit"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/kuudra/kuudra_profit.webp"))
                                        .text(Text.literal(
                                                "Displays profit of any opened Kuudra chest on the screen."))
                                        .build())
                                .binding(defaults.crimsonIsle.kuudraProfit,
                                        () -> config.crimsonIsle.kuudraProfit,
                                        newVal -> config.crimsonIsle.kuudraProfit = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Kuudra Profit Tracker
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra Profit Tracker"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/kuudra/kuudra_profit_tracker.webp"))
                                        .text(Text.literal("Tracks Kuudra profit gains over time."))
                                        .build())
                                .binding(defaults.crimsonIsle.kuudraProfitTracker,
                                        () -> config.crimsonIsle.kuudraProfitTracker,
                                        newVal -> config.crimsonIsle.kuudraProfitTracker = newVal)
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
                                .binding(defaults.crimsonIsle.tabascoIncluded,
                                        () -> config.crimsonIsle.tabascoIncluded,
                                        newVal -> config.crimsonIsle.tabascoIncluded = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Kuudra Alerts
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Kuudra Alerts"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/kuudra/kuudra_alerts.webp"))
                                        .text(Text.literal(
                                                "Alerts for various Kuudra splits and events. Set tracked events using /vc toggles."))
                                        .build())
                                .binding(defaults.crimsonIsle.kuudraAlerts,
                                        () -> config.crimsonIsle.kuudraAlerts,
                                        newVal -> config.crimsonIsle.kuudraAlerts = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Kuudra Crate
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Kuudra Crates"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/kuudra/kuudra_crates.webp"))
                                        .text(Text.literal(
                                                "Displays waypoints for nearby Kuudra crates."))
                                        .build())
                                .binding(defaults.crimsonIsle.kuudraCrates,
                                        () -> config.crimsonIsle.kuudraCrates,
                                        newVal -> config.crimsonIsle.kuudraCrates = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Kuudra Supply Piles
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra Supplies"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/kuudra/kuudra_supply_piles.webp"))
                                        .text(Text.literal(
                                                "Displays waypoints for nearby incomplete supply piles."))
                                        .build())
                                .binding(defaults.crimsonIsle.kuudraSupplyPiles,
                                        () -> config.crimsonIsle.kuudraSupplyPiles,
                                        newVal -> config.crimsonIsle.kuudraSupplyPiles = newVal)
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
                                .binding(defaults.crimsonIsle.kuudraFreshTracking,
                                        () -> config.crimsonIsle.kuudraFreshTracking,
                                        newVal -> config.crimsonIsle.kuudraFreshTracking = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Kuudra HP Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra HP Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/kuudra/kuudra_hp_display.webp"))
                                        .text(Text.literal("Render Kuudra's HP bar on the screen."))
                                        .build())
                                .binding(defaults.crimsonIsle.kuudraHpDisplay,
                                        () -> config.crimsonIsle.kuudraHpDisplay,
                                        newVal -> config.crimsonIsle.kuudraHpDisplay = newVal)
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
                                .binding(defaults.crimsonIsle.kuudraSpawnAlert,
                                        () -> config.crimsonIsle.kuudraSpawnAlert,
                                        newVal -> config.crimsonIsle.kuudraSpawnAlert = newVal)
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
                                .binding(defaults.crimsonIsle.kuudraSplitsTimer,
                                        () -> config.crimsonIsle.kuudraSplitsTimer,
                                        newVal -> config.crimsonIsle.kuudraSplitsTimer = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        .build())

                .build();
    }

    // Crimson Isle Option Group
    @SerialEntry
    public boolean vanquisherWarp = false;

    // Fishing Option Group
    @SerialEntry
    public boolean goldenFishTimer = false;

    @SerialEntry
    public boolean trophyFishDisplay = false;

    // Kuudra Option Group
    @SerialEntry
    public boolean kuudraProfit = false;

    @SerialEntry
    public boolean kuudraProfitTracker = false;

    @SerialEntry
    public boolean tabascoIncluded = false;

    @SerialEntry
    public boolean kuudraAlerts = false;

    @SerialEntry
    public boolean kuudraCrates = false;

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
