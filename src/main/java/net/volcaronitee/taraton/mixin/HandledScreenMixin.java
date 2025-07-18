package net.volcaronitee.taraton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.volcaronitee.taraton.feature.qol.ProtectItem;
import net.volcaronitee.taraton.interfaces.TooltipSuppressor;

/**
 * Mixin for HandledScreen to suppress tooltips and prevent dropping items
 */
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin implements TooltipSuppressor {
    // Suppresses tooltips
    @Unique
    private boolean suppressTooltip = false;

    @Override
    public void setSuppressTooltip(boolean suppress) {
        this.suppressTooltip = suppress;
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true)
    private void onDrawMouseoverTooltip(DrawContext context, int mouseX, int mouseY,
            CallbackInfo ci) {
        if (this.suppressTooltip) {
            ci.cancel();
        }
    }

    // Prevents dropping specific items by clicking outside the GUI
    @Shadow
    protected abstract boolean isClickOutsideBounds(double mouseX, double mouseY, int x, int y,
            int button);

    @Shadow
    protected int x;

    @Shadow
    protected int y;

    @Shadow
    public abstract ScreenHandler getScreenHandler();

    /**
     * Intercepts mouse clicks to prevent dropping protected items
     * 
     * @param mouseX The X coordinate of the mouse cursor
     * @param mouseY The Y coordinate of the mouse cursor
     * @param button The mouse button that was clicked
     * @param cir The callback info to return whether the click was handled
     */
    @Inject(method = "mouseClicked(DDI)Z", at = @At("HEAD"), cancellable = true)
    private void taraton$onScreenMouseClicked(double mouseX, double mouseY, int button,
            CallbackInfoReturnable<Boolean> cir) {
        ItemStack cursorStack = this.getScreenHandler().getCursorStack();

        if (ProtectItem.getInstance().shouldCancelStack(cursorStack)
                && this.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, button)) {
            cir.setReturnValue(false);
        }
    }
}
