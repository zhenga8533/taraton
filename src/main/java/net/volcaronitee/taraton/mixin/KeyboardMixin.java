package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Keyboard;
import net.volcaronitee.taraton.feature.qol.CommandHotkey;

/**
 * Mixin to intercept keyboard input and handle command hotkeys.
 */
@Mixin(Keyboard.class)
public class KeyboardMixin {
    /**
     * Intercepts key events and triggers command hotkeys if applicable.
     * 
     * @param window The window handle.
     * @param key The key code.
     * @param scancode The scan code.
     * @param action The action (press, release, repeat).
     * @param modifiers The modifier keys.
     * @param info The callback info.
     */
    @Inject(method = "onKey", at = @At("HEAD"))
    private void taraton$onKey(long window, int key, int scancode, int action, int modifiers,
            CallbackInfo info) {
        CommandHotkey.getInstance().onKeyPress(key, action);
    }
}
