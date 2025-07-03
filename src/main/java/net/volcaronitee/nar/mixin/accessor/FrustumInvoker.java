package net.volcaronitee.nar.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.client.render.Frustum;

/**
 * This mixin interface allows access to the private method
 */
@Mixin(Frustum.class)
public interface FrustumInvoker {
    @Invoker
    int invokeIntersectAab(double minX, double minY, double minZ, double maxX, double maxY,
            double maxZ);
}
