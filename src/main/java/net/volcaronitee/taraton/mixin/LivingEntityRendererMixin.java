package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.volcaronitee.taraton.feature.general.PlayerScale;

/**
 * Mixin for LivingEntityRenderer to apply player resizing transformations.
 */
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState> {
    /**
     * Injects into the updateRenderState method to apply player resizing.
     * 
     * @param entity The entity being rendered.
     * @param state The render state of the entity.
     * @param tickDelta The tick delta for rendering.
     * @param ci The callback info for the method injection.
     */
    @Inject(method = "updateRenderState", at = @At("TAIL"))
    private void taraton$updateRenderState(T entity, S state, float tickDelta, CallbackInfo ci) {
        if (entity instanceof AbstractClientPlayerEntity player) {
            String username = player.getGameProfile().getName();
            state.baseScale *= PlayerScale.getInstance().getPlayerScale(username);
        }
    }
}
