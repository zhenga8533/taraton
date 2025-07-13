package net.volcaronitee.nar.feature.crimson_isle;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.volcaronitee.nar.util.ScheduleUtil;

/**
 * AttributeSalvager automates the process of salvaging attributes in the "Attribute Transfer"
 * screen.
 */
public class AttributeSalvager {
    private static final AttributeSalvager INSTANCE = new AttributeSalvager();

    private static final String SCREEN_TITLE = "Attribute Transfer";

    private static final int ANVIL_INDEX = 22;
    private static final int OUTPUT_INDEX = 31;

    /**
     * Private constructor to prevent instantiation.
     */
    private AttributeSalvager() {}

    /**
     * Registers the event listeners for the AttributeSalvager.
     */
    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            ScreenMouseEvents.afterMouseClick(screen).register(INSTANCE::onMouseClick);
        });
    }

    /**
     * Checks if the current screen is the valid "Attribute Transfer" screen.
     * 
     * @return True if the current screen is valid, false otherwise.
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
     * @param button The mouse button that was clicked.
     */
    private void onMouseClick(Screen screen, double mouseX, double mouseY, int button) {
        if (button != 0 || !isValidScreen()) {
            return;
        }

        ScheduleUtil.schedule(() -> {
            if (!isValidScreen()) {
                return;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            ScreenHandler handler = client.player.currentScreenHandler;
            ItemStack outputStack = handler.getSlot(OUTPUT_INDEX).getStack();
            if (outputStack.getItem() == Items.BARRIER) {
                return;
            }

            client.interactionManager.clickSlot(handler.syncId, ANVIL_INDEX, button,
                    SlotActionType.PICKUP, client.player);

            // Schedule pickup after 10 ticks (0.5 seconds)
            ScheduleUtil.schedule(INSTANCE::pickupOutput, 20);
        }, 10);
    }

    /**
     * Picks up the output item from the output slot and places it in the player's inventory.
     */
    private void pickupOutput() {
        if (!isValidScreen()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ScreenHandler handler = client.player.currentScreenHandler;
        ItemStack outputStack = handler.getSlot(OUTPUT_INDEX).getStack();
        if (outputStack.getItem() == Items.BARRIER) {
            return;
        }

        client.interactionManager.clickSlot(handler.syncId, OUTPUT_INDEX, 0, SlotActionType.PICKUP,
                client.player);

        // Schedule placing in inventory after 20 ticks (1 second)
        ScheduleUtil.schedule(INSTANCE::releaseOutput, 20);
    }

    /**
     * Releases the held item into the first available slot in the player's inventory.
     */
    private void releaseOutput() {
        if (!isValidScreen()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ScreenHandler handler = client.player.currentScreenHandler;
        ItemStack heldStack = client.player.getMainHandStack();
        if (heldStack.isEmpty()) {
            return;
        }

        // Find first empty slot in inventory
        for (int i = 54; i < handler.slots.size(); i++) {
            if (handler.getSlot(i).getStack().isEmpty()) {
                client.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.PICKUP,
                        client.player);
                return;
            }
        }
    }
}
