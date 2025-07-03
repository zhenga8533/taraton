package net.volcaronitee.nar.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.volcaronitee.nar.config.NarConfig;

/**
 * Mixin to prevent the mouse from being reset when opening a screen.
 */
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    /**
     * Redirects the call to unlock the cursor when a screen is set.
     * 
     * @param instance The Mouse instance.
     */
    @Redirect(method = "setScreen",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;unlockCursor()V"),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;onDisplayed()V",
                    ordinal = 0)))
    private void volcclient$noMouseReset(Mouse instance) {
        if (!NarConfig.getHandler().general.noMouseReset) {
            instance.unlockCursor();
        }
    }
}
