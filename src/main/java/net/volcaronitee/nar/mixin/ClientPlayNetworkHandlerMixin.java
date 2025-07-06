package net.volcaronitee.nar.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.volcaronitee.nar.feature.general.ServerStatus;

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
    private void nar$onStatistics(StatisticsS2CPacket packet, CallbackInfo ci) {
        ServerStatus.getInstance().onPingResponse();
    }
}
