package net.volcaronitee.volcclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.client.network.ClientPlayerEntity;

import net.volcaronitee.volcclient.util.PlayerUtil;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "move", at = @At("HEAD"))
    private void volcclient$onPlayerMove() {
        PlayerUtil.getInstance().clientPlayerEntity$onPlayerMove();
    }
}
