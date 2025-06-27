package net.volcaronitee.volcclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.volcaronitee.volcclient.feature.general.ServerStatus; // Import your ServerStatus class

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onWorldTimeUpdate", at = @At("HEAD"), cancellable = false)
    private void volcclient_onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        ServerStatus.getInstance().recordServerTick();
    }
}
