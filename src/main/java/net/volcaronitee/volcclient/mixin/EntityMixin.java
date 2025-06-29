package net.volcaronitee.volcclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.entity.Entity;
import net.volcaronitee.volcclient.feature.combat.EntityHighlight;

/**
 * Mixin for the Entity class to modify glowing behavior and team color value.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
    /**
     * Injects into the isGlowing method to allow custom glowing behavior.
     * 
     * @param cir The callback info for the return value
     */
    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void volcclient$entityIsGlowing(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (EntityHighlight.getInstance().getGlow(entity)) {
            cir.setReturnValue(true);
        }
    }

    /**
     * Injects into the getTeamColorValue method to return a custom color value.
     * 
     * @param cir The callback info for the return value
     */
    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void volcclient$entityGetTeamColorValue(CallbackInfoReturnable<Integer> cir) {
        Entity entity = (Entity) (Object) this;
        if (EntityHighlight.getInstance().getGlow(entity)) {
            cir.setReturnValue(EntityHighlight.getInstance().getColor(entity));
        }
    }
}
