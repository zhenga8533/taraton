package net.volcaronitee.nar.util;

import org.joml.FrustumIntersection;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.volcaronitee.nar.mixin.accessor.BeaconBlockEntityRendererInvoker;
import net.volcaronitee.nar.mixin.accessor.FrustumInvoker;
import net.volcaronitee.nar.mixin.accessor.WorldRendererAccessor;

/**
 * Utility class for rendering-related operations.
 */
public class RenderUtil {
    /**
     * Checks if the given axis-aligned bounding box (AABB) is within the current view frustum.
     * 
     * @param minX The minimum X coordinate of the AABB.
     * @param minY The minimum Y coordinate of the AABB.
     * @param minZ The minimum Z coordinate of the AABB.
     * @param maxX The maximum X coordinate of the AABB.
     * @param maxY The maximum Y coordinate of the AABB.
     * @param maxZ The maximum Z coordinate of the AABB.
     * @return True if the AABB is inside or intersects with the frustum, false otherwise.
     */
    public static boolean isInFrustum(double minX, double minY, double minZ, double maxX,
            double maxY, double maxZ) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.worldRenderer == null) {
            return false;
        }

        // Get the frustum from the world renderer
        Frustum frustum = ((WorldRendererAccessor) client.worldRenderer).getFrustum();
        if (frustum == null) {
            return false;
        }

        // Check if the frustum is inside or intersects with the AABB
        int plane =
                ((FrustumInvoker) frustum).invokeIntersectAab(minX, minY, minZ, maxX, maxY, maxZ);
        return plane == FrustumIntersection.INSIDE || plane == FrustumIntersection.INTERSECT;
    }

    /**
     * Checks if the given axis-aligned bounding box (AABB) is within the current view frustum.
     * 
     * @param box The axis-aligned bounding box to check.
     * @return True if the AABB is inside or intersects with the frustum, false otherwise.
     */
    public static boolean isInFrustum(Box box) {
        return isInFrustum(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    /**
     * Checks if there is a clear line of sight from the player's eye position to the target entity.
     * 
     * @param targetEntity The target entity to check for line of sight.
     * @return True if there is a clear line of sight to the target entity, false otherwise.
     */
    public static boolean hasLineOfSight(Entity targetEntity) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return false;
        }

        Vec3d playerEyePos = client.player.getEyePos();
        Box targetBoundingBox = targetEntity.getBoundingBox();

        // Define the corners of the target entity's bounding box
        Vec3d[] corners = new Vec3d[] {
                new Vec3d(targetBoundingBox.minX, targetBoundingBox.minY, targetBoundingBox.minZ),
                new Vec3d(targetBoundingBox.maxX, targetBoundingBox.minY, targetBoundingBox.minZ),
                new Vec3d(targetBoundingBox.minX, targetBoundingBox.maxY, targetBoundingBox.minZ),
                new Vec3d(targetBoundingBox.maxX, targetBoundingBox.maxY, targetBoundingBox.minZ),
                new Vec3d(targetBoundingBox.minX, targetBoundingBox.minY, targetBoundingBox.maxZ),
                new Vec3d(targetBoundingBox.maxX, targetBoundingBox.minY, targetBoundingBox.maxZ),
                new Vec3d(targetBoundingBox.minX, targetBoundingBox.maxY, targetBoundingBox.maxZ),
                new Vec3d(targetBoundingBox.maxX, targetBoundingBox.maxY, targetBoundingBox.maxZ)};

        // Check each point in the bounding box for line of sight
        for (Vec3d point : corners) {
            RaycastContext blockRaycastContext =
                    new RaycastContext(playerEyePos, point, RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE, client.player);
            BlockHitResult blockHit = client.world.raycast(blockRaycastContext);

            // Check if the raycast hit a block
            if (blockHit.getType() == HitResult.Type.MISS) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the target entity is visible to the player by verifying if it is within the view
     * frustum and has a clear line of sight.
     * 
     * @param targetEntity The target entity to check for visibility.
     * @return True if the entity is visible, false otherwise.
     */
    public static boolean isVisible(Entity targetEntity) {
        if (!isInFrustum(targetEntity.getBoundingBox())) {
            return false;
        }

        return hasLineOfSight(targetEntity);
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
