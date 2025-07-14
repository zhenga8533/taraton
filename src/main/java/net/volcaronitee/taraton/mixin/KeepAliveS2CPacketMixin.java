package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.volcaronitee.taraton.feature.general.ServerStatus;

/**
 * Mixin for handling KeepAliveS2CPacket to update server status.
 */
@Mixin(KeepAliveS2CPacket.class)
public class KeepAliveS2CPacketMixin {
    /**
     * Injects into the KeepAliveS2CPacket application method to update server status.
     * 
     * @param clientCommonPacketListener The client common packet listener
     * @param ci The callback info
     */
    @Inject(method = "apply(Lnet/minecraft/network/listener/ClientCommonPacketListener;)V",
            at = @At("HEAD"))
    private void taraton$onKeepAlivePacket(ClientCommonPacketListener clientCommonPacketListener,
            CallbackInfo ci) {
        ServerStatus.getInstance().onKeepAlivePacket();
    }
}
