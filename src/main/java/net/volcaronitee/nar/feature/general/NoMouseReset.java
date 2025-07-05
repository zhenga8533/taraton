package net.volcaronitee.nar.feature.general;

import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.volcaronitee.nar.config.NarConfig;

/**
 * Feature to prevent Minecraft from resetting the mouse cursor position when opening or closing
 * screens, allowing for a more consistent mouse experience.
 */
public class NoMouseReset {
    private static final NoMouseReset INSTANCE = new NoMouseReset();

    private long lastScreenOpen = 0;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private NoMouseReset() {}

    /**
     * Returns the singleton instance of NoMouseReset.
     *
     * @return The instance of NoMouseReset.
     */
    public static NoMouseReset getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the NoMouseReset feature to center the mouse cursor.
     */
    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (System.currentTimeMillis() - INSTANCE.lastScreenOpen > 200) {
                INSTANCE.centerMouse();
            }

            ScreenEvents.remove(screen).register(closedScreen -> {
                INSTANCE.lastScreenOpen = System.currentTimeMillis();
            });
        });
    }

    /**
     * Centers the mouse cursor to the middle of the screen.
     */
    private void centerMouse() {
        MinecraftClient client = MinecraftClient.getInstance();
        double centerX = client.getWindow().getWidth() / 2.0;
        double centerY = client.getWindow().getHeight() / 2.0;
        GLFW.glfwSetCursorPos(client.getWindow().getHandle(), centerX, centerY);
    }

    /**
     * Checks if the mouse should be centered based on the configuration.
     * 
     * @return True if the mouse should be centered, false otherwise.
     */
    public boolean shouldCenter() {
        return !NarConfig.getHandler().general.noMouseReset;
    }
}
