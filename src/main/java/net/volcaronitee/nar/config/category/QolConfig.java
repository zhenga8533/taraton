package net.volcaronitee.nar.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.config.NarConfig;

/**
 * Configuration class for Quality of Life (QOL) features in NotARat.
 */
public class QolConfig {
    /**
     * Creates a new {@link ConfigCategory} for the QOL features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the QOL features.
     */
    public static ConfigCategory create(NarConfig defaults, NarConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("QOL"))

                // Quality of Life Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Quality of Life"))

                        // No Mouse Reset
                        .option(Option.<Boolean>createBuilder().name(Text.literal("No Mouse Reset"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/no_mouse_reset.webp"))
                                        .text(Text.literal(
                                                "Prevents the mouse from being reset when navigating menus."))
                                        .build())
                                .binding(defaults.qol.noMouseReset, () -> config.qol.noMouseReset,
                                        newVal -> config.qol.noMouseReset = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // Remove Selfie Mode
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Remove Selfie Mode"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/remove_selfie_mode.webp"))
                                        .text(Text.literal(
                                                "Removes the frontal view camera view when toggling perspective."))
                                        .build())
                                .binding(defaults.qol.removeSelfieMode,
                                        () -> config.qol.removeSelfieMode,
                                        newVal -> config.qol.removeSelfieMode = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        .build())

                // Visibility Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Visibility"))

                        // Hide All Particles
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Hide All Particles"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/hide_all_particles.webp"))
                                        .text(Text.literal("Hides all particles in the game."))
                                        .build())
                                .binding(defaults.qol.hideAllParticles,
                                        () -> config.qol.hideAllParticles,
                                        newVal -> config.qol.hideAllParticles = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Hide Far Entities
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Hide Far Entities"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/hide_far_entities.webp"))
                                        .text(Text.literal(
                                                "Sets the maximum distance an entity can be before it is hidden."))
                                        .build())
                                .binding(defaults.qol.hideFarEntities,
                                        () -> config.qol.hideFarEntities,
                                        newVal -> config.qol.hideFarEntities = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 128).step(4))
                                .build())

                        // TODO: Hide Close Players
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Hide Close Players"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/hide_close_players.webp"))
                                        .text(Text.literal(
                                                "Sets the minimum distance a player can be before they are hidden."))
                                        .build())
                                .binding(defaults.qol.hideClosePlayers,
                                        () -> config.qol.hideClosePlayers,
                                        newVal -> config.qol.hideClosePlayers = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 128).step(4))
                                .build())

                        // Hide Potion Effects
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Hide Potion Effects"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/qol/hide_potion_effects.webp"))
                                        .text(Text.literal(
                                                "Hides potion effects from the HUD and inventory."))
                                        .build())
                                .binding(defaults.qol.hidePotionEffects,
                                        () -> config.qol.hidePotionEffects,
                                        newVal -> config.qol.hidePotionEffects = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // Hurt Cam Intensity
                        .option(Option.<Float>createBuilder()
                                .name(Text.literal("Hurt Cam Intensity"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/hurt_cam_intensity.webp"))
                                        .text(Text.literal(
                                                "Sets the intensity of the hurt camera effect."))
                                        .build())
                                .binding(defaults.qol.hurtCamIntensity,
                                        () -> config.qol.hurtCamIntensity,
                                        newVal -> config.qol.hurtCamIntensity = newVal)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                        .range(0.0f, 2.0f).step(0.1f))
                                .build())

                        // Low Fire
                        .option(Option.<Double>createBuilder().name(Text.literal("Low Fire"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/general/low_fire.webp"))
                                        .text(Text.literal("Sets the height of the fire overlay."))
                                        .build())
                                .binding(defaults.qol.lowFire, () -> config.qol.lowFire,
                                        newVal -> config.qol.lowFire = newVal)
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(-0.5, 0.4).step(0.01))
                                .build())

                        .build())

                .build();
    }


    // Quality of Life Option Group
    @SerialEntry
    public boolean noMouseReset = true;

    @SerialEntry
    public boolean removeSelfieMode = false;

    // Visibility Option Group
    @SerialEntry
    public boolean hideAllParticles = false;

    @SerialEntry
    public int hideClosePlayers = 0;

    @SerialEntry
    public int hideFarEntities = 0;

    @SerialEntry
    public boolean hidePotionEffects = false;

    @SerialEntry
    public float hurtCamIntensity = 1.0f;

    @SerialEntry
    public double lowFire = 0.0;

}
