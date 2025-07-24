package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.volcaronitee.taraton.feature.general.ItemCooldown;

/**
 * Mixin class for DrawContext to inject custom rendering behavior for item cooldown overlays.
 */
@Mixin(DrawContext.class)
public class DrawContextMixin {
    /**
     * Injects into the drawStackOverlay method to render item cooldown overlays.
     * 
     * @param textRenderer The TextRenderer used for rendering text.
     * @param stack The ItemStack for which to render the cooldown overlay.
     * @param x The x-coordinate for rendering the cooldown overlay.
     * @param y The y-coordinate for rendering the cooldown overlay.
     * @param countOverride An optional string to override the item count display.
     * @param ci The CallbackInfo for the method injection, allowing modification of the method's
     *        behavior.
     */
    @Inject(method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At("TAIL"))
    private void taraton$drawStack(TextRenderer textRenderer, ItemStack stack, int x, int y,
            String countOverride, CallbackInfo ci) {
        ItemCooldown.getInstance().renderCooldown((DrawContext) (Object) this, stack, x, y);
    }
}
