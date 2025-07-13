package net.volcaronitee.nar.feature.qol;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.util.ScheduleUtil;

/**
 * Feature that automatically confirms fusions in the "Confirm Fusion" screen.
 */
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
        ScreenEvents.AFTER_INIT.register(INSTANCE::onScreenOpen);
    }

    /**
     * Called when a screen is opened.
     * 
     * @param client The Minecraft client instance.
     * @param screen The screen that was opened.
     * @param width The width of the screen.
     * @param height The height of the screen.
     */
    private void onScreenOpen(MinecraftClient client, Screen screen, int width, int height) {
        if (!NarConfig.getHandler().qol.autoFusion
                || !screen.getTitle().getString().equals(SCREEN_TITLE)) {
            return;
        }

        ScheduleUtil.schedule(() -> {
            if (client.currentScreen == null
                    || !client.currentScreen.getTitle().getString().equals(SCREEN_TITLE)) {
                return;
            }

            // Click the fusion slot if it contains the expected item
            ScreenHandler handler = client.player.currentScreenHandler;
            ItemStack outputStack = handler.getSlot(FUSION_INDEX).getStack();
            if (outputStack.getItem() != Items.LIME_TERRACOTTA) {
                System.out.println(outputStack.getItem());
                return;
            }

            client.interactionManager.clickSlot(handler.syncId, FUSION_INDEX, 0,
                    SlotActionType.PICKUP, client.player);
        }, 5);
    }
}
