package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.volcaronitee.taraton.feature.qol.ProtectItem;
import net.volcaronitee.taraton.util.PlayerUtil;

/**
 * Mixin for the ClientPlayerEntity class to inject custom behavior
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    /**
     * Injects custom behavior into the move method of ClientPlayerEntity.
     * 
     * @param type The type of movement (e.g., walking, flying)
     * @param movement The movement vector indicating how much to move in each direction
     * @param info Callback information for the method call
     */
    @Inject(method = "move", at = @At(value = "HEAD"))
    private void taraton$playerMove(MovementType type, Vec3d movement, CallbackInfo info) {
        PlayerUtil.clientPlayerEntity$move();
    }

    @Inject(method = "dropSelectedItem(Z)Z", at = @At("HEAD"), cancellable = true)
    private void taraton$onSelectDrop(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack selectedStack = player.getInventory().getSelectedStack();

        if (!selectedStack.isEmpty()
                && ProtectItem.getInstance().shouldCancelStack(selectedStack)) {
            cir.setReturnValue(false);
        }
    }
}
