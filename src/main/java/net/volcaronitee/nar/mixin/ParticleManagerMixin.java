package net.volcaronitee.nar.mixin;

import java.util.Queue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.volcaronitee.nar.config.NarConfig;

/**
 * Mixin for the ParticleManager to hide all particles based on configuration.
 */
@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    /**
     * Injects into the renderParticles method of ParticleManager to cancel rendering of all
     * particles if the configuration option to hide all particles is enabled.
     * 
     * @param camera The camera used for rendering.
     * @param tickProgress The tick progress for rendering.
     * @param vertexConsumers The vertex consumer provider for rendering particles.
     * @param sheet The particle texture sheet used for rendering.
     * @param particles The queue of particles to be rendered.
     * @param ci The callback info to cancel the rendering if needed.
     */
    @Inject(method = "renderParticles(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/particle/ParticleTextureSheet;Ljava/util/Queue;)V",
            at = @At("HEAD"), cancellable = true)
    private static void nar$renderParticles(Camera camera, float tickProgress,
            VertexConsumerProvider.Immediate vertexConsumers, ParticleTextureSheet sheet,
            Queue<Particle> particles, CallbackInfo ci) {
        if (!NarConfig.getHandler().qol.hideAllParticles) {
            return;
        }

        ci.cancel();
    }
}
