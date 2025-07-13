package net.volcaronitee.nar.feature.qol;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.util.ScheduleUtil;

public class AutoFusion {
    private static final AutoFusion INSTANCE = new AutoFusion();

    private static final String SCREEN_TITLE = "Confirm Fusion";

    private static final int FUSION_INDEX = 33;

    /**
     * Private constructor to prevent instantiation.
     */
    private AutoFusion() {}

    /**
     * Registers the event listeners for the AutoFusion.
     */
    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            ScreenMouseEvents.afterMouseClick(screen).register(INSTANCE::onMouseClick);
        });
    }

    /**
     * Checks if the current screen is the valid "Confirm Fusion" screen.
     * 
     * @return True if the current screen is the "Confirm Fusion" screen, false otherwise.
     */
    private boolean isValidScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.currentScreen == null) {
            return false;
        }

        String screenTitle = client.currentScreen.getTitle().getString();
        return screenTitle.equals(SCREEN_TITLE);
    }

    /**
     * Handles mouse click events in the screen.
     * 
     * @param screen The current screen.
     * @param mouseX The X coordinate of the mouse.
     * @param mouseY The Y coordinate of the mouse.
     * @param button The mouse button clicked.
     */
    private void onMouseClick(Screen screen, double mouseX, double mouseY, int button) {
        if (!NarConfig.getHandler().qol.autoFusion || button != 0 || !isValidScreen()) {
            return;
        }

        // Schedule picking up output after 5 ticks (0.25 seconds)
        ScheduleUtil.schedule(() -> {
            if (!isValidScreen()) {
                return;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            ScreenHandler handler = client.player.currentScreenHandler;
            ItemStack outputStack = handler.getSlot(FUSION_INDEX).getStack();
            if (outputStack.getItem() != Items.LIME_TERRACOTTA) {
                return;
            }

            client.interactionManager.clickSlot(handler.syncId, FUSION_INDEX, button,
                    SlotActionType.PICKUP, client.player);
        }, 5);
    }
}
