package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.util.FeatureUtil;

/**
 * Mixin for LivingEntity to modify the hand swing duration based on configuration settings.
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    /**
     * Modifies the hand swing duration based on the configuration setting.
     * 
     * @param cir The callback info for the method, allowing modification of the return value.
     */
    @Inject(method = "getHandSwingDuration", at = @At("RETURN"), cancellable = true)
    private void taraton$modifyHandSwingDuration(CallbackInfoReturnable<Integer> cir) {
        if (!FeatureUtil.isEnabled(true)) {
            return;
        }

        double speedMultiplier = TaratonConfig.getInstance().general.heldItemSwingSpeed;
        LivingEntity entity = (LivingEntity) (Object) this;

        // Only apply to the main player to avoid affecting other entities
        if (speedMultiplier != 1.0 && entity == MinecraftClient.getInstance().player) {
            int originalDuration = cir.getReturnValue();
            double calculatedDuration = Math.max(1.0, originalDuration / speedMultiplier);
            int modifiedDuration = (int) Math.round(calculatedDuration);
            cir.setReturnValue(modifiedDuration);
        }
    }
}
