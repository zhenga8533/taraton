package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.volcclient.VolcClient;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.ConfigUtil.AnnounceWP;

/**
 * Configuration for the Crimson Isle features in VolcClient.
 */
public class CrimsonIsleConfig {
    /**
     * Creates a new {@link ConfigCategory} for the Crimson Isle features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the Crimson Isle features.
     */
    public static ConfigCategory create(ConfigUtil defaults, ConfigUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal("Crimson Isle"))

                // Fishing Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Fishing"))

                        // TODO: Mythic Lava Creature Announce
                        .option(Option.<AnnounceWP>createBuilder()
                                .name(Text.literal("Mythic Lava Creature Announce"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/crimson_isle/mythic_lava_creature_announce.webp"))
                                        .text(Text.literal(
                                                "Sends a chat message on mythic lava sea creature spawn."))
                                        .build())
                                .binding(defaults.crimsonIsle.mythicLavaCreatureAnnounce,
                                        () -> config.crimsonIsle.mythicLavaCreatureAnnounce,
                                        newVal -> config.crimsonIsle.mythicLavaCreatureAnnounce =
                                                newVal)
                                .controller(ConfigUtil::createEnumController).build())

                        // TODO: Golden Fish Timer
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Golden Fish Timer"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/crimson_isle/golden_fish_timer.webp"))
                                        .text(Text.literal(
                                                "Displays a timer for the golden trophy fish on the screen."))
                                        .build())
                                .binding(defaults.crimsonIsle.goldenFishTimer,
                                        () -> config.crimsonIsle.goldenFishTimer,
                                        newVal -> config.crimsonIsle.goldenFishTimer = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Trophy Fisher Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Trophy Fisher Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/crimson_isle/trophy_fisher_display.webp"))
                                        .text(Text.literal(
                                                "Displays the session trophy fishing progress on the screen."))
                                        .build())
                                .binding(defaults.crimsonIsle.trophyFisherDisplay,
                                        () -> config.crimsonIsle.trophyFisherDisplay,
                                        newVal -> config.crimsonIsle.trophyFisherDisplay = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                // Vanquisher Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Vanquisher"))

                        // TODO: Vanquisher Announce
                        .option(Option.<AnnounceWP>createBuilder()
                                .name(Text.literal("Vanquisher Announce"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/crimson_isle/vanquisher_announce.webp"))
                                        .text(Text.literal(
                                                "Sends a chat message on vanquisher spawn."))
                                        .build())
                                .binding(defaults.crimsonIsle.vanquisherAnnounce,
                                        () -> config.crimsonIsle.vanquisherAnnounce,
                                        newVal -> config.crimsonIsle.vanquisherAnnounce = newVal)
                                .controller(ConfigUtil::createEnumController).build())

                        // TODO: Vanquisher Warp
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Vanquisher Warp"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/crimson_isle/vanquisher_warp.webp"))
                                        .text(Text.literal(
                                                "Warps vanquisher swap party to your lobby when you spawn a vanquisher. Set the players in your party using /vc vanq."))
                                        .build())
                                .binding(defaults.crimsonIsle.vanquisherWarp,
                                        () -> config.crimsonIsle.vanquisherWarp,
                                        newVal -> config.crimsonIsle.vanquisherWarp = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

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
    public boolean vanquisherWarp = false;
}
