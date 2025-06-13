package net.volcaronitee.volcclient.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.LayeredDrawerWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class OverlayUtil {
    private static final OverlayUtil INSTANCE = new OverlayUtil();

    /**
     * Returns the singleton instance of OverlayUtil.
     *
     * @return The OverlayUtil instance.
     */
    public static OverlayUtil getInstance() {
        return INSTANCE;
    }

    private static final Map<String, Overlay> OVERLAYS = new java.util.HashMap<>();
    public static boolean globalMoveMode = false;

    private OverlayUtil() {}

    public static void init() {
        HudLayerRegistrationCallback.EVENT.register(OverlayUtil::renderOverlays);
    }

    public static void createOverlay(String name, Supplier<Boolean> shouldRender,
            List<LineContent> templateLines) {
        JsonObject overlayJson = JsonUtil.getInstance().loadJson(name + ".json");
        int x = overlayJson.has("x") ? overlayJson.get("x").getAsInt() : 0;
        int y = overlayJson.has("y") ? overlayJson.get("y").getAsInt() : 0;
        float scale = overlayJson.has("scale") ? overlayJson.get("scale").getAsFloat() : 1.0f;

        OVERLAYS.put(name, new Overlay(x, y, scale, shouldRender, templateLines));
    }

    public void resetOverlay(String name) {
        Overlay overlay = OVERLAYS.get(name);
        if (overlay != null) {
            JsonObject templateJson = JsonUtil.getInstance().loadTemplate(name + ".json");
            int x = templateJson.has("x") ? templateJson.get("x").getAsInt() : 0;
            int y = templateJson.has("y") ? templateJson.get("y").getAsInt() : 0;
            float scale = templateJson.has("scale") ? templateJson.get("scale").getAsFloat() : 1.0f;

            overlay.setX(x);
            overlay.setY(y);
            overlay.scale = scale;
            overlay.recalculateSize();
        }
    }

    public static void renderOverlays(LayeredDrawerWrapper context) {
        for (Overlay overlay : OVERLAYS.values()) {
            overlay.render((DrawContext) context);
        }
    }

    public static void saveOverlays() {
        for (Map.Entry<String, Overlay> entry : OVERLAYS.entrySet()) {
            String name = entry.getKey();
            Overlay overlay = entry.getValue();

            JsonObject overlayJson = new JsonObject();
            overlayJson.addProperty("x", overlay.getX());
            overlayJson.addProperty("y", overlay.getY());
            overlayJson.addProperty("scale", overlay.scale);

            JsonUtil.getInstance().saveJson("overlays", name + ".json", overlayJson);
        }
    }

    public static class LineContent {
        public final List<ItemStack> items;
        public Text text;

        public LineContent(List<ItemStack> items, Text text) {
            this.items = items;
            this.text = text;
        }

        public void changeText(Text newText) {
            this.text = newText;
        }
    }

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

        private void render(DrawContext context) {
            if (!shouldRender.get())
                return;

            TextRenderer tr = MinecraftClient.getInstance().textRenderer;

            float lineHeight = 18 * scale;

            List<LineContent> linesToRender = globalMoveMode ? templateLines : activeLines;

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

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setX(int newX) {
            x = newX;
        }

        public void setY(int newY) {
            y = newY;
        }
    }
}
