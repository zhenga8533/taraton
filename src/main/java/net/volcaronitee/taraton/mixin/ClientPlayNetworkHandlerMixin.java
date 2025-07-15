package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.volcaronitee.taraton.feature.general.ServerStatus;
import net.volcaronitee.taraton.feature.qol.ProtectItem;

/**
 * Mixin for the ClientPlayNetworkHandler class to inject custom behavior
 */
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    /**
     * Injects custom behavior into the sendPacket method of ClientPlayNetworkHandler.
     * 
     * @param packet The packet to be sent.
     * @param ci Callback information for the method call.
     */
    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void taraton$onSendPacket(Packet<?> packet, CallbackInfo ci) {
        // Handles click packets
        if (packet instanceof ClickSlotC2SPacket clickPacket) {
            if (ProtectItem.getInstance().shouldCancelThrow(clickPacket)) {
                ci.cancel();
            }
        }

        // Handles action packets
        if (packet instanceof PlayerActionC2SPacket actionPacket) {
            if (ProtectItem.getInstance().shouldCancelDrop(actionPacket)) {
                ci.cancel();
            }
        }
    }

    /**
     * Injects custom behavior into the onStatistics method of ClientPlayNetworkHandler.
     * 
     * @param packet The StatisticsS2CPacket containing statistics data.
     * @param ci Callback information for the method call.
     */
    @Inject(method = "onStatistics", at = @At("HEAD"))
    private void taraton$onStatistics(StatisticsS2CPacket packet, CallbackInfo ci) {
        ServerStatus.getInstance().onPingResponse();
    }
}
