package net.volcaronitee.nar.feature.general;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.InputUtil;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.util.TickUtil;

/**
 * Feature to prevent Minecraft from resetting the mouse cursor position when opening or closing
 * screens, allowing for a more consistent mouse experience.
 */
public class NoMouseReset {
    private static final NoMouseReset INSTANCE = new NoMouseReset();

    private int ticksSince = 0;
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private Screen lastScreen = null;

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
        ScreenEvents.BEFORE_INIT.register(INSTANCE::onScreen);
        TickUtil.register(client -> INSTANCE.ticksSince++, 1);
    }

    /**
     * Handles the screen initialization event to set the mouse cursor position
     * 
     * @param client The Minecraft client instance.
     * @param screen The screen being initialized.
     * @param scaledWidth The scaled width of the screen.
     * @param scaledHeight The scaled height of the screen.
     */
    private void onScreen(MinecraftClient client, Screen screen, int scaledWidth,
            int scaledHeight) {
        if (!NarConfig.getHandler().qol.noMouseReset) {
            return;
        }

        // If the last screen was opened very recently, recall the mouse position
        if (INSTANCE.ticksSince < 3
                && (INSTANCE.lastScreen instanceof GenericContainerScreen
                        || INSTANCE.lastScreen instanceof InventoryScreen)) {
            InputUtil.setCursorParameters(client.getWindow().getHandle(), 212993,
                    INSTANCE.lastMouseX, INSTANCE.lastMouseY);
        }

        // Register the screen close event to update the last screen and mouse position
        ScreenEvents.remove(screen).register(closedScreen -> {
            INSTANCE.ticksSince = 0;
            INSTANCE.lastMouseX = client.mouse.getX();
            INSTANCE.lastMouseY = client.mouse.getY();
            INSTANCE.lastScreen = closedScreen;
        });
    }
}
