package net.volcaronitee.nar.feature.qol;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.util.ScheduleUtil;

/**
 * AutoSalvage automates the process of salvaging attributes in the "Attribute Transfer" screen.
 */
public class AutoSalvage {
    private static final AutoSalvage INSTANCE = new AutoSalvage();

    private static final String SCREEN_TITLE = "Attribute Transfer";

    private static final int INPUT_INDEX = 13;
    private static final int ANVIL_INDEX = 22;
    private static final int OUTPUT_INDEX = 31;

    /**
     * Private constructor to prevent instantiation.
     */
    private AutoSalvage() {}

    /**
     * Registers the event listeners for the AutoSalvage.
     */
    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            ScreenMouseEvents.afterMouseClick(screen).register(
                    (clickedScreen, mouseX, mouseY, button) -> INSTANCE.onMouseClick(button));
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
    private void onMouseClick(int button) {
        if (!NarConfig.getHandler().qol.autoSalvage || button != 0 || !isValidScreen()) {
            return;
        }

        // Schedule anvil click after 10 ticks (0.5 seconds)
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

            // Schedule picking up output after 15 ticks (0.75 seconds)
            ScheduleUtil.schedule(INSTANCE::pickupOutput, 15);
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

        // Schedule placing in inventory after 10 ticks (0.5 seconds)
        ScheduleUtil.schedule(INSTANCE::releaseOutput, 10);
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
        ItemStack heldStack = handler.getCursorStack();
        if (heldStack == null || heldStack.isEmpty()) {
            return;
        }

        // Find first empty slot in inventory
        for (int i = 54; i < handler.slots.size(); i++) {
            if (handler.getSlot(i).getStack().isEmpty()) {
                client.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.PICKUP,
                        client.player);
                break;
            }
        }

        // Schedule picking up next input after 5 ticks (0.25 seconds)
        ScheduleUtil.schedule(INSTANCE::pickupInput, 5);
    }

    /**
     * Picks up the next valid input item from the player's inventory.
     */
    private void pickupInput() {
        if (!isValidScreen()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ScreenHandler handler = client.player.currentScreenHandler;

        // Find first empty slot in inventory
        boolean found = false;
        for (int i = 54; i < handler.slots.size(); i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            if (stack == null || stack.isEmpty()) {
                continue;
            }

            // Get the custom data component from the item stack
            NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
            if (customData == null) {
                continue;
            }
            NbtCompound nbt = customData.copyNbt();

            // Pick up the item if it has attributes
            if (nbt.contains("attributes")) {
                client.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.PICKUP,
                        client.player);
                found = true;
                break;
            }
        }

        // Schedule releasing input after 15 ticks (0.75 seconds) if found
        if (found) {
            ScheduleUtil.schedule(INSTANCE::releaseInput, 15);
        }
    }

    /**
     * Releases the held input item into the input slot of the screen.
     */
    private void releaseInput() {
        if (!isValidScreen()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ScreenHandler handler = client.player.currentScreenHandler;

        // Place in input slot
        client.interactionManager.clickSlot(handler.syncId, INPUT_INDEX, 0, SlotActionType.PICKUP,
                client.player);

        // Cycle after 5 ticks (0.25 seconds)
        ScheduleUtil.schedule(() -> INSTANCE.onMouseClick(0), 5);
    }
}
