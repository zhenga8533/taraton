package net.volcaronitee.nar.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.Mouse;
import net.volcaronitee.nar.feature.general.NoMouseReset;
import net.volcaronitee.nar.feature.general.ServerStatus;

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
    public void NotARat$mouseOnMouseButton(long window, int button, int action, int mods,
            CallbackInfo ci) {
        ServerStatus.getInstance().onClick(button, action);
    }

    /**
     * Wraps the unlockCursor and lockCursor methods to conditionally set the cursor position based
     * on the NoMouseReset feature.
     * 
     * @param instance The instance of the Mouse class.
     * @param value The value to set for the cursor position (x or y).
     * @return True if the cursor position should be set, false otherwise.
     */
    @WrapWithCondition(method = {"unlockCursor", "lockCursor"}, at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/Mouse;x:D", opcode = Opcodes.PUTFIELD))
    private boolean nar_shouldSetCursorX(Mouse instance, double value) {
        return NoMouseReset.getInstance().shouldCenter();
    }

    /**
     * Wraps the unlockCursor and lockCursor methods to conditionally set the cursor position based
     * on the NoMouseReset feature.
     * 
     * @param instance The instance of the Mouse class.
     * @param value The value to set for the cursor position (x or y).
     * @return True if the cursor position should be set, false otherwise.
     */
    @WrapWithCondition(method = {"unlockCursor", "lockCursor"}, at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/Mouse;y:D", opcode = Opcodes.PUTFIELD))
    private boolean nar_shouldSetCursorY(Mouse instance, double value) {
        return NoMouseReset.getInstance().shouldCenter();
    }
}
