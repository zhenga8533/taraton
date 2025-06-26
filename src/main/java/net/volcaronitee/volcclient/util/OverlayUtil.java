package net.volcaronitee.volcclient.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.volcaronitee.volcclient.VolcClient;

/**
 * Utility class for handling overlays.
 */
public class OverlayUtil {
    private static final OverlayUtil INSTANCE = new OverlayUtil();

    private static final Map<String, Overlay> OVERLAYS = new java.util.HashMap<>();
    private boolean globalMoveMode = false;
    private Overlay currentOverlay = null;

    private static final int FONT_SIZE = 9;
    private static final int MARGIN = 4;

    /**
     * Private constructor to prevent instantiation.
     */
    private OverlayUtil() {}

    /**
     * Initializes the OverlayUtil by registering the overlay rendering callback.
     */
    public static void init() {
        HudElementRegistry.addLast(Identifier.of(VolcClient.MOD_ID, "overlays"),
                OverlayUtil::renderOverlays);
    }

    /**
     * Creates a new overlay with the specified name, rendering condition, and template lines.
     * 
     * @param name The name of the overlay.
     * @param shouldRender A supplier that determines if the overlay should be rendered.
     * @param templateLines The template lines to be displayed in the overlay.
     */
    public static Overlay createOverlay(String name, Supplier<Boolean> shouldRender,
            List<LineContent> templateLines) {
        JsonObject overlayJson = JsonUtil.loadJson("overlays", name + ".json");
        int x = overlayJson.has("x") ? overlayJson.get("x").getAsInt() : 0;
        int y = overlayJson.has("y") ? overlayJson.get("y").getAsInt() : 0;
        float scale = overlayJson.has("scale") ? overlayJson.get("scale").getAsFloat() : 1.0f;

        Overlay overlay = new Overlay(x, y, scale, shouldRender, templateLines);
        OVERLAYS.put(name, overlay);

        return overlay;
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
            overlay.calcSize();
        }
    }

    /**
     * Renders all overlays using the provided context.
     * 
     * @param context The context to use for rendering overlays.
     */
    public static void renderOverlays(DrawContext context, RenderTickCounter tickCounter) {
        if (INSTANCE.globalMoveMode) {
            return;
        }

        for (Overlay overlay : OVERLAYS.values()) {
            overlay.render(context);
        }
    }

    /**
     * Moves the GUI to a new screen for overlay management.
     * 
     * @param context The command context containing the source of the command.
     */
    public static int moveGui(CommandContext<FabricClientCommandSource> context) {
        INSTANCE.globalMoveMode = true;

        // Recalculate the size of all overlays and reset dragging state
        for (Map.Entry<String, Overlay> entry : OVERLAYS.entrySet()) {
            Overlay overlay = entry.getValue();
            overlay.calcSize();
        }

        // Create a new screen for managing overlays
        MinecraftClient.getInstance().send(() -> {
            ScreenUtil screen = new ScreenUtil() {
                @Override
                public void render(DrawContext context, int mouseX, int mouseY, float delta) {
                    context.fill(0, 0, this.width, this.height, 0x60007BFF);
                    for (Overlay overlay : OVERLAYS.values()) {
                        overlay.render(context);
                    }
                    super.render(context, mouseX, mouseY, delta);
                }

                @Override
                public void renderBackground(DrawContext context, int mouseX, int mouseY,
                        float deltaTicks) {}

                @Override
                public boolean mouseClicked(double mouseX, double mouseY, int button) {
                    for (Overlay overlay : OVERLAYS.values()) {
                        if (mouseX >= overlay.x - MARGIN
                                && mouseX <= overlay.x + overlay.width + MARGIN
                                && mouseY >= overlay.y - MARGIN
                                && mouseY <= overlay.y + overlay.height + MARGIN) {
                            INSTANCE.currentOverlay = overlay;
                            INSTANCE.currentOverlay.dx = (float) mouseX - overlay.x;
                            INSTANCE.currentOverlay.dy = (float) mouseY - overlay.y;
                            break;
                        }
                    }
                    return super.mouseClicked(mouseX, mouseY, button);
                }

                @Override
                public boolean mouseReleased(double mouseX, double mouseY, int button) {
                    INSTANCE.currentOverlay = null;
                    return super.mouseReleased(mouseX, mouseY, button);
                }

                @Override
                public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX,
                        double deltaY) {
                    if (INSTANCE.currentOverlay != null) {
                        INSTANCE.currentOverlay.x = (int) (mouseX - INSTANCE.currentOverlay.dx);
                        INSTANCE.currentOverlay.y = (int) (mouseY - INSTANCE.currentOverlay.dy);
                    }
                    return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
                }

                @Override
                public void close() {
                    INSTANCE.globalMoveMode = false;
                    OverlayUtil.saveOverlays();
                    super.close();
                }
            };
            context.getSource().getClient().setScreen(screen);
        });

        return 1;
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
         * Creates a new LineContent instance with the specified text.
         * 
         * @param text The text to display in the line.
         */
        public LineContent(Text text) {
            this.items = new ArrayList<>();
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

        private final Supplier<Boolean> shouldRender;
        private final List<LineContent> activeLines;
        private final List<LineContent> templateLines;

        private float width = -1;
        private float height = -1;

        private float dx = 0;
        private float dy = 0;

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
        }

        /**
         * Renders the overlay using the provided context.
         * 
         * @param context The context to use for rendering the overlay.
         */
        private void render(DrawContext context) {
            if (!shouldRender.get())
                return;

            // Recalculate size if width or height is not initialized
            if (width < 0 || height < 0) {
                calcSize();
            }

            // Draw the overlay box
            int boxX1 = (int) x - MARGIN;
            int boxY1 = (int) y - MARGIN;
            int boxX2 = (int) (x + width + MARGIN);
            int boxY2 = (int) (y + height + MARGIN);
            context.fill(boxX1, boxY1, boxX2, boxY2, 0x80000000);
            context.drawBorder(boxX1, boxY1, boxX2 - boxX1, boxY2 - boxY1, 0xFFDDDDDD);

            TextRenderer tr = MinecraftClient.getInstance().textRenderer;
            float lineHeight = FONT_SIZE * scale;

            List<LineContent> linesToRender = INSTANCE.globalMoveMode ? templateLines : activeLines;

            for (int i = 0; i < linesToRender.size(); i++) {
                LineContent line = linesToRender.get(i);
                float offsetX = 0;
                float drawY = y + (i * lineHeight);

                for (ItemStack stack : line.items) {
                    context.drawItem(stack, (int) (x + offsetX), (int) drawY);
                    offsetX += FONT_SIZE * scale;
                }

                context.drawTextWithShadow(tr, line.text, (int) (x + offsetX), (int) drawY,
                        Colors.WHITE);
            }
        }

        /**
         * Recalculates the size of the overlay based on its content.
         */
        private void calcSize() {
            TextRenderer tr = MinecraftClient.getInstance().textRenderer;

            float maxWidth = 0;
            float totalHeight = 0;
            float lineHeight = FONT_SIZE * scale;

            for (LineContent line : templateLines) {
                float lineWidth = (line.items.size() * FONT_SIZE + tr.getWidth(line.text) * scale);
                maxWidth = Math.max(maxWidth, lineWidth);
                totalHeight += lineHeight;
            }

            this.width = maxWidth;
            this.height = totalHeight;
        }
    }
}
