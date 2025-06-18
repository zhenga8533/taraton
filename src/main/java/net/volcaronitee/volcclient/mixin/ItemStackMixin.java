package net.volcaronitee.volcclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.feature.chat.TextSubstitution;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    private void volcclient$modifyItemName(CallbackInfoReturnable<Text> cir) {
        Text originalName = cir.getReturnValue();
        Text modifiedName = TextSubstitution.itemStack$applyItemNameSubstitutions(originalName);

        if (originalName != modifiedName) {
            cir.setReturnValue(modifiedName);
        }
    }
}
