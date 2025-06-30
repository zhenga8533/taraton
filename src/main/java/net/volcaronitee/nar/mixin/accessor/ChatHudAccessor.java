package net.volcaronitee.nar.mixin.accessor;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.client.gui.hud.ChatHudLine; // Correct import for ChatHudLine

@Mixin(net.minecraft.client.gui.hud.ChatHud.class)
public interface ChatHudAccessor {

    @Accessor("visibleMessages")
    List<ChatHudLine.Visible> getVisibleMessages();

    // CORRECTED TYPE: Now uses the top-level ChatHudLine class
    @Accessor("messages")
    List<ChatHudLine> getMessages();

    @Invoker("toChatLineX")
    double invokeToChatLineX(double x);

    @Invoker("toChatLineY")
    double invokeToChatLineY(double y);

    @Invoker("getMessageIndex")
    int invokeGetMessageIndex(double chatLineX, double chatLineY);
}
