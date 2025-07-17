package net.volcaronitee.taraton.feature.qol;

import java.util.HashSet;
import java.util.Set;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.mixin.accessor.HandledScreenAccessor;

/**
 * Feature that allows players to drag the mouse while holding Shift to quickly
 */
public class DragShiftClick {
    private static boolean isDragging = false;
    private static final Set<Slot> draggedSlots = new HashSet<>();

    /**
     * Registers the necessary event listeners to enable the drag-to-shift-click feature.
     */
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(DragShiftClick::onClientTick);
    }

    /**
     * Called every client tick to manage the dragging logic.
     * 
     * @param client The Minecraft client instance.
     */
    private static void onClientTick(MinecraftClient client) {
        // Ensure the feature is enabled and we have a valid window and screen.
        if (!TaratonConfig.getInstance().qol.dragShiftClick || client.getWindow() == null
                || client.currentScreen == null) {
            if (isDragging) {
                isDragging = false;
                draggedSlots.clear();
            }
            return;
        }

        long windowHandle = client.getWindow().getHandle();

        // Use the correct GLFW function for mouse buttons
        boolean isLeftMouseDown = GLFW.glfwGetMouseButton(windowHandle,
                GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        if (!isLeftMouseDown) {
            if (isDragging) {
                // If we were dragging, reset the state on mouse release.
                isDragging = false;
                draggedSlots.clear();
            }
            return;
        }

        // We need to be in a HandledScreen to have slots.
        if (!(client.currentScreen instanceof HandledScreen<?> screen)) {
            return;
        }

        // This check for keyboard keys is correct.
        boolean isShiftDown = InputUtil.isKeyPressed(windowHandle, GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputUtil.isKeyPressed(windowHandle, GLFW.GLFW_KEY_RIGHT_SHIFT);

        if (!isShiftDown) {
            if (isDragging) {
                isDragging = false;
                draggedSlots.clear();
            }
            return;
        }

        isDragging = true;
        Slot hoveredSlot = ((HandledScreenAccessor) screen).getFocusedSlot();

        // Handle the slot if it's not null and hasn't been handled yet.
        if (hoveredSlot != null && !draggedSlots.contains(hoveredSlot)) {
            draggedSlots.add(hoveredSlot);
            handleClick(client, screen, hoveredSlot);
        }
    }

    /**
     * Simulates a shift-click on the given slot.
     * 
     * @param client The Minecraft client instance.
     * @param screen The current HandledScreen.
     * @param slot The slot to click.
     */
    private static void handleClick(MinecraftClient client, HandledScreen<?> screen, Slot slot) {
        if (client.interactionManager == null || client.player == null) {
            return;
        }

        // This is the same action that Minecraft sends when you Shift + Left Click an item.
        client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slot.id, 0,
                SlotActionType.QUICK_MOVE, client.player);
    }
}
