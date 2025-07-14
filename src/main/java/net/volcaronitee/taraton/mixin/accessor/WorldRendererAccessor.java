package net.volcaronitee.taraton.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;

/**
 * Mixin interface for accessing the Frustum in WorldRenderer.
 */
@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {
    /**
     * Accessor method to get the Frustum used for rendering.
     * 
     * @return The Frustum used for rendering in the WorldRenderer.
     */
    @Accessor
    Frustum getFrustum();
}
