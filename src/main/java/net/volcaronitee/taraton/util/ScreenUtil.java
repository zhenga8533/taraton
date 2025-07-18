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
     * Draws a line between two slots with a specified color and thickness.
     * 
     * @param context The current DrawContext.
     * @param slot1 The first slot to draw the line from.
     * @param slot2 The second slot to draw the line to.
     * @param z The z-level for rendering, determining the draw order.
     * @param color The ARGB color of the line.
     * @param thickness The thickness of the line to be drawn.
     */
    public static void drawLine(DrawContext context, Slot slot1, Slot slot2, float z, int color,
            float thickness) {
        if (context == null || slot1 == null || slot2 == null || thickness <= 0) {
            return;
        }

        float x1 = slot1.x + 8f;
        float y1 = slot1.y + 8f;
        float x2 = slot2.x + 8f;
        float y2 = slot2.y + 8f;

        // Get the immediate vertex consumer for rendering
        VertexConsumerProvider.Immediate immediate =
                MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayer.getGui());
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length == 0) {
            return;
        }

        // Calculate the perpendicular vector for the line's thickness
        float px = -dy / length * (thickness / 2f);
        float py = dx / length * (thickness / 2f);

        // Define the four corners of the rectangle (quad) representing the line
        float p1x = x1 + px;
        float p1y = y1 + py;
        float p2x = x2 + px;
        float p2y = y2 + py;
        float p3x = x2 - px;
        float p3y = y2 - py;
        float p4x = x1 - px;
        float p4y = y1 - py;

        // Draw the first triangle of the quad
        vertexConsumer.vertex(matrix, p1x, p1y, z).color(color);
        vertexConsumer.vertex(matrix, p2x, p2y, z).color(color);
        vertexConsumer.vertex(matrix, p3x, p3y, z).color(color);
        vertexConsumer.vertex(matrix, p4x, p4y, z).color(color);

        // Draw the second triangle to complete the quad
        vertexConsumer.vertex(matrix, p1x, p1y, z).color(color);
        vertexConsumer.vertex(matrix, p3x, p3y, z).color(color);
        immediate.draw();
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
