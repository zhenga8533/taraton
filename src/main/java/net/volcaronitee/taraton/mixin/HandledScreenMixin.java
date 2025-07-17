package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.volcaronitee.taraton.interfaces.TooltipSuppressor;

/**
 * Mixin to HandledScreen to suppress tooltips when needed.
 */
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin implements TooltipSuppressor {
    @Unique
    private boolean suppressTooltip = false;

    @Override
    public void setSuppressTooltip(boolean suppress) {
        this.suppressTooltip = suppress;
    }

    /**
     * Inject into drawMouseoverTooltip to cancel tooltip rendering if suppressTooltip is true.
     * 
     * @param context The DrawContext.
     * @param mouseX The mouse X position.
     * @param mouseY The mouse Y position.
     * @param ci The CallbackInfo.
     */
    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true)
    private void onDrawMouseoverTooltip(DrawContext context, int mouseX, int mouseY,
            CallbackInfo ci) {
        if (this.suppressTooltip) {
            ci.cancel();
        }
    }
}
