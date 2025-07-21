package net.volcaronitee.taraton.feature.container;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.mixin.accessor.HandledScreenAccessor;
import net.volcaronitee.taraton.mixin.accessor.ScreenAccessor;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.OverlayUtil;
import net.volcaronitee.taraton.util.OverlayUtil.LineContent;
import net.volcaronitee.taraton.util.OverlayUtil.Overlay;
import net.volcaronitee.taraton.util.ScreenUtil;

/**
 * Feature to add a searchbar to container screens.
 */
public class Searchbar {
    private static final Searchbar INSTANCE = new Searchbar();

    private static final int SEARCHBAR_WIDTH = 192;
    private static final int SEARCHBAR_HEIGHT = 16;

    private static final Overlay OVERLAY = OverlayUtil.createOverlay("searchbar",
            () -> FeatureUtil.isEnabled(TaratonConfig.getInstance().container.searchbar),
            List.of(new LineContent("Searchbar Placeholder", () -> true)));
    static {
        OVERLAY.setFixedSize(SEARCHBAR_WIDTH, SEARCHBAR_HEIGHT);
        OVERLAY.setSpecialRender(INSTANCE::highlightMatches);
        OVERLAY.setOnContainer(true);
    }

    private static TextFieldWidget searchbar;

    private static List<Integer> searchMatches = new ArrayList<>();

    private static List<Runnable> searchbarListeners = new ArrayList<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private Searchbar() {}

    /**
     * Gets the singleton instance of the Searchbar feature.
     * 
     * @return The singleton instance of Searchbar.
     */
    public static Searchbar getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the searchbar feature.
     */
    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!FeatureUtil.isEnabled(TaratonConfig.getInstance().container.searchbar)) {
                return;
            }

            if (screen instanceof HandledScreen) {
                // Initialize the searchbar if it doesn't exist
                if (searchbar == null) {
                    searchbar = new TextFieldWidget(client.textRenderer, 0, 0, SEARCHBAR_WIDTH,
                            SEARCHBAR_HEIGHT, Text.literal("Search..."));
                    searchbar.setChangedListener((text) -> {
                        INSTANCE.updateMatches();
                        for (Runnable listener : searchbarListeners) {
                            listener.run();
                        }
                    });
                }

                // Set the position and add the searchbar to the screen
                searchbar.setX(OVERLAY.getX());
                searchbar.setY(OVERLAY.getY());
                ((ScreenAccessor) screen).invokeAddDrawableChild(searchbar);

                // Register event listeners
                ScreenEvents.remove(screen).register(INSTANCE::onScreenClose);
                ScreenMouseEvents.afterMouseClick(screen).register(INSTANCE::onMouseClick);
                ScreenKeyboardEvents.allowKeyPress(screen).register(INSTANCE::allowKeyPress);
            }
        });
    }

    /**
     * Adds a listener that will be called when the searchbar text changes.
     * 
     * @param listener The listener to add. It will be called whenever the searchbar text changes.
     */
    public static void addSearchbarListener(Runnable listener) {
        if (listener != null) {
            searchbarListeners.add(listener);
        }
    }

    /**
     * Gets the text from the searchbar.
     * 
     * @return The text from the searchbar.
     */
    public static String getText() {
        return searchbar != null ? searchbar.getText() : "";
    }

    /**
     * Highlights the matching slots in the container.
     * 
     * @param context The current DrawContext.
     * @param delta The delta time since the last frame.
     */
    private void highlightMatches(DrawContext context, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!(client.currentScreen instanceof HandledScreen<?> handledScreen)
                || searchMatches.isEmpty()) {
            return;
        }

        // Translate to the parent screen's origin
        HandledScreenAccessor accessor = (HandledScreenAccessor) handledScreen;
        int parentX = accessor.getX();
        int parentY = accessor.getY();

        context.getMatrices().push();
        context.getMatrices().translate(parentX, parentY, 0);

        // Highlight matching slots
        ScreenHandler handler = handledScreen.getScreenHandler();
        for (Slot slot : handler.slots) {
            if (searchMatches.contains(slot.id)) {
                ScreenUtil.highlightSlot(context, slot, 0x80FFFF00); // Semi-transparent yellow
            }
        }

        // Pop the matrix stack to restore previous state
        context.getMatrices().pop();
    }

    /**
     * Gets the list of slot IDs that match the search text.
     */
    private void updateMatches() {
        searchMatches.clear();
        String searchText = getText();
        if (searchText.isEmpty()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !(client.currentScreen instanceof HandledScreen)) {
            return;
        }

        // Get the current screen handler and iterate through its slots
        ScreenHandler handler = client.player.currentScreenHandler;
        for (Slot slot : handler.slots) {
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty()) {
                if (itemMatchesSearch(stack)) {
                    searchMatches.add(slot.id);
                }
            }
        }
    }

    /**
     * Checks if an item matches the search text.
     * 
     * @param stack The item stack to check.
     * @return True if the item matches the search text, false otherwise.
     */
    public boolean itemMatchesSearch(ItemStack stack) {
        String lowerCaseSearch = getText().toLowerCase();
        if (stack.getName().getString().toLowerCase().contains(lowerCaseSearch)) {
            return true;
        }

        // Check lore
        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        if (lore != null) {
            for (Text line : lore.lines()) {
                if (line.getString().toLowerCase().contains(lowerCaseSearch)) {
                    return true;
                }
            }
        }

        // Check enchantments
        return EnchantmentHelper
                .getEnchantments(
                        stack)
                .getEnchantments().stream()
                .anyMatch(entry -> entry.getKey().map(key -> key.getValue().getPath()
                        .replace('_', ' ').toLowerCase().contains(lowerCaseSearch)).orElse(false));
    }

    /**
     * Handles mouse clicks on the screen.
     * 
     * @param screen The screen that received the mouse click event.
     * @param mouseX The x-coordinate of the mouse click.
     * @param mouseY The y-coordinate of the mouse click.
     * @param button The mouse button that was clicked (0 for left, 1 for right, etc.).
     */
    private void onMouseClick(Screen screen, double mouseX, double mouseY, int button) {
        // Unfocus the searchbar if clicking outside of it
        if (searchbar != null && searchbar.isFocused() && !searchbar.isMouseOver(mouseX, mouseY)) {
            searchbar.setFocused(false);
        }

        // Update matches if there is text in the searchbar
        if (!getText().isEmpty()) {
            INSTANCE.updateMatches();
        }
    }

    /**
     * Handles key press events on the screen.
     * 
     * @param screen The screen that received the key press event.
     * @param key The key that was pressed.
     * @param scancode The scancode of the key that was pressed.
     * @param modifiers The modifiers that were pressed along with the key.
     * @return True if the key press should be handled by the screen, false if it was handled by the
     *         searchbar.
     */
    private boolean allowKeyPress(Screen screen, int key, int scancode, int modifiers) {
        if (searchbar != null && searchbar.isFocused() && key != GLFW.GLFW_KEY_ESCAPE) {
            searchbar.keyPressed(key, scancode, modifiers);
            return false;
        }

        return true;
    }

    /**
     * Called when a screen is closed.
     * 
     * @param screen The screen that was closed.
     */
    private void onScreenClose(Screen screen) {
        if (searchbar != null && screen instanceof ScreenAccessor) {
            ((ScreenAccessor) screen).getSelectables().remove(searchbar);
            searchbar.setFocused(false);
            searchbar.setText("");
            searchMatches.clear();
        }
    }
}
