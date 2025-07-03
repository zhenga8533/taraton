package net.volcaronitee.nar.mixin.accessor;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.client.gui.hud.ChatHudLine;

/**
 * Mixin interface for accessing and invoking methods in the ChatHud class.
 */
@Mixin(net.minecraft.client.gui.hud.ChatHud.class)
public interface ChatHudAccessor {
    /**
     * Accessor for the visible messages in the chat HUD.
     * 
     * @return A list of visible chat lines.
     */
    @Accessor("visibleMessages")
    List<ChatHudLine.Visible> getVisibleMessages();

    /**
     * Accessor for the messages in the chat HUD.
     * 
     * @return A list of chat lines.
     */
    @Accessor("messages")
    List<ChatHudLine> getMessages();

    /**
     * Invoker for the method that converts a chat line X coordinate to a chat line
     * 
     * @param x The X coordinate to convert.
     * @return The converted X coordinate for the chat line.
     */
    @Invoker("toChatLineX")
    double invokeToChatLineX(double x);

    /**
     * Invoker for the method that converts a chat line Y coordinate to a chat line
     * 
     * @param y The Y coordinate to convert.
     * @return The converted Y coordinate for the chat line.
     */
    @Invoker("toChatLineY")
    double invokeToChatLineY(double y);

    /**
     * Invoker for the method that retrieves the message index based on chat line
     * 
     * @param chatLineX The X coordinate of the chat line.
     * @param chatLineY The Y coordinate of the chat line.
     * @return The index of the message at the specified chat line coordinates.
     */
    @Invoker("getMessageIndex")
    int invokeGetMessageIndex(double chatLineX, double chatLineY);
}
