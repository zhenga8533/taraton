package net.volcaronitee.taraton.mixin.accessor;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;

/**
 * Accessor interface for InGameHud to retrieve the title text.
 */
@Mixin(InGameHud.class)
public interface InGameHudAccessor {
    /**
     * Accessor method to get the title text displayed in the game HUD.
     * 
     * @return The title text, or null if not set.
     */
    @Accessor("title")
    @Nullable
    Text getTitle();
}
