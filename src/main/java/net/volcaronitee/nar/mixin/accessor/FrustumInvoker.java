package net.volcaronitee.nar.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.client.render.Frustum;

/**
 * This mixin interface allows access to the private method
 */
@Mixin(Frustum.class)
public interface FrustumInvoker {
    /**
     * Invokes the private method `intersectAab` from `Frustum`.
     * 
     * @param minX The minimum X coordinate of the axis-aligned bounding box (AABB).
     * @param minY The minimum Y coordinate of the axis-aligned bounding box (AABB).
     * @param minZ The minimum Z coordinate of the axis-aligned bounding box (AABB).
     * @param maxX The maximum X coordinate of the axis-aligned bounding box (AABB).
     * @param maxY The maximum Y coordinate of the axis-aligned bounding box (AABB).
     * @param maxZ The maximum Z coordinate of the axis-aligned bounding box (AABB).
     * @return An integer indicating whether the AABB intersects with the frustum.
     */
    @Invoker
    int invokeIntersectAab(double minX, double minY, double minZ, double maxX, double maxY,
            double maxZ);
}
