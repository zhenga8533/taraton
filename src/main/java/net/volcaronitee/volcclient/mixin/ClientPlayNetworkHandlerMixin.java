package net.volcaronitee.volcclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.volcaronitee.volcclient.feature.general.ServerStatus;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onStatistics", at = @At("HEAD"))
    private void onStatistics(StatisticsS2CPacket packet, CallbackInfo ci) {
        ServerStatus.onPingResponse();
    }

    @Inject(method = "onWorldTimeUpdate", at = @At("TAIL"))
    private void volcclient_onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        System.out.println("E");
        ServerStatus.recordServerTick();
    }
}
