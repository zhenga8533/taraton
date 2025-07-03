package net.volcaronitee.nar.util;

import org.joml.FrustumIntersection;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
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

    /**
     * Renders a beacon beam at the specified coordinates with the given color components.
     * 
     * @param context The rendering context containing the necessary matrices and world information.
     * @param x The X coordinate of the beacon beam's position.
     * @param y The Y coordinate of the beacon beam's position.
     * @param z The Z coordinate of the beacon beam's position.
     * @param colorComponents An array of three float values representing the RGB color components
     */
    public static void renderBeaconBeam(WorldRenderContext context, double x, double y, double z,
            float[] colorComponents) {
        MinecraftClient client = MinecraftClient.getInstance();
        MatrixStack matrices = context.matrixStack();
        Vec3d camera = context.camera().getPos();

        matrices.push();
        matrices.translate(x - camera.getX(), y - camera.getY(), z - camera.getZ());
        matrices.translate(-0.5, 0, -0.5);

        float length = (float) camera.subtract(new Vec3d(x, y, z)).horizontalLength();
        float scale = client.player != null && client.player.isUsingSpyglass() ? 1.0f
                : Math.max(1.0f, length / 96.0f);

        BeaconBlockEntityRendererInvoker.renderBeam(matrices, context.consumers(),
                context.tickCounter().getTickProgress(true), scale, context.world().getTime(), 0,
                context.world().getHeight(), ColorHelper.fromFloats(1f, colorComponents[0],
                        colorComponents[1], colorComponents[2]));

        matrices.pop();
    }

    /**
     * Renders a beacon beam at the specified block position with the given color components.
     * 
     * @param context The rendering context containing the necessary matrices and world information.
     * @param pos The block position of the beacon beam.
     * @param colorComponents An array of three float values representing the RGB color components.
     */
    public static void renderBeaconBeam(WorldRenderContext context, BlockPos pos,
            float[] colorComponents) {
        renderBeaconBeam(context, pos.getX(), pos.getY(), pos.getZ(), colorComponents);
    }

    /**
     * Renders a beacon beam at the specified entity's position with the given color components.
     * 
     * @param context The rendering context containing the necessary matrices and world information.
     * @param entity The entity at whose position the beacon beam will be rendered.
     * @param colorComponents An array of three float values representing the RGB color components.
     * @param tickProgress The progress of the current tick for interpolation.
     */
    public static void renderBeaconBeam(WorldRenderContext context, Entity entity,
            float[] colorComponents, float tickProgress) {
        double interpolatedX =
                entity.lastRenderX + (entity.getX() - entity.lastRenderX) * tickProgress;
        double interpolatedY =
                entity.lastRenderY + (entity.getY() - entity.lastRenderY) * tickProgress;
        double interpolatedZ =
                entity.lastRenderZ + (entity.getZ() - entity.lastRenderZ) * tickProgress;

        renderBeaconBeam(context, interpolatedX, interpolatedY, interpolatedZ, colorComponents);
    }
}
