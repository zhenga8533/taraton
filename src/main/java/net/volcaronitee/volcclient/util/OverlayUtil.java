package net.volcaronitee.volcclient.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class OverlayUtil {
    private ArrayList<Overlay> overlays = new ArrayList<>();

    public static boolean globalMoveMode = false;

    public void createOverlay(int initialX, int initialY, Supplier<Boolean> shouldRender,
            List<LineContent> templateLines) {
        overlays.add(new Overlay(initialX, initialY, shouldRender, templateLines));
    }

    public void renderOverlays(DrawContext context) {
        for (Overlay overlay : overlays) {
            overlay.render(context);
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

    public class Overlay {
        private int x, y;
        private boolean dragging = false;
        private int offsetX, offsetY;

        private final Supplier<Boolean> shouldRender;
        private final List<LineContent> activeLines;
        private final List<LineContent> templateLines;

        private int width = 0;
        private int height = 0;

        public Overlay(int initialX, int initialY, Supplier<Boolean> shouldRender,
                List<LineContent> templateLines) {
            this.x = initialX;
            this.y = initialY;
            this.shouldRender = shouldRender;
            this.activeLines = new ArrayList<>(templateLines);
            this.templateLines = templateLines;
            recalculateSize();
        }

        private void render(DrawContext context) {
            if (!shouldRender.get())
                return;

            TextRenderer tr = MinecraftClient.getInstance().textRenderer;

            int maxWidth = 0;
            int lineHeight = 18;
            int totalHeight = 0;

            List<LineContent> linesToRender = globalMoveMode ? templateLines : activeLines;

            for (int i = 0; i < linesToRender.size(); i++) {
                LineContent line = linesToRender.get(i);
                int offsetX = 0;
                int drawY = y + (i * lineHeight);

                for (ItemStack stack : line.items) {
                    context.drawItem(stack, x + offsetX, drawY);
                    offsetX += 18;
                }

                context.drawText(tr, line.text, x + offsetX, drawY + 4, 0xAAAAAA, true);

                int lineWidth = offsetX + tr.getWidth(line.text);
                maxWidth = Math.max(maxWidth, lineWidth);
                totalHeight += lineHeight;
            }

            this.width = maxWidth;
            this.height = totalHeight;
        }

        public void updateMouse(double mx, double my, boolean isDown) {
            if (!globalMoveMode)
                return;

            if (isMouseOver(mx, my)) {
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

        public boolean isMouseOver(double mx, double my) {
            return mx >= x && mx <= x + width && my >= y && my <= y + height;
        }

        public void recalculateSize() {
            TextRenderer tr = MinecraftClient.getInstance().textRenderer;

            int maxWidth = 0;
            int totalHeight = 0;
            int lineHeight = 18;

            for (LineContent line : templateLines) {
                int lineWidth = (line.items.size() * 18) + tr.getWidth(line.text);
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
