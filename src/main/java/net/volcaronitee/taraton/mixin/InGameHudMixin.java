package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.volcaronitee.taraton.config.TaratonConfig;

/**
 * Mixin to hide potion effects from the HUD.
 */
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    /**
     * Injects into the renderStatusEffectOverlay method to cancel the rendering of status effects
     * from the HUD.
     * 
     * @param context The DrawContext used for rendering.
     * @param tickCounter The RenderTickCounter for the current frame.
     * @param ci The CallbackInfo for the injection.
     */
    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void taraton$hideStatusEffectsOnHud(DrawContext context, RenderTickCounter tickCounter,
            CallbackInfo ci) {
        if (TaratonConfig.getInstance().qol.hidePotionEffects) {
            ci.cancel();
        }
    }
}
