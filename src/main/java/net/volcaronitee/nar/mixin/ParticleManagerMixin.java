package net.volcaronitee.nar.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.volcaronitee.nar.config.NarConfig;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @Inject(method = "renderParticles", at = @At("HEAD"), cancellable = true)
    private void volcclient$hideAllParticles(MatrixStack matrices,
            VertexConsumerProvider.Immediate vertexConsumers, Camera camera, float tickDelta,
            CallbackInfo ci) {
        if (!NarConfig.getHandler().general.hideAllParticles) {
            return;
        }

        ci.cancel();
    }
}
