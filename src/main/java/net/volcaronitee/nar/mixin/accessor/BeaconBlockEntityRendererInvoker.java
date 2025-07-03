package net.volcaronitee.nar.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Mixin interface for invoking the private method `renderBeam` from
 */
@Mixin(BeaconBlockEntityRenderer.class)
public interface BeaconBlockEntityRendererInvoker {
    /**
     * Invokes the private method `renderBeam` from `BeaconBlockEntityRenderer`.
     * 
     * @param matrices The matrix stack to use for rendering.
     * @param vertexConsumers The vertex consumer provider for rendering.
     * @param tickDelta The tick delta for rendering.
     * @param scale The scale factor for rendering the beam.
     * @param worldTime The current world time, used for animation.
     * @param yOffset The Y offset for the beam rendering, typically used to adjust the beam's
     *        position.
     * @param maxY The maximum Y coordinate for the beam rendering.
     * @param color The color of the beam, represented as an integer.
     */
    @Invoker("renderBeam")
    static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            float tickDelta, float scale, long worldTime, int yOffset, int maxY, int color) {
        throw new UnsupportedOperationException();
    }
}
