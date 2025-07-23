package net.volcaronitee.taraton.interfaces;

import net.minecraft.client.gui.DrawContext;

/**
 * Represents a special render function that can be used to render custom content
 */
public interface SpecialRender {
    /**
     * Renders custom content using the provided context and delta time.
     * 
     * @param context The context to use for rendering the custom content.
     * @param delta The time delta since the last render.
     */
    void render(DrawContext context, float delta);
}
