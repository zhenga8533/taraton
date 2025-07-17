package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.volcaronitee.taraton.config.TaratonConfig;

/**
 * Mixin to adjust the fire overlay rendering position based on the low fire setting.
 */
@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
    /**
     * Injects a translation into the fire overlay rendering method to adjust its position based on
     * the low fire setting from the configuration.
     * 
     * @param matrices The matrix stack used for rendering.
     * @param vertexConsumers The vertex consumer provider for rendering.
     * @param ci The callback info for the injection.
     */
    @Inject(method = "renderFireOverlay", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"))
    private static void taraton$renderFireOverlay(MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        matrices.translate(0.0, TaratonConfig.getInstance().qol.lowFire, 0.0);
    }
}
