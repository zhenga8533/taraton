package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.VolcClientConfig;
import net.volcaronitee.volcclient.config.VolcClientConfig.AnnounceWP;

public class CrimsonIslesConfig {
    public static ConfigCategory create(VolcClientConfig defaults, VolcClientConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("Crimson Isles"))

                // Fishing Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Fishing"))

                        // Mythic Lava Creature Announce
                        .option(Option.<AnnounceWP>createBuilder()
                                .name(Text.literal("Mythic Lava Creature Announce"))
                                .description(OptionDescription.of(Text.literal(
                                        "Sends a chat message on mythic lava sea creature spawn.")))
                                .binding(defaults.crimsonIsles.mythicLavaCreatureAnnounce,
                                        () -> config.crimsonIsles.mythicLavaCreatureAnnounce,
                                        newVal -> config.crimsonIsles.mythicLavaCreatureAnnounce =
                                                newVal)
                                .controller(VolcClientConfig::createEnumController).build())

                        // Mythic Lava Creature Highlight
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Mythic Lava Creature Highlight"))
                                .description(OptionDescription.of(Text.literal(
                                        "Highlights nearby mythic lava sea creatures. This includes mythic lava sea creatures spawned by other players.")))
                                .binding(defaults.crimsonIsles.mythicLavaCreatureHighlight,
                                        () -> config.crimsonIsles.mythicLavaCreatureHighlight,
                                        newVal -> config.crimsonIsles.mythicLavaCreatureHighlight =
                                                newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Golden Fish Timer
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Golden Fish Timer"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays a timer for the golden trophy fish on the screen.")))
                                .binding(defaults.crimsonIsles.goldenFishTimer,
                                        () -> config.crimsonIsles.goldenFishTimer,
                                        newVal -> config.crimsonIsles.goldenFishTimer = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Trophy Fisher Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Trophy Fisher Display"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays the session trophy fishing progress on the screen.")))
                                .binding(defaults.crimsonIsles.trophyFisherDisplay,
                                        () -> config.crimsonIsles.trophyFisherDisplay,
                                        newVal -> config.crimsonIsles.trophyFisherDisplay = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        .build())

                // Vanquisher Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Vanquisher"))

                        // Vanquisher Announce
                        .option(Option.<AnnounceWP>createBuilder()
                                .name(Text.literal("Vanquisher Announce"))
                                .description(OptionDescription.of(
                                        Text.literal("Sends a chat message on vanquisher spawn.")))
                                .binding(defaults.crimsonIsles.vanquisherAnnounce,
                                        () -> config.crimsonIsles.vanquisherAnnounce,
                                        newVal -> config.crimsonIsles.vanquisherAnnounce = newVal)
                                .controller(VolcClientConfig::createEnumController).build())

                        // Vanquisher Highlight
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Vanquisher Highlight"))
                                .description(OptionDescription.of(Text.literal(
                                        "Highlights nearby vanquishers. This includes vanquishers spawned by other players.")))
                                .binding(defaults.crimsonIsles.vanquisherHighlight,
                                        () -> config.crimsonIsles.vanquisherHighlight,
                                        newVal -> config.crimsonIsles.vanquisherHighlight = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Vanquisher Warp
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Vanquisher Warp"))
                                .description(OptionDescription.of(Text.literal(
                                        "Warps vanquisher swap party to your lobby when you spawn a vanquisher. Set the players in your party using /vc vanq.")))
                                .binding(defaults.crimsonIsles.vanquisherWarp,
                                        () -> config.crimsonIsles.vanquisherWarp,
                                        newVal -> config.crimsonIsles.vanquisherWarp = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        .build())

                .build();
    }

    // Fishing Option Group
    @SerialEntry
    public AnnounceWP mythicLavaCreatureAnnounce = AnnounceWP.OFF;

    @SerialEntry
    public boolean mythicLavaCreatureHighlight = false;

    @SerialEntry
    public boolean goldenFishTimer = false;

    @SerialEntry
    public boolean trophyFisherDisplay = false;

    // Vanquisher
    @SerialEntry
    public AnnounceWP vanquisherAnnounce = AnnounceWP.OFF;

    @SerialEntry
    public boolean vanquisherHighlight = false;

    @SerialEntry
    public boolean vanquisherWarp = false;
}
