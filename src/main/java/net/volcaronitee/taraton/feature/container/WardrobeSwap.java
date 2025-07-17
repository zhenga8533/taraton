package net.volcaronitee.taraton.feature.container;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.lwjgl.glfw.GLFW;
import com.google.common.reflect.TypeToken;
import com.mojang.brigadier.context.CommandContext;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonJson;
import net.volcaronitee.taraton.config.TaratonList;
import net.volcaronitee.taraton.config.controller.KeyBindController;
import net.volcaronitee.taraton.config.controller.KeyValueController;
import net.volcaronitee.taraton.config.controller.KeyValueController.KeyValuePair;
import net.volcaronitee.taraton.interfaces.TooltipSuppressor;
import net.volcaronitee.taraton.mixin.accessor.HandledScreenAccessor;
import net.volcaronitee.taraton.util.ScheduleUtil;

/**
 * Feature for managing wardrobe swap hotkeys.
 */
public class WardrobeSwap {
    private static final WardrobeSwap INSTANCE = new WardrobeSwap();

    private static final int WARDROBE_INDEX = 36;
    private static final int WARDROBE_SLOTS = 9;

    private static final String TITLE = "Wardrobe Swap Map";
    private static final Text DESCRIPTION = Text.literal("A list of wardrobe swap hotkeys.");
    private static final Type TYPE =
            new TypeToken<KeyValuePair<Integer, KeyValuePair<Integer, Boolean>>>() {}.getType();
    public static final TaratonList WARDROBE_SWAP_MAP = new TaratonList(TITLE, DESCRIPTION,
            "wardrobe_swap_map.json", new String[] {"Slot", "Hotkey"});
    static {
        WARDROBE_SWAP_MAP.setCustomCategory(INSTANCE::createCustomCategory);
        WARDROBE_SWAP_MAP.setSaveCallback(INSTANCE::onSave);
    }

    private static final Map<Integer, Integer> SWAP_HOTKEYS = new HashMap<>();

    private boolean editWardrobe = false;

    /**
     * Private constructor to prevent instantiation.
     */
    private WardrobeSwap() {}

    /**
     * Registers the event listeners for the WardrobeSwap feature.
     */
    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!screen.getTitle().getString().startsWith("Wardrobe")) {
                return;
            }

            if (INSTANCE.editWardrobe && screen instanceof HandledScreen<?>) {
                INSTANCE.editWardrobe = false;
                MinecraftClient.getInstance()
                        .submit(() -> INSTANCE.createScreen((HandledScreen<?>) screen));
            }

            // Register the key press event for wardrobe hotkeys
            ScreenKeyboardEvents.beforeKeyPress(screen).register(INSTANCE::onWardrobeKey);
        });
    }

    /**
     * Returns the singleton instance of WardrobeSwap.
     * 
     * @return The singleton instance of WardrobeSwap.
     */
    public static WardrobeSwap getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the wardrobe screen in the Minecraft client.
     * 
     * @param context The command context containing the source of the command.
     */
    public int setWardrobe(CommandContext<FabricClientCommandSource> context) {
        ScheduleUtil.scheduleCommand("wardrobe", 0);
        editWardrobe = true;
        return 1;
    }

    /**
     * Creates a custom wardrobe screen based on the provided parent screen.
     * 
     * @param parentScreen The parent screen to base the custom wardrobe screen on.
     */
    private void createScreen(HandledScreen<?> screen) {
        MinecraftClient client = MinecraftClient.getInstance();
        WardrobeScreen customScreen = new WardrobeScreen(screen.getScreenHandler(),
                client.player.getInventory(), Text.literal("Edit Wardrobe Swap"), screen);
        client.setScreen(customScreen);
    }

    /**
     * Saves the current wardrobe swap hotkeys to the configuration file.
     */
    private void onSave() {
        SWAP_HOTKEYS.clear();
        List<KeyValuePair<Integer, KeyValuePair<Integer, Boolean>>> typedList =
                getTypedList(WARDROBE_SWAP_MAP.getInstance(), TYPE);

        for (KeyValuePair<Integer, KeyValuePair<Integer, Boolean>> hotkey : typedList) {
            if (hotkey.getValue().getValue()) {
                SWAP_HOTKEYS.put(hotkey.getValue().getKey(), hotkey.getKey());
            }
        }
    }

    /**
     * Handles key presses in the wardrobe screen to swap items based on hotkeys.
     * 
     * @param screen The current screen where the key press occurred.
     * @param keyCode The key code of the pressed key.
     * @param scanCode The scan code of the pressed key.
     * @param modifiers The modifiers applied to the key press.
     */
    private void onWardrobeKey(Screen screen, int keyCode, int scanCode, int modifiers) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.currentScreen != screen) {
            return;
        }

        ScreenHandler handler = client.player.currentScreenHandler;
        if (SWAP_HOTKEYS.containsKey(keyCode)) {
            // Get the slot index from the hotkey mapping
            int slot = SWAP_HOTKEYS.get(keyCode) + WARDROBE_INDEX - 1;
            if (slot < WARDROBE_INDEX || slot >= WARDROBE_INDEX + WARDROBE_SLOTS) {
                return;
            }

            // Perform the slot click action
            client.interactionManager.clickSlot(handler.syncId, slot, 0, SlotActionType.PICKUP,
                    client.player);

            // Schedule escape input
            ScheduleUtil.schedule(() -> {
                screen.close();
            }, 2);
        }
    }

    /**
     * Retrieves a typed list from the configuration, converting JSON elements to the specified
     * type.
     * 
     * @param config The configuration list to retrieve the typed list from.
     * @param type The type to which the JSON elements should be converted.
     * @return A list of KeyValuePair objects representing the typed list.
     */
    private List<KeyValuePair<Integer, KeyValuePair<Integer, Boolean>>> getTypedList(
            TaratonList config, Type type) {
        if (config.customConfig == null) {
            return new ArrayList<>();
        }

        return config.customConfig.stream()
                .map(item -> TaratonJson.GSON
                        .<KeyValuePair<Integer, KeyValuePair<Integer, Boolean>>>fromJson(
                                TaratonJson.GSON.toJsonTree(item), type))
                .collect(Collectors.toList());
    }

    /**
     * Creates a custom category for the wardrobe swap map configuration.
     * 
     * @param defaults The default configuration for the wardrobe swap map.
     * @param config The current configuration for the wardrobe swap map.
     * @return A ConfigCategory representing the wardrobe swap map configuration.
     */
    public ConfigCategory createCustomCategory(TaratonList defaults, TaratonList config) {
        List<KeyValuePair<Integer, KeyValuePair<Integer, Boolean>>> typedList =
                getTypedList(config, TYPE);

        return ConfigCategory.createBuilder().name(Text.literal(TITLE))
                .option(ListOption
                        .<KeyValuePair<Integer, KeyValuePair<Integer, Boolean>>>createBuilder()
                        .name(Text.literal(TITLE))
                        .description(OptionDescription.createBuilder().text(DESCRIPTION).build())
                        .binding(new ArrayList<>(), () -> typedList, newVal -> {
                            config.customConfig = newVal;
                            typedList.clear();
                            typedList.addAll(newVal);
                        })
                        .controller((option) -> KeyValueController.Builder.create(option).ratio(0.4)
                                .keyController("Slot",
                                        opt -> IntegerSliderControllerBuilder.create(opt)
                                                .range(1, 9).step(1))
                                .valueController(null,
                                        (subOption) -> KeyValueController.Builder.create(subOption)
                                                .ratio(2.0 / 3.0)
                                                .keyController("Hotkey",
                                                        KeyBindController.Builder::create)
                                                .valueController("Enabled",
                                                        TickBoxControllerBuilder::create)))
                        .initial(new KeyValuePair<>(1,
                                new KeyValuePair<>(GLFW.GLFW_KEY_UNKNOWN, true)))
                        .minimumNumberOfEntries(WARDROBE_SLOTS).build())
                .build();
    }

    /**
     * Custom screen for the wardrobe swap functionality.
     */
    private static class WardrobeScreen extends HandledScreen<ScreenHandler> {
        private final HandledScreen<?> parent;
        private static final Text INSTRUCTIONS =
                Text.literal("Hover over a slot and press any key to bind it.")
                        .formatted(Formatting.YELLOW);
        private Text tooltip = INSTRUCTIONS.copy();

        /**
         * Constructor for the WardrobeScreen.
         * 
         * @param handler The screen handler for the wardrobe.
         * @param inventory The player's inventory.
         * @param title The title of the screen.
         * @param parent The parent screen to return to when closing this screen.
         */
        public WardrobeScreen(ScreenHandler handler, PlayerInventory inventory, Text title,
                HandledScreen<?> parent) {
            super(handler, inventory, title);
            this.parent = parent;
        }

        @Override
        protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {}

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            TooltipSuppressor parentSuppressor = (TooltipSuppressor) this.parent;
            parentSuppressor.setSuppressTooltip(true);
            this.parent.render(context, mouseX, mouseY, delta);
            parentSuppressor.setSuppressTooltip(false);

            context.drawTooltip(this.textRenderer, tooltip, mouseX, mouseY);
        }

        @Override
        public void close() {
            super.close();
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == 256) { // Escape key
                this.close();
                return super.keyPressed(keyCode, scanCode, modifiers);
            }

            Slot hoveredSlot = ((HandledScreenAccessor) this.parent).getFocusedSlot();
            int slotIndex = hoveredSlot != null && hoveredSlot.id < WARDROBE_INDEX + WARDROBE_SLOTS
                    ? hoveredSlot.id % WARDROBE_SLOTS + 1
                    : -1;
            if (slotIndex == -1) {
                // If not in wardrobe range, do nothing
                return super.keyPressed(keyCode, scanCode, modifiers);
            }

            // If hovered slot is in the wardrobe range, handle the hotkey swap
            TaratonList config = WARDROBE_SWAP_MAP.getInstance();
            List<KeyValuePair<Integer, KeyValuePair<Integer, Boolean>>> typedList =
                    INSTANCE.getTypedList(config, TYPE);

            // Update the hotkey for the selected slot
            typedList.removeIf(item -> item.getKey() == slotIndex);
            typedList.add(new KeyValuePair<>(slotIndex, new KeyValuePair<>(keyCode, true)));
            config.customConfig = typedList;
            WARDROBE_SWAP_MAP.getHandler().save();
            INSTANCE.onSave();

            // Get the key name for display
            String keyName = GLFW.glfwGetKeyName(keyCode, scanCode);
            keyName = keyName != null ? keyName.toUpperCase() : "???";

            // Show confirmation tooltip
            Text confirmation =
                    Text.literal("Set hotkey for slot " + slotIndex + " to " + keyName + "!")
                            .formatted(Formatting.GREEN);
            tooltip = confirmation.copy();
            ScheduleUtil.schedule(() -> {
                if (tooltip.getString().equals(confirmation.getString())) {
                    tooltip = INSTRUCTIONS.copy();
                }
            }, 60);

            // Send confirmation
            Taraton.sendMessage(tooltip);
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }
}
