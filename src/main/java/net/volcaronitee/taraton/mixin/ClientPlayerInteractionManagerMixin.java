package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.volcaronitee.taraton.feature.qol.ProtectItem;

/**
 * Mixin for the ClientPlayerInteractionManager class to inject custom behavior.
 */
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    /**
     * Injects custom behavior into the clickSlot method of ClientPlayerInteractionManager to
     * prevent dropping protected items.
     * 
     * @param syncId The synchronization ID of the container
     * @param slotId The ID of the slot being clicked
     * @param button The mouse button used for the click
     * @param actionType The type of action being performed on the slot
     * @param player The player performing the action
     * @param ci Callback information for the method call
     */
    @Inject(method = "clickSlot(IIILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At("HEAD"), cancellable = true)
    private void taraton$onGuiDrop(int syncId, int slotId, int button, SlotActionType actionType,
            PlayerEntity player, CallbackInfo ci) {
        ItemStack clickedStack = player.currentScreenHandler.getSlot(slotId).getStack();

        if (actionType == SlotActionType.THROW) {
            // This action is a drop from a specific slot
            if (ProtectItem.getInstance().shouldCancelStack(clickedStack)) {
                ci.cancel();
            }
        } else if (slotId == -999 && actionType == SlotActionType.PICKUP) {
            // This action is a drop outside the inventory (slotId -999)
            ItemStack cursorStack = player.currentScreenHandler.getCursorStack();

            if (ProtectItem.getInstance().shouldCancelStack(cursorStack)) {
                ci.cancel();
            }
        } else if (actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_MOVE) {
            // This action is a pickup or quick move
            String title = MinecraftClient.getInstance().currentScreen.getTitle().getString();
            if (title.startsWith("Salvage")) {
                // If the screen title starts with "Salvage", we check for cancel
                if (ProtectItem.getInstance().shouldCancelStack(clickedStack)) {
                    ci.cancel();
                }
            }
        }
    }
}
