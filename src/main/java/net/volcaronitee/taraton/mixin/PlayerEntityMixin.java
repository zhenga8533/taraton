package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.volcaronitee.taraton.feature.qol.ProtectItem;

/**
 * Mixin to prevent players from dropping items that are protected.
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    /**
     * Intercepts the dropItem method to check if the item stack is protected.
     * 
     * @param stack The item stack to be dropped.
     * @param throwRandomly Whether the item should be thrown randomly.
     * @param retainOwnership Whether the item should retain ownership after being dropped.
     * @param cir The callback info to return the item entity or cancel the drop.
     */
    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
            at = @At("HEAD"), cancellable = true)
    private void taraton$onDropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership,
            CallbackInfoReturnable<ItemEntity> cir) {
        if (ProtectItem.getInstance().shouldCancelStack(stack)) {
            cir.setReturnValue(null);
        }
    }
}
