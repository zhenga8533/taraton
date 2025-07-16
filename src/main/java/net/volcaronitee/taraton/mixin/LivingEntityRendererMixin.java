package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.volcaronitee.taraton.feature.general.PlayerResize;

/**
 * Mixin for LivingEntityRenderer to apply player resizing transformations.
 */
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity> {
    /**
     * Injects custom transformations into the renderModel method
     * 
     * @param entity The living entity being rendered.
     * @param f The partial tick time for rendering.
     * @param g The animation progress for rendering.
     * @param matrices The matrix stack for rendering transformations.
     * @param vertexConsumers The vertex consumer provider for rendering.
     * @param light The light level for rendering the entity.
     * @param ci The callback info for the method injection.
     */
    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;renderModel(Lnet/minecraft/entity/LivingEntity;FFFFFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private void onRenderModel(T entity, float f, float g, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {

        if (entity instanceof AbstractClientPlayerEntity player) {
            String username = player.getGameProfile().getName();
            Float scale = PlayerResize.getInstance().getPlayerResize(username);

            if (scale != null && scale != 1.0f) {
                matrices.scale(scale, scale, scale);
            }
        }
    }

    /**
     * Modifies the shadow radius of the entity based on player resize settings.
     * 
     * @param originalRadius The original shadow radius of the entity.
     * @param entity The living entity being rendered.
     * @return The modified shadow radius based on player resize settings.
     */
    @ModifyVariable(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "STORE"), ordinal = 0)
    private float modifyShadowRadius(float originalRadius, T entity) {
        if (entity instanceof AbstractClientPlayerEntity player) {
            String username = player.getGameProfile().getName();
            Float scale = PlayerResize.getInstance().getPlayerResize(username);

            if (scale != null && scale != 1.0f) {
                return originalRadius * scale;
            }
        }
        return originalRadius;
    }
}
