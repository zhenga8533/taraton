package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.VolcClientConfig;

public class KuudraConfig {
    public static ConfigCategory create(VolcClientConfig defaults, VolcClientConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("Kuudra"))

                // Kuudra Profit Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Kuudra Profit"))

                        // Kuudra Profit
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Kuudra Profit"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays profit of any opened Kuudra chest on the screen.")))
                                .binding(defaults.kuudra.kuudraProfit,
                                        () -> config.kuudra.kuudraProfit,
                                        newVal -> config.kuudra.kuudraProfit = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Kuudra Profit Tracker
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra Profit Tracker"))
                                .description(OptionDescription
                                        .of(Text.literal("Tracks Kuudra profit gains over time.")))
                                .binding(defaults.kuudra.kuudraProfitTracker,
                                        () -> config.kuudra.kuudraProfitTracker,
                                        newVal -> config.kuudra.kuudraProfitTracker = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Tabasco Included
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Tabasco Included"))
                                .description(OptionDescription.of(Text.literal(
                                        "Include Tabasco crafting in the profit calculations.")))
                                .binding(defaults.kuudra.tabascoIncluded,
                                        () -> config.kuudra.tabascoIncluded,
                                        newVal -> config.kuudra.tabascoIncluded = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        .build())

                // Kuudra Run Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Kuudra Run"))

                        // Kuudra Alerts
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Kuudra Alerts"))
                                .description(OptionDescription.of(Text.literal(
                                        "Alerts for various Kuudra splits and events. Set tracked events using /vc toggles.")))
                                .binding(defaults.kuudra.kuudraAlerts,
                                        () -> config.kuudra.kuudraAlerts,
                                        newVal -> config.kuudra.kuudraAlerts = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Kuudra Crate Waypoints
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra Crate Waypoints"))
                                .description(OptionDescription.of(Text
                                        .literal("Displays waypoints for nearby Kuudra crates.")))
                                .binding(defaults.kuudra.kuudraCrateWaypoints,
                                        () -> config.kuudra.kuudraCrateWaypoints,
                                        newVal -> config.kuudra.kuudraCrateWaypoints = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Kuudra Fresh Tracking
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra Fresh Tracking"))
                                .description(OptionDescription.of(Text.literal(
                                        "Sends chat message to party when fresh tools activates. Also tracks other players' fresh tools when announced.")))
                                .binding(defaults.kuudra.kuudraFreshTracking,
                                        () -> config.kuudra.kuudraFreshTracking,
                                        newVal -> config.kuudra.kuudraFreshTracking = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Kuudra HP Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra HP Display"))
                                .description(OptionDescription
                                        .of(Text.literal("Render Kuudra's HP bar on the screen.")))
                                .binding(defaults.kuudra.kuudraHpDisplay,
                                        () -> config.kuudra.kuudraHpDisplay,
                                        newVal -> config.kuudra.kuudraHpDisplay = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Kuudra Spawn Alert
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra Spawn Alert"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays a title for where Kuudra spawns in P4. UAYOR: Uses ESP!")))
                                .binding(defaults.kuudra.kuudraSpawnAlert,
                                        () -> config.kuudra.kuudraSpawnAlert,
                                        newVal -> config.kuudra.kuudraSpawnAlert = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Kuudra Splits Timer
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra Splits Timer"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays time taken for each Kuudra split on the screen. See Kuudra split records using /vc ks.")))
                                .binding(defaults.kuudra.kuudraSplitsTimer,
                                        () -> config.kuudra.kuudraSplitsTimer,
                                        newVal -> config.kuudra.kuudraSplitsTimer = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Kuudra Supply Piles
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Kuudra Supply Piles"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays waypoints for nearby incomplete supply piles.")))
                                .binding(defaults.kuudra.kuudraSupplyPiles,
                                        () -> config.kuudra.kuudraSupplyPiles,
                                        newVal -> config.kuudra.kuudraSupplyPiles = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

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
