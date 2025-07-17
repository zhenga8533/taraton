package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import net.minecraft.client.render.GameRenderer;
import net.volcaronitee.taraton.config.TaratonConfig;

/**
 * Mixin for modifying the GameRenderer class to change the bobbing effect when the player is hurt.
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    /**
     * Changes the intensity of the bobbing effect when the player is hurt.
     * 
     * @param value The original bobbing intensity.
     * @return The modified bobbing intensity.
     */
    @ModifyArg(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;"),
            method = "tiltViewWhenHurt", require = 4)
    public float taraton$changeBobIntensity(float value) {
        return TaratonConfig.getInstance().qol.hurtCamIntensity * value;
    }
}
