package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import net.volcaronitee.taraton.config.TaratonConfig;

/**
 * Mixin to hide the status effects display in the inventory.
 */
@Mixin(StatusEffectsDisplay.class)
public abstract class StatusEffectsDisplayMixin {
    /**
     * Injects into the drawStatusEffects method to cancel the rendering of status effects.
     * 
     * @param context The DrawContext used for rendering.
     * @param mouseX The mouse X coordinate.
     * @param mouseY The mouse Y coordinate.
     * @param deltaTicks The delta ticks for the frame.
     * @param ci The CallbackInfo for the injection.
     */
    @Inject(method = "drawStatusEffects", at = @At("HEAD"), cancellable = true)
    private void taraton$hideStatusEffects(DrawContext context, int mouseX, int mouseY,
            float deltaTicks, CallbackInfo ci) {
        if (TaratonConfig.getHandler().qol.hidePotionEffects) {
            ci.cancel();
        }
    }
}
