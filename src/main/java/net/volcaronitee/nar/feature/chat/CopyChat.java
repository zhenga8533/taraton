package net.volcaronitee.nar.feature.chat;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.volcaronitee.nar.mixin.accessor.ChatHudAccessor;
import net.volcaronitee.nar.util.ConfigUtil;
import net.volcaronitee.nar.util.TextUtil;

/**
 * Handles copying chat messages when the user clicks on them while holding Shift.
 */
public class CopyChat {
    private static final CopyChat INSTANCE = new CopyChat();

    /**
     * Private constructor to prevent instantiation.
     */
    private CopyChat() {}

    /**
     * Returns the singleton instance of CopyChat.
     *
     * @return The singleton instance of CopyChat.
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
        if (!Screen.hasShiftDown() || button != GLFW.GLFW_MOUSE_BUTTON_LEFT
                || !ConfigUtil.getHandler().chat.copyChat) {
            return;
        }

        // Get chat HUD instance
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.inGameHud == null) {
            return;
        }

        ChatHud chatHudInstance = client.inGameHud.getChatHud();
        if (chatHudInstance == null) {
            return;
        }

        // Access ChatHud methods via the accessor
        ChatHudAccessor chatHudAccessor = (ChatHudAccessor) chatHudInstance;
        double chatLineX = chatHudAccessor.invokeToChatLineX(mouseX);
        double chatLineY = chatHudAccessor.invokeToChatLineY(mouseY);
        int visibleMessageIndex = chatHudAccessor.invokeGetMessageIndex(chatLineX, chatLineY);
        List<ChatHudLine.Visible> visibleLines = chatHudAccessor.getVisibleMessages();

        if (visibleMessageIndex == -1 || visibleMessageIndex >= visibleLines.size()) {
            return;
        }

        // Collect all lines of the clicked message
        ArrayList<ChatHudLine.Visible> messageParts = new ArrayList<>();
        messageParts.add(visibleLines.get(visibleMessageIndex));
        for (int i = visibleMessageIndex + 1; i < visibleLines.size(); i++) {
            if (visibleLines.get(i).endOfEntry()) {
                break;
            }
            messageParts.add(0, visibleLines.get(i));
        }

        // Concatenate the text from all parts of the message
        StringBuilder fullMessageText = new StringBuilder();
        for (ChatHudLine.Visible line : messageParts) {
            fullMessageText.append(TextUtil.getInstance().orderedTextToString(line.content()));
        }

        String messageToCopy = fullMessageText.toString();

        // Copy the message content to the clipboard and send a confirmation message
        if (!messageToCopy.isEmpty()) {
            client.keyboard.setClipboard(messageToCopy);
            Text sendMessage = TextUtil.MOD_TITLE.copy().append(
                    Text.literal(" §aCopied chat message to clipboard: §8§o" + messageToCopy));
            client.player.sendMessage(sendMessage, false);
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
