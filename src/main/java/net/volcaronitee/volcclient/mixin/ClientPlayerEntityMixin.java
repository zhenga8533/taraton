package net.volcaronitee.volcclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import net.volcaronitee.volcclient.util.PlayerUtil;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "move", at = @At("HEAD"))
    private void volcclient$clientPlayerEntityMove(MovementType type, Vec3d movement,
            CallbackInfo ci) {
        PlayerUtil.getInstance().clientPlayerEntity$move();
    }
}
