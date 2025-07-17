package net.volcaronitee.taraton.feature.qol;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.InputUtil;
import net.volcaronitee.taraton.config.TaratonConfig;

/**
 * Feature to prevent Minecraft from resetting the mouse cursor position when opening or closing
 * screens, allowing for a more consistent mouse experience.
 */
public class NoMouseReset {
    private static final NoMouseReset INSTANCE = new NoMouseReset();

    private static final long TIME_THRESHOLD = 100; // milliseconds

    private long lastOpen = 0;
    private double lastMouseX = 0;
    private double lastMouseY = 0;

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
        if (!TaratonConfig.getInstance().qol.noMouseReset
                || !(screen instanceof GenericContainerScreen
                        || screen instanceof InventoryScreen)) {
            return;
        }

        // If the last screen was opened very recently, recall the mouse position
        if (System.currentTimeMillis() - lastOpen < TIME_THRESHOLD) {
            InputUtil.setCursorParameters(client.getWindow().getHandle(), 212993, lastMouseX,
                    lastMouseY);
        }

        // Register the screen close event to update the last screen and mouse position
        ScreenEvents.remove(screen).register(closedScreen -> {
            INSTANCE.lastOpen = System.currentTimeMillis();
            INSTANCE.lastMouseX = client.mouse.getX();
            INSTANCE.lastMouseY = client.mouse.getY();
        });
    }
}
