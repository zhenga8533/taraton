package net.volcaronitee.nar.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.util.ConfigUtil;

/**
 * Configuration for the Rift features in NotARat.
 */
public class RiftConfig {
    /**
     * Creates a new {@link ConfigCategory} for the Rift features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the Rift features.
     */
    public static ConfigCategory create(ConfigUtil defaults, ConfigUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal("Rift"))

                // Rift Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Rift Tasks"))

                        // TODO: DDR Helper
                        .option(Option.<Boolean>createBuilder().name(Text.literal("DDR Helper"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/rift/ddr_helper.webp"))
                                        .text(Text.literal(
                                                "Replaces Dance Room titles with custom ones."))
                                        .build())
                                .binding(defaults.rift.ddrHelper, () -> config.rift.ddrHelper,
                                        newVal -> config.rift.ddrHelper = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Enigma Soul Waypoints
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Enigma Soul Waypoints"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/rift/enigma_soul_waypoints.webp"))
                                        .text(Text.literal(
                                                "Renders waypoints of Enigma Soul locations. Sets maximum distance for waypoints to be rendered."))
                                        .build())
                                .binding(defaults.rift.enigmaSoulWaypoints,
                                        () -> config.rift.enigmaSoulWaypoints,
                                        newVal -> config.rift.enigmaSoulWaypoints = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 128).step(4))
                                .build())

                        // TODO: Montezuma Soul Waypoints
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Montezuma Soul Waypoints"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/rift/montezuma_soul_waypoints.webp"))
                                        .text(Text.literal(
                                                "Renders waypoints for nearby discord kittens. Sets maximum distance for waypoints to be rendered."))
                                        .build())
                                .binding(defaults.rift.montezumaSoulWaypoints,
                                        () -> config.rift.montezumaSoulWaypoints,
                                        newVal -> config.rift.montezumaSoulWaypoints = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 128).step(4))
                                .build())

                        .build())

                // Vampire Slayer Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Vampire Slayer"))

                        // TODO: Announce Mania Phase
                        .option(Option.<AnnounceManiaPhase>createBuilder()
                                .name(Text.literal("Announce Mania Phase"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/rift/announce_mania_phase.webp"))
                                        .text(Text.literal(
                                                "Sends a chat message on start of a mania phase."))
                                        .build())
                                .binding(defaults.rift.announceManiaPhase,
                                        () -> config.rift.announceManiaPhase,
                                        newVal -> config.rift.announceManiaPhase = newVal)
                                .controller(ConfigUtil::createEnumController).build())

                        // TODO: Effigy Waypoint
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Effigy Waypoint"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/rift/effigy_waypoint.webp"))
                                        .text(Text.literal(
                                                "Renders a waypoint on inactive Blood Effigies."))
                                        .build())
                                .binding(defaults.rift.effigyWaypoint,
                                        () -> config.rift.effigyWaypoint,
                                        newVal -> config.rift.effigyWaypoint = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Vampire Attack Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Vampire Attack Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/rift/vampire_attack_display.webp"))
                                        .text(Text.literal(
                                                "Displays time of Mania, Twinclaws, and Ichor attacks."))
                                        .build())
                                .binding(defaults.rift.vampireAttackDisplay,
                                        () -> config.rift.vampireAttackDisplay,
                                        newVal -> config.rift.vampireAttackDisplay = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                .build();
    }

    // Rift Tasks Option Group
    @SerialEntry
    public boolean ddrHelper = false;

    @SerialEntry
    public int enigmaSoulWaypoints = 0;

    @SerialEntry
    public int montezumaSoulWaypoints = 0;

    // Vampire Slayer Option Group
    @SerialEntry
    public AnnounceManiaPhase announceManiaPhase = AnnounceManiaPhase.OFF;

    public enum AnnounceManiaPhase implements NameableEnum {
        OFF, ALL, PARTY;

        @Override
        public Text getDisplayName() {
            return switch (this) {
                case OFF -> Text.literal("Disabled");
                case ALL -> Text.literal("All Chat");
                case PARTY -> Text.literal("Party Chat");
            };
        }
    }

    @SerialEntry
    public boolean effigyWaypoint = false;

    @SerialEntry
    public boolean vampireAttackDisplay = false;
}
