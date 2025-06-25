package net.volcaronitee.volcclient.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.volcclient.VolcClient;

/**
 * Utility class for handling overlays.
 */
public class OverlayUtil {
    private static final OverlayUtil INSTANCE = new OverlayUtil();

    private static final Map<String, Overlay> OVERLAYS = new java.util.HashMap<>();
    public boolean globalMoveMode = false;

    /**
     * Private constructor to prevent instantiation.
     */
    private OverlayUtil() {}

    /**
     * Initializes the OverlayUtil by registering the overlay rendering callback.
     */
    public static void init() {
        HudElementRegistry.addFirst(Identifier.of(VolcClient.MOD_ID, "overlays"),
                OverlayUtil::renderOverlays);
    }

    /**
     * Creates a new overlay with the specified name, rendering condition, and template lines.
     * 
     * @param name The name of the overlay.
     * @param shouldRender A supplier that determines if the overlay should be rendered.
     * @param templateLines The template lines to be displayed in the overlay.
     */
    public static void createOverlay(String name, Supplier<Boolean> shouldRender,
            List<LineContent> templateLines) {
        JsonObject overlayJson = JsonUtil.loadJson(name + ".json");
        int x = overlayJson.has("x") ? overlayJson.get("x").getAsInt() : 0;
        int y = overlayJson.has("y") ? overlayJson.get("y").getAsInt() : 0;
        float scale = overlayJson.has("scale") ? overlayJson.get("scale").getAsFloat() : 1.0f;

        OVERLAYS.put(name, new Overlay(x, y, scale, shouldRender, templateLines));
    }

    /**
     * Resets the overlay with the specified name to its template values.
     * 
     * @param name The name of the overlay to reset.
     */
    public void resetOverlay(String name) {
        Overlay overlay = OVERLAYS.get(name);
        if (overlay != null) {
            JsonObject templateJson = JsonUtil.loadTemplate("overlays/" + name + ".json");
            int x = templateJson.has("x") ? templateJson.get("x").getAsInt() : 0;
            int y = templateJson.has("y") ? templateJson.get("y").getAsInt() : 0;
            float scale = templateJson.has("scale") ? templateJson.get("scale").getAsFloat() : 1.0f;

            overlay.x = x;
            overlay.y = y;
            overlay.scale = scale;
            overlay.recalculateSize();
        }
    }

    /**
     * Renders all overlays using the provided context.
     * 
     * @param context The context to use for rendering overlays.
     */
    public static void renderOverlays(DrawContext context, RenderTickCounter tickCounter) {
        for (Overlay overlay : OVERLAYS.values()) {
            overlay.render(context);
        }
    }

    /**
     * Updates the mouse position and state for all overlays.
     * 
     * @param mx The mouse X position.
     * @param my The mouse Y position.
     * @param isDown Whether the mouse button is pressed.
     */
    public static void saveOverlays() {
        for (Map.Entry<String, Overlay> entry : OVERLAYS.entrySet()) {
            String name = entry.getKey();
            Overlay overlay = entry.getValue();

            JsonObject overlayJson = new JsonObject();
            overlayJson.addProperty("x", overlay.x);
            overlayJson.addProperty("y", overlay.y);
            overlayJson.addProperty("scale", overlay.scale);

            JsonUtil.saveJson("overlays", name + ".json", overlayJson);
        }
    }

    /**
     * Represents the content of a line in an overlay, containing a list of item stacks and text.
     */
    public static class LineContent {
        public final List<ItemStack> items;
        public Text text;

        /**
         * Creates a new LineContent instance with the specified items and text.
         * 
         * @param items The list of item stacks to display in the line.
         * @param text The text to display in the line.
         */
        public LineContent(List<ItemStack> items, Text text) {
            this.items = items;
            this.text = text;
        }

        /**
         * Changes the text of this line content to a new Text instance.
         * 
         * @param newText The new Text instance to set for this line content.
         */
        public void changeText(Text newText) {
            this.text = newText;
        }
    }

    /**
     * Represents an overlay that can be rendered on the screen, containing lines of content.
     */
    public static class Overlay {
        private int x, y;
        private float scale;
        private boolean dragging = false;
        private int offsetX, offsetY;

        private final Supplier<Boolean> shouldRender;
        private final List<LineContent> activeLines;
        private final List<LineContent> templateLines;

        private float width = 0;
        private float height = 0;

        /**
         * Creates a new Overlay instance.
         * 
         * @param initialX The initial X position of the overlay.
         * @param initialY The initial Y position of the overlay.
         * @param scale The scale factor for the overlay.
         * @param shouldRender A supplier that determines if the overlay should be rendered.
         * @param templateLines The template lines to be displayed in the overlay.
         */
        public Overlay(int initialX, int initialY, float scale, Supplier<Boolean> shouldRender,
                List<LineContent> templateLines) {
            this.x = initialX;
            this.y = initialY;
            this.scale = scale;
            this.shouldRender = shouldRender;
            this.activeLines = new ArrayList<>(templateLines);
            this.templateLines = templateLines;
            recalculateSize();
        }

        /**
         * Renders the overlay using the provided context.
         * 
         * @param context The context to use for rendering the overlay.
         */
        private void render(DrawContext context) {
            if (!shouldRender.get())
                return;

            TextRenderer tr = MinecraftClient.getInstance().textRenderer;

            float lineHeight = 18 * scale;

            List<LineContent> linesToRender = INSTANCE.globalMoveMode ? templateLines : activeLines;

            for (int i = 0; i < linesToRender.size(); i++) {
                LineContent line = linesToRender.get(i);
                float offsetX = 0;
                float drawY = y + (i * lineHeight);

                for (ItemStack stack : line.items) {
                    context.drawItem(stack, (int) (x + offsetX), (int) drawY);
                    offsetX += 18 * scale;
                }

                context.drawText(tr, line.text, (int) (x + offsetX), (int) (drawY + 4 * scale),
                        0xAAAAAA, true);
            }
        }

        /**
         * Updates the mouse position and state for the overlay, allowing it to be dragged.
         * 
         * @param mx The mouse X position.
         * @param my The mouse Y position.
         * @param isDown Whether the mouse button is pressed.
         */
        public void updateMouse(double mx, double my, boolean isDown) {
            // Check if the mouse is within the overlay bounds
            if (mx >= x && mx <= x + width && my >= y && my <= y + height) {
                if (isDown && !dragging) {
                    dragging = true;
                    offsetX = (int) mx - x;
                    offsetY = (int) mx - y;
                }
            }

            if (!isDown)
                dragging = false;

            if (dragging) {
                x = (int) mx - offsetX;
                y = (int) mx - offsetY;
            }
        }

        /**
         * Recalculates the size of the overlay based on its content.
         */
        public void recalculateSize() {
            TextRenderer tr = MinecraftClient.getInstance().textRenderer;

            float maxWidth = 0;
            float totalHeight = 0;
            float lineHeight = 18 * scale;

            for (LineContent line : templateLines) {
                float lineWidth = (line.items.size() * 18 + tr.getWidth(line.text) * scale);
                maxWidth = Math.max(maxWidth, lineWidth);
                totalHeight += lineHeight;
            }

            this.width = maxWidth;
            this.height = totalHeight;
        }
    }
}
