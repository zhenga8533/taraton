package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.volcaronitee.taraton.feature.general.HeldItemTransformer;

/**
 * Mixin for HeldItemRenderer to apply custom transformations to held items.
 */
@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    /**
     * Injects into the renderFirstPersonItem method to apply custom transformations
     * 
     * @param player The player entity.
     * @param tickDelta The tick delta for rendering.
     * @param pitch The pitch of the camera.
     * @param hand The hand being rendered (main or off).
     * @param swingProgress The swing progress of the item.
     * @param item The item being rendered in the hand.
     * @param equipProgress The equip progress of the item.
     * @param matrices The matrix stack for rendering transformations.
     * @param vertexConsumers The vertex consumer provider for rendering.
     * @param light The light level at the position of the item.
     * @param ci The callback info for the method injection.
     */
    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.AFTER))
    private void onRenderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta,
            float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            CallbackInfo ci) {
        HeldItemTransformer.INSTANCE.applyTransformations(matrices);
    }
}
