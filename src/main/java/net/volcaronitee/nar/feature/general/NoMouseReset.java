package net.volcaronitee.nar.feature.general;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.volcaronitee.nar.config.NarConfig;

/**
 * Feature to prevent mouse centering when opening consecutive container screens.
 */
public class NoMouseReset {
    private static final NoMouseReset INSTANCE = new NoMouseReset();

    private long lastOpen = -1L;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private NoMouseReset() {}

    /**
     * Singleton instance of NoMouseReset.
     * 
     * @return The singleton instance of NoMouseReset.
     */
    public static NoMouseReset getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the event listener to track the last time a screen was opened.
     */
    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth,
                scaledHeight) -> INSTANCE.lastOpen = System.currentTimeMillis());
    }

    /**
     * Determines whether mouse centering should be allowed based on the last screen open time and
     * current screen state.
     * 
     * @return True if mouse centering should be allowed, false otherwise.
     */
    public boolean shouldCenter() {
        if (!NarConfig.getHandler().general.noMouseReset) {
            return true;
        }

        long timeSinceLastOpen = System.currentTimeMillis() - this.lastOpen;
        boolean isScreenOpenRecently = timeSinceLastOpen <= 150;

        boolean shouldPreventReset =
                isScreenOpenRecently && MinecraftClient.getInstance().currentScreen != null;

        return !shouldPreventReset;
    }
}
