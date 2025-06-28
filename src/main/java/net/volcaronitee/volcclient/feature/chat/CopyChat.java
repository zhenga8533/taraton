package net.volcaronitee.volcclient.feature.chat;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.OrderedText;
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

        // Get the chat hud instance
        ChatHud chatHudInstance = client.inGameHud.getChatHud();
        if (chatHudInstance == null) {
            return;
        }

        // Cast the chat hud instance to ChatHudAccessor to access its methods
        ChatHudAccessor chatHudAccessor = (ChatHudAccessor) chatHudInstance;
        double chatLineX = chatHudAccessor.invokeToChatLineX(mouseX);
        double chatLineY = chatHudAccessor.invokeToChatLineY(mouseY);
        int messageIndex = chatHudAccessor.invokeGetMessageIndex(chatLineX, chatLineY);
        if (messageIndex == -1 || messageIndex >= chatHudAccessor.getVisibleMessages().size()) {
            return;
        }

        // Find the start of the message by checking previous messages until we find an end of entry
        int startOfMessageIndex = messageIndex;
        while (startOfMessageIndex > 0 && !chatHudAccessor.getVisibleMessages()
                .get(startOfMessageIndex - 1).endOfEntry()) {
            startOfMessageIndex--;
        }

        // Build the full message text from the start index to the clicked message index
        StringBuilder fullMessageBuilder = new StringBuilder();
        for (int i = startOfMessageIndex; i <= messageIndex; i++) {
            OrderedText currentOrderedText = chatHudAccessor.getVisibleMessages().get(i).content();
            fullMessageBuilder
                    .append(TextUtil.getInstance().orderedTextToString(currentOrderedText));
        }

        // Set the clipboard with the full message text and send a confirmation message
        String messageText = fullMessageBuilder.toString();
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
