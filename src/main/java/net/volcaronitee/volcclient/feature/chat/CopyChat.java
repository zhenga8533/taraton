package net.volcaronitee.volcclient.feature.chat;

import java.util.List;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.mixin.accessor.ChatHudAccessor;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.TextUtil;

/**
 * Handles copying chat messages when the user clicks on them while holding
 */
public class CopyChat {
    private static final CopyChat INSTANCE = new CopyChat();

    /**
     * Private constructor to prevent instantiation.
     */
    private CopyChat() {}

    /**
     * Returns the singleton instance of ChatCopy.
     *
     * @return The singleton instance of ChatCopy.
     */
    public static CopyChat getInstance() {
        return INSTANCE;
    }

    /**
     * Handles the mouse click event to copy chat messages.
     *
     * @param mouseX The x-coordinate of the mouse click.
     * @param mouseY The y-coordinate of the mouse click.
     * @param button The mouse button that was clicked.
     * @param cir The callback information to control the flow of the method.
     */
    public void onMouseClicked(double mouseX, double mouseY, int button,
            CallbackInfoReturnable<Boolean> cir) {
        // Check if the Shift key is pressed and the left mouse button is clicked
        if (!Screen.hasShiftDown() || button != GLFW.GLFW_MOUSE_BUTTON_LEFT
                || !ConfigUtil.getHandler().chat.copyChat) {
            return;
        }

        // Get the Minecraft client instance and check if player and in-game HUD are available
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.inGameHud == null) {
            return;
        }

        ChatHud chatHudInstance = client.inGameHud.getChatHud();
        if (chatHudInstance == null) {
            return;
        }

        // Cast the chat hud instance to ChatHudAccessor to access its methods
        ChatHudAccessor chatHudAccessor = (ChatHudAccessor) chatHudInstance;
        double chatLineX = chatHudAccessor.invokeToChatLineX(mouseX);
        double chatLineY = chatHudAccessor.invokeToChatLineY(mouseY);
        int visibleMessageIndex = chatHudAccessor.invokeGetMessageIndex(chatLineX, chatLineY);

        List<ChatHudLine.Visible> visibleLines = chatHudAccessor.getVisibleMessages();
        if (visibleMessageIndex == -1 || visibleMessageIndex >= visibleLines.size()) {
            return;
        }

        // Get the clicked visible line and find the corresponding message source
        ChatHudLine.Visible clickedVisibleLine = visibleLines.get(visibleMessageIndex);
        List<ChatHudLine> allChatLines = chatHudAccessor.getMessages();
        ChatHudLine messageSource = null;
        for (ChatHudLine chatLine : allChatLines) {
            if (chatLine.creationTick() == clickedVisibleLine.addedTime()) {
                messageSource = chatLine;
                break;
            }
        }
        if (messageSource == null) {
            return;
        }

        // Copy the message content to the clipboard
        String messageText = messageSource.content().getString();
        if (!messageText.isEmpty()) {
            client.keyboard.setClipboard(messageText);
            Text sendMessage = TextUtil.MOD_TITLE.copy().append(
                    Text.literal(" §aCopied chat message to clipboard: §8§o" + messageText));
            client.player.sendMessage(sendMessage, false);
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
