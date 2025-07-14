package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Mouse;
import net.volcaronitee.taraton.feature.general.ServerStatus;

/**
 * Mixin class for the Mouse class in Minecraft to modify mouse behavior.
 */
@Mixin(Mouse.class)
public class MouseMixin {
    /**
     * Injects custom behavior into the onMouseButton method of Mouse.
     * 
     * @param window The window handle.
     * @param button The mouse button that was pressed or released.
     * @param action The action performed (pressed or released).
     * @param mods The modifier keys that were pressed during the mouse event.
     * @param ci The callback info to control the method's execution.
     */
    @Inject(method = "onMouseButton", at = @At("HEAD"))
    public void taraton$onMouseButton(long window, int button, int action, int mods,
            CallbackInfo ci) {
        ServerStatus.getInstance().onClick(button, action);
    }
}
