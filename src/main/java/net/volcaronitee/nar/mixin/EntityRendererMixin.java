package net.volcaronitee.nar.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.volcaronitee.nar.feature.qol.HideEntity;

/**
 * Mixin to modify the rendering behavior of entities in Minecraft.
 */
@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    /**
     * Injects into the shouldRender method to determine if an entity should be rendered.
     * 
     * @param entity The entity to check for rendering.
     * @param frustum The frustum used for rendering checks.
     * @param x The x-coordinate of the entity's position.
     * @param y The y-coordinate of the entity's position.
     * @param z The z-coordinate of the entity's position.
     * @param cir The callback info to control the rendering behavior.
     */
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void onShouldRender(Entity entity, Frustum frustum, double x, double y, double z,
            CallbackInfoReturnable<Boolean> cir) {
        if (HideEntity.getInstance().shouldHide(entity)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
