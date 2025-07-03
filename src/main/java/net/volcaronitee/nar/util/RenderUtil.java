package net.volcaronitee.nar.util;

import org.joml.FrustumIntersection;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.volcaronitee.nar.mixin.accessor.BeaconBlockEntityRendererInvoker;
import net.volcaronitee.nar.mixin.accessor.FrustumInvoker;
import net.volcaronitee.nar.mixin.accessor.WorldRendererAccessor;

/**
 * Utility class for rendering operations, particularly for beacon beams and visibility checks.
 */
public class RenderUtil {
    /**
     * The alpha value for the beacon beam rendering.
     * 
     * @param minX The minimum X coordinate of the bounding box.
     * @param minY The minimum Y coordinate of the bounding box.
     * @param minZ The minimum Z coordinate of the bounding box.
     * @param maxX The maximum X coordinate of the bounding box.
     * @param maxY The maximum Y coordinate of the bounding box.
     * @param maxZ The maximum Z coordinate of the bounding box.
     * @return True if the bounding box is visible in the current frustum, false otherwise.
     */
    public static boolean isVisible(double minX, double minY, double minZ, double maxX, double maxY,
            double maxZ) {
        int plane = ((FrustumInvoker) ((WorldRendererAccessor) MinecraftClient
                .getInstance().worldRenderer).getFrustum()).invokeIntersectAab(minX, minY, minZ,
                        maxX, maxY, maxZ);

        return plane == FrustumIntersection.INSIDE || plane == FrustumIntersection.INTERSECT;
    }

    public static void renderBeaconBeam(WorldRenderContext context, BlockPos pos,
            float[] colorComponents) {
        renderFilled(context, pos, colorComponents, 1, true);
        MinecraftClient client = MinecraftClient.getInstance();
        MatrixStack matrices = context.matrixStack();
        Vec3d camera = context.camera().getPos();

        matrices.push();
        matrices.translate(pos.getX() - camera.getX(), pos.getY() - camera.getY(),
                pos.getZ() - camera.getZ());

        float length = (float) camera.subtract(pos.toCenterPos()).horizontalLength();
        float scale = client.player != null && client.player.isUsingSpyglass() ? 1.0f
                : Math.max(1.0f, length / 96.0f);

        BeaconBlockEntityRendererInvoker.renderBeam(matrices, context.consumers(),
                context.tickCounter().getTickProgress(true), scale, context.world().getTime(), 0,
                context.world().getHeight(), ColorHelper.fromFloats(1f, colorComponents[0],
                        colorComponents[1], colorComponents[2]));

        matrices.pop();
    }

    public static void renderFilled(WorldRenderContext context, BlockPos pos,
            float[] colorComponents, float alpha, boolean throughWalls) {
        renderFilled(context, pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1,
                pos.getZ() + 1, colorComponents, alpha, throughWalls);
    }

    public static void renderFilled(WorldRenderContext context, double minX, double minY,
            double minZ, double maxX, double maxY, double maxZ, float[] colorComponents,
            float alpha, boolean throughWalls) {
        if (isVisible(minX, minY, minZ, maxX, maxY, maxZ)) {
            renderFilledInternal(context, minX, minY, minZ, maxX, maxY, maxZ, colorComponents,
                    alpha, throughWalls);
        }
    }

    private static void renderFilledInternal(WorldRenderContext context, double minX, double minY,
            double minZ, double maxX, double maxY, double maxZ, float[] colorComponents,
            float alpha, boolean throughWalls) {
        MatrixStack matrices = context.matrixStack();
        Vec3d camera = context.camera().getPos();

        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getDebugQuads());

        VertexRendering.drawFilledBox(matrices, buffer, minX, minY, minZ, maxX, maxY, maxZ,
                colorComponents[0], colorComponents[1], colorComponents[2], alpha);

        matrices.pop();
    }
}
