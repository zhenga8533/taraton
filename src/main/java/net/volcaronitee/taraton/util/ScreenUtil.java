package net.volcaronitee.taraton.util;

import org.joml.Matrix4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.screen.slot.Slot;

/**
 * Utility class for screen-related operations, particularly for rendering
 */
public final class ScreenUtil {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ScreenUtil() {}

    /**
     * Highlights a slot with a specified color and z-level.
     * 
     * @param context The current DrawContext.
     * @param slot The slot to highlight.
     * @param color The ARGB color to use for highlighting.
     * @param z The z-level for rendering, determining the draw order.
     */
    public static void highlightSlot(DrawContext context, Slot slot, int color, int z) {
        if (slot == null) {
            return;
        }
        context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, z, color);
    }

    /**
     * Draws a single line between two slots, handling all rendering setup and flushing internally.
     *
     * @param context The current DrawContext.
     * @param slot1 The starting slot.
     * @param slot2 The ending slot.
     * @param z The z-level for rendering.
     * @param color The ARGB color of the line.
     */
    public static void drawLine(DrawContext context, Slot slot1, Slot slot2, float z, int color) {
        if (context == null || slot1 == null || slot2 == null) {
            return;
        }

        // Get rendering resources
        VertexConsumerProvider.Immediate immediate =
                MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayer.getLines());

        // Get the current matrix
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        // Add the line to the buffer using our other helper
        drawLine(vertexConsumer, matrix, slot1, slot2, z, color);

        // Draw immediately (this is the inefficient part for multiple calls)
        immediate.draw();
    }

    /**
     * Draws a line between two slots using the provided vertex consumer and matrix.
     * 
     * @param vertexConsumer The vertex consumer to use for rendering.
     * @param matrix The current transformation matrix.
     * @param slot1 The starting slot.
     * @param slot2 The ending slot.
     * @param z The z-level for rendering, determining the draw order.
     * @param color The ARGB color of the line.
     */
    public static void drawLine(VertexConsumer vertexConsumer, Matrix4f matrix, Slot slot1,
            Slot slot2, float z, int color) {
        if (slot1 == null || slot2 == null) {
            return;
        }
        float x1 = slot1.x + 8f;
        float y1 = slot1.y + 8f;
        float x2 = slot2.x + 8f;
        float y2 = slot2.y + 8f;
        drawLine(vertexConsumer, matrix, x1, y1, x2, y2, z, color);
    }

    /**
     * Draws a line between two points in 3D space using the provided vertex consumer and matrix.
     * 
     * @param vertexConsumer The vertex consumer to use for rendering.
     * @param matrix The current transformation matrix.
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @param z The z-coordinate for both points, determining the draw order.
     * @param color The ARGB color of the line.
     */
    public static void drawLine(VertexConsumer vertexConsumer, Matrix4f matrix, float x1, float y1,
            float x2, float y2, float z, int color) {
        vertexConsumer.vertex(matrix, x1, y1, z).color(color).normal(1.0f, 0.0f, 0.0f);
        vertexConsumer.vertex(matrix, x2, y2, z).color(color).normal(1.0f, 0.0f, 0.0f);
    }
}
