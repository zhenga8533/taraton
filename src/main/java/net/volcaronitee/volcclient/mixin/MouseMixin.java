package net.volcaronitee.volcclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Mouse;
import net.volcaronitee.volcclient.feature.general.ServerStatus;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onMouseButton", at = @At("HEAD"))
    public void volcclient$onMouseButton(long window, int button, int action, int mods,
            CallbackInfo ci) {
        ServerStatus.getInstance().onClick(button, action);
    }
}
