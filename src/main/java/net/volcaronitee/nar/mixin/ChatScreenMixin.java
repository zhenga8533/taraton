package net.volcaronitee.nar.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.volcaronitee.nar.feature.chat.CopyChat;

/**
 * Mixin for the ChatScreen to handle mouse clicks for copying chat messages.
 */
@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    /**
     * Constructor for ChatScreenMixin.
     * 
     * @param title The title of the chat screen.
     */
    protected ChatScreenMixin(Text title) {
        super(title);
    }

    /**
     * Injects into the mouseClicked method of ChatScreen to handle chat message copying.
     * 
     * @param mouseX The X coordinate of the mouse click.
     * @param mouseY The Y coordinate of the mouse click.
     * @param button The mouse button that was clicked.
     * @param cir The callback info to control the method's return value.
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button,
            CallbackInfoReturnable<Boolean> cir) {
        CopyChat.getInstance().onMouseClicked(mouseX, mouseY, button, cir);
    }
}
