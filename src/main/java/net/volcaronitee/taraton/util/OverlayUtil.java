package net.volcaronitee.taraton.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.lwjgl.glfw.GLFW;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonJson;
import net.volcaronitee.taraton.interfaces.SpecialRender;
import net.volcaronitee.taraton.util.helper.LineContent;

/**
 * Utility class for handling overlays.
 */
public class OverlayUtil {
    private static final Identifier LAYER = Identifier.of(Taraton.MOD_ID, "overlays");

    private static final Map<String, Overlay> OVERLAYS = new java.util.HashMap<>();
    private static boolean globalMoveMode = false;
    private static Overlay currentOverlay = null;

    private static final int MARGIN = 4;

    /**
     * Private constructor to prevent instantiation.
     */
    private OverlayUtil() {}

    /**
     * Initializes the OverlayUtil by registering the overlay rendering callbacks.
     */
    public static void init() {
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer
                .attachLayerBefore(IdentifiedLayer.SCOREBOARD, LAYER, (context, tickCounter) -> {
                    renderOverlays(context, tickCounter, false);
                }));

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.afterRender(screen)
                    .register((renderedScreen, context, mouseX, mouseY, tickDelta) -> {
                        if (renderedScreen instanceof HandledScreen) {
                            renderOverlays(context, tickDelta, true);
                        }
                    });
        });
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
        JsonObject overlayJson = TaratonJson.loadJson("overlays", name.toLowerCase() + ".json");
        int x = overlayJson.has("x") ? overlayJson.get("x").getAsInt() : 100;
        int y = overlayJson.has("y") ? overlayJson.get("y").getAsInt() : 100;
        float scale = overlayJson.has("scale") ? overlayJson.get("scale").getAsFloat() : 1.0f;
        int align = overlayJson.has("align") ? overlayJson.get("align").getAsInt() : 0;

        Overlay overlay = new Overlay(x, y, scale, align, shouldRender, templateLines);
        OVERLAYS.put(name.toLowerCase(), overlay);

        return overlay;
    }

    /**
     * Removes the overlay with the specified name.
     * 
     * @param name The name of the overlay to remove.
     */
    public static void removeOverlay(String name) {
        if (name == null || name.isEmpty() || !OVERLAYS.containsKey(name.toLowerCase())) {
            return;
        }

        OVERLAYS.remove(name.toLowerCase());
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

            TaratonJson.saveJson("overlays", name + ".json", overlayJson);
        }
    }

    /**
     * Resets the overlay with the specified name to its template values.
     * 
     * @param name The name of the overlay to reset.
     */
    public static void resetOverlay(String name) {
        Overlay overlay = OVERLAYS.get(name);
        if (overlay != null) {
            JsonObject templateJson = TaratonJson.loadTemplate("overlays/" + name + ".json");
            if (templateJson == null) {
                return;
            }

            int x = templateJson.has("x") ? templateJson.get("x").getAsInt() : 100;
            int y = templateJson.has("y") ? templateJson.get("y").getAsInt() : 100;
            float scale = templateJson.has("scale") ? templateJson.get("scale").getAsFloat() : 1.0f;

            overlay.x = x;
            overlay.y = y;
            overlay.scale = scale;
        }
    }

    /**
     * Renders all overlays using the provided context.
     * 
     * @param context The context to use for rendering overlays.
     * @param delta The time delta since the last render.
     * @param inContainer Whether the player is currently in a container screen.
     */
    public static void renderOverlays(DrawContext context, float delta, boolean inContainer) {
        if (globalMoveMode) {
            return;
        }

        for (Overlay overlay : OVERLAYS.values()) {
            overlay.render(context, delta, inContainer);
        }
    }

    /**
     * Renders all overlays using the provided context.
     * 
     * @param context The context to use for rendering overlays.
     * @param tickCounter The render tick counter.
     * @param inContainer Whether the player is currently in a container screen.
     */
    public static void renderOverlays(DrawContext context, RenderTickCounter tickCounter,
            boolean inContainer) {
        renderOverlays(context, tickCounter.getDynamicDeltaTicks(), inContainer);
    }

    /**
     * Moves the GUI to a new screen for overlay management.
     * 
     * @param context The command context containing the source of the command.
     */
    public static int moveGui(CommandContext<FabricClientCommandSource> context) {
        globalMoveMode = true;

        // Create a new screen for managing overlays
        MinecraftClient.getInstance().send(() -> {
            OverlayScreen screen = new OverlayScreen();
            context.getSource().getClient().setScreen(screen);
        });

        return 1;
    }

    /**
     * Screen for managing overlays, allowing users to drag and drop overlays around the screen.
     */
    private static class OverlayScreen extends Screen {
        /**
         * Creates a new OverlayScreen instance with the title set to the current Taraton version.
         */
        public OverlayScreen() {
            super(Text.literal("Overlay Management - Taraton " + Taraton.MOD_VERSION));
        }

        @Override
        protected void init() {
            super.init();
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            context.fill(0, 0, this.width, this.height, 0x40007BFF);
            for (Overlay overlay : OVERLAYS.values()) {
                overlay.render(context, delta, true);
            }
            super.render(context, mouseX, mouseY, delta);
        }

        @Override
        public void renderBackground(DrawContext context, int mouseX, int mouseY,
                float deltaTicks) {}

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            boolean hovered = false;
            for (Overlay overlay : OVERLAYS.values()) {
                if (overlay.isMouseOver(mouseX, mouseY) && !hovered) {
                    overlay.hovering = true;
                    hovered = true;
                } else {
                    overlay.hovering = false;
                }
            }
            super.mouseMoved(mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (Overlay overlay : OVERLAYS.values()) {
                if (overlay.isMouseOver(mouseX, mouseY)) {
                    currentOverlay = overlay;
                    currentOverlay.dx = (int) mouseX - overlay.x;
                    currentOverlay.dy = (int) mouseY - overlay.y;
                    break;
                }
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            currentOverlay = null;

            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX,
                double deltaY) {
            if (currentOverlay != null) {
                currentOverlay.x = (int) (mouseX - currentOverlay.dx);
                currentOverlay.y = (int) (mouseY - currentOverlay.dy);
            }

            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (currentOverlay != null) {
                if (keyCode == GLFW.GLFW_KEY_UP) {
                    currentOverlay.y--;
                    return true;
                }
                if (keyCode == GLFW.GLFW_KEY_DOWN) {
                    currentOverlay.y++;
                    return true;
                }
                if (keyCode == GLFW.GLFW_KEY_LEFT) {
                    currentOverlay.x--;
                    return true;
                }
                if (keyCode == GLFW.GLFW_KEY_RIGHT) {
                    currentOverlay.x++;
                    return true;
                }
                if (keyCode == GLFW.GLFW_KEY_EQUAL) { // Plus key
                    currentOverlay.scale += 0.1f;
                    return true;
                }
                if (keyCode == GLFW.GLFW_KEY_MINUS) { // Minus key
                    currentOverlay.scale = Math.max(0.1f, currentOverlay.scale - 0.1f);
                    return true;
                }
            }

            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public void close() {
            globalMoveMode = false;

            for (Overlay overlay : OVERLAYS.values()) {
                overlay.hovering = false;
            }

            OverlayUtil.saveOverlays();
            super.close();
        }
    };

    /**
     * Represents an overlay that can be rendered on the screen, containing lines of content.
     */
    public static class Overlay {
        private int x, y;
        private float scale;
        private int align;

        private boolean onContainer = false;
        private final Supplier<Boolean> shouldRender;
        private final List<LineContent> lines;
        private final List<LineContent> templateLines = new ArrayList<>();

        private int fixedWidth = -1;
        private int fixedHeight = -1;
        private int width = -1;
        private int height = -1;
        private final List<Integer> maxColumnWidths = new ArrayList<>();

        private int dx = 0;
        private int dy = 0;
        private boolean hovering = false;

        private SpecialRender specialRender = null;

        /**
         * Creates a new Overlay
         * 
         * @param initialX The initial X position of the overlay.
         * @param initialY The initial Y position of the overlay.
         * @param scale The scale factor for the overlay.
         * @param shouldRender A supplier that determines if the overlay should be rendered.
         * @param lines The template lines to be displayed in the overlay.
         */
        public Overlay(int initialX, int initialY, float scale, int align,
                Supplier<Boolean> shouldRender, List<LineContent> lines) {
            this.x = initialX;
            this.y = initialY;
            this.scale = scale;
            this.align = align;
            this.shouldRender = shouldRender;
            this.lines = lines;
            for (LineContent line : lines) {
                this.templateLines.add(new LineContent(line));
            }
        }

        /**
         * Sets a special render function for this overlay.
         * 
         * @param specialRender The special render function to set.
         */
        public void setSpecialRender(SpecialRender specialRender) {
            this.specialRender = specialRender;
        }

        /**
         * Sets whether the overlay is currently being displayed on a container screen.
         * 
         * @param onContainer True if the overlay is on a container screen, false otherwise.
         */
        public void setOnContainer(boolean onContainer) {
            this.onContainer = onContainer;
        }

        /**
         * Sets a fixed size for the overlay, overriding its calculated size.
         * 
         * @param width The fixed width of the overlay.
         * @param height The fixed height of the overlay.
         */
        public void setFixedSize(int width, int height) {
            this.fixedWidth = width;
            this.fixedHeight = height;
            this.width = width;
            this.height = height;
        }

        /**
         * Renders the overlay using the provided context.
         * 
         * @param context The context to use for rendering the overlay.
         * @param delta The time delta since the last render.
         * @param inContainer Whether the player is currently in a container screen.
         */
        private void render(DrawContext context, float delta, boolean inContainer) {
            if (!shouldRender.get() || (onContainer && !inContainer)
                    || (!onContainer && inContainer && !globalMoveMode)) {
                return;
            }

            // Prepare lines to render
            List<LineContent> lines =
                    globalMoveMode && this.lines.isEmpty() ? templateLines : this.lines;
            TextRenderer tr = MinecraftClient.getInstance().textRenderer;

            boolean changed = false;
            for (LineContent line : lines) {
                if (line.isChanged()) {
                    changed = true;
                    line.calculateSize();
                }
            }
            if (changed) {
                calculateSize(lines);
            }

            // Render alignment lines if this is the current overlay
            if (this == currentOverlay) {
                // Draw vertical and horizontal lines
                MinecraftClient client = MinecraftClient.getInstance();
                int screenWidth = client.getWindow().getScaledWidth();
                int screenHeight = client.getWindow().getScaledHeight();
                context.fill(0, y - 1, screenWidth, y, Colors.WHITE);
                context.fill(x - 1, 0, x, screenHeight, Colors.WHITE);

                // Draw position text
                String positionText = String.format("X: %d, Y: %d", x, y);
                context.drawTextWithShadow(tr, Text.literal(positionText), x + 2, y - 11,
                        Colors.WHITE);
            }

            if (specialRender == null || globalMoveMode) {
                // Draw the overlay box
                int boxX1 = (int) x - MARGIN;
                int boxY1 = (int) y - MARGIN;
                int boxX2 = (int) (x + width + MARGIN);
                int boxY2 = (int) (y + height + MARGIN);
                int fillColor = hovering ? 0x8000FF00 : 0x80000000;
                int borderColor = hovering ? 0xFF00FF00 : 0xFFDDDDDD;
                context.fill(boxX1, boxY1, boxX2, boxY2, fillColor);
                context.drawBorder(boxX1, boxY1, boxX2 - boxX1, boxY2 - boxY1, borderColor);

                // Render each line of content
                float currentY = y;
                for (LineContent line : lines) {
                    if (line.shouldRender()) {
                        line.draw(context, x, (int) currentY, scale, align, maxColumnWidths);
                        currentY += line.getHeight() * scale;
                    }
                }
            } else if (specialRender != null) {
                // If special render is set, use it
                specialRender.render(context, delta);
                return;
            }
        }

        /**
         * Recalculates the size of the overlay based on its content.
         */
        private void calculateSize(List<LineContent> lines) {
            if (fixedWidth > 0 && fixedHeight > 0) {
                this.width = fixedWidth;
                this.height = fixedHeight;
                return;
            }

            // Clear previous max column widths
            int maxColumns = 0;
            for (LineContent line : lines) {
                if (line.shouldRender()) {
                    maxColumns = Math.max(maxColumns, line.getColumns());
                }
            }

            // Initialize max column widths with zeros
            for (int i = 0; i < maxColumns; i++) {
                maxColumnWidths.add(0);
            }
            int totalHeight = 0;

            // Calculate the size of each line and find the maximum column widths
            for (LineContent line : lines) {
                if (line.shouldRender()) {
                    // This loop finds the widest that each column needs to be across all lines.
                    for (int i = 0; i < line.getColumns(); i++) {
                        int columnWidth = line.getColumnWidth(i);
                        maxColumnWidths.set(i, Math.max(maxColumnWidths.get(i), columnWidth));
                    }
                    totalHeight += line.getHeight() * scale;
                }
            }

            // The total width is now the sum of the widest columns.
            this.width = lines.stream().filter(line -> line.shouldRender())
                    .mapToInt(line -> line.getWidth()).max().orElse(0);
            this.height = totalHeight - MARGIN / 2;
        }

        /**
         * Checks if the mouse is over this overlay.
         * 
         * @param mouseX The mouse X position.
         * @param mouseY The mouse Y position.
         * @return True if the mouse is over the overlay, false otherwise.
         */
        private boolean isMouseOver(double mouseX, double mouseY) {
            return mouseX >= x - MARGIN && mouseX <= x + width + MARGIN && mouseY >= y - MARGIN
                    && mouseY <= y + height + MARGIN;
        }

        /**
         * Gets the X position of the overlay.
         * 
         * @return The X position of the overlay.
         */
        public int getX() {
            return this.x;
        }

        /**
         * Gets the Y position of the overlay.
         * 
         * @return The Y position of the overlay.
         */
        public int getY() {
            return this.y;
        }
    }
}
