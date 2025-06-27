package net.volcaronitee.volcclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.volcaronitee.volcclient.feature.general.ServerStatus;

/**
 * Mixin for the ClientPlayNetworkHandler class to inject custom behavior
 */
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    /**
     * Injects custom behavior into the onStatistics method of ClientPlayNetworkHandler.
     * 
     * @param packet The StatisticsS2CPacket containing statistics data
     * @param ci Callback information for the method call
     */
    @Inject(method = "onStatistics", at = @At("HEAD"))
    private void volcclient$clientPlayerNetworkHandlerOnStatistics(StatisticsS2CPacket packet,
            CallbackInfo ci) {
        ServerStatus.getInstance().onPingResponse();
    }

    /**
     * Injects custom behavior into the onWorldTimeUpdate method of ClientPlayNetworkHandler.
     * 
     * @param packet The WorldTimeUpdateS2CPacket containing world time data
     * @param ci Callback information for the method call
     */
    @Inject(method = "onWorldTimeUpdate", at = @At("TAIL"))
    private void volcclient$clientPlayerNetworkHandlerOnWorldTimeUpdate(
            WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        ServerStatus.getInstance().recordServerTick();
    }

    /**
     * TODO: Test these
     * 
     * - onTickStep(TickStepS2CPacket packet)
     * 
     * - onPlayerListHeader(PlayerListHeaderS2CPacket packet)
     */
}
