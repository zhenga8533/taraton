package net.volcaronitee.taraton.feature.container;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.google.common.reflect.TypeToken;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.context.CommandContext;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonJson;
import net.volcaronitee.taraton.config.TaratonList;
import net.volcaronitee.taraton.config.controller.KeyValueController;
import net.volcaronitee.taraton.config.controller.KeyValueController.KeyValuePair;
import net.volcaronitee.taraton.interfaces.TooltipSuppressor;
import net.volcaronitee.taraton.mixin.accessor.HandledScreenAccessor;
import net.volcaronitee.taraton.util.ScheduleUtil;
import net.volcaronitee.taraton.util.ScreenUtil;

public class SlotBinding {
    private static final SlotBinding INSTANCE = new SlotBinding();

    private static final int INVENTORY_INDEX = 5;
    private static final int INVENTORY_SLOTS = 40;

    private static final int HOTBAR_START_INDEX = 36;
    private static final int HOTBAR_END_INDEX = 45;

    private static final String TITLE = "Slot Binding Map";
    private static final Text DESCRIPTION = Text.literal("A list of slot bindings.");
    private static final Type TYPE =
            new TypeToken<KeyValuePair<Integer, KeyValuePair<Integer, Boolean>>>() {}.getType();
    public static final TaratonList SLOT_BINDING_MAP = new TaratonList(TITLE, DESCRIPTION,
            "slot_binding_map.json", new String[] {"Slot", "Slot"});
    static {
        SLOT_BINDING_MAP.setCustomCategory(INSTANCE::createCustomCategory);
        SLOT_BINDING_MAP.setSaveCallback(INSTANCE::onSave);
    }

    private static final Map<Integer, List<Integer>> SLOT_BINDINGS = new HashMap<>();
    private static final Map<Integer, Integer> SLOT_COLORS = new HashMap<>();

    private boolean editBindings = false;

    /**
     * Private constructor to prevent instantiation.
     */
    private SlotBinding() {}

    /**
     * Registers the event listeners for the SlotBinding feature.
     */
    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            // Check if container is player inventory
            if (!(screen instanceof InventoryScreen)) {
                return;
            }

            // Check for edit mode toggle
            if (INSTANCE.editBindings && screen instanceof HandledScreen<?>) {
                INSTANCE.editBindings = false;
                MinecraftClient.getInstance()
                        .submit(() -> INSTANCE.createScreen((HandledScreen<?>) screen));
            }

            // Register event listeners for the screen
            if (TaratonConfig.getInstance().container.slotBinding) {
                ScreenEvents.afterRender(screen).register(INSTANCE::afterRender);
                ScreenMouseEvents.beforeMouseClick(screen).register(INSTANCE::onMouseClick);
            }
        });
    }

    /**
     * Returns the singleton instance of SlotBinding.
     * 
     * @return The singleton instance of SlotBinding.
     */
    public static SlotBinding getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the slot binding mode to edit mode, allowing users to modify slot bindings.
     * 
     * @param context The command context for the Fabric client command source.
     * @return 1 if the command was successful, 0 otherwise.
     */
    public int setSlotBinding(CommandContext<FabricClientCommandSource> context) {
        ScheduleUtil.schedule(() -> {
            MinecraftClient client = MinecraftClient.getInstance();
            client.setScreen(new InventoryScreen(client.player));
        }, 0);

        editBindings = true;
        return 1;
    }

    /**
     * Creates a screen for editing slot bindings.
     * 
     * @param screen The screen to be edited, typically a HandledScreen.
     */
    private void createScreen(HandledScreen<?> screen) {
        MinecraftClient client = MinecraftClient.getInstance();
        BindingScreen customScreen = new BindingScreen(screen.getScreenHandler(),
                client.player.getInventory(), Text.literal("Edit Slot Bindings"), screen);
        client.setScreen(customScreen);
    }

    /**
     * Saves the current wardrobe swap hotkeys to the configuration file.
     */
    private void onSave() {
        SLOT_BINDINGS.clear();
        List<KeyValuePair<Integer, KeyValuePair<Integer, Boolean>>> typedList =
                getTypedList(SLOT_BINDING_MAP.getInstance(), TYPE);

        for (KeyValuePair<Integer, KeyValuePair<Integer, Boolean>> binding : typedList) {
            if (!binding.getValue().getValue()) {
                continue;
            }

            int source = binding.getKey();
            int target = binding.getValue().getKey();

            // Add forward binding (Source -> Target)
            SLOT_BINDINGS.computeIfAbsent(source, k -> new ArrayList<>()).add(target);

            // Add reverse binding (Target -> Source)
            SLOT_BINDINGS.computeIfAbsent(target, k -> new ArrayList<>()).add(source);
        }

        updateSlotColors();
    }

    /**
     * Overrides mouse clicks if targeting a slot binding. If a user Shift-clicks a slot with a
     * single binding, it will swap the items with its bound hotbar/inventory slot.
     *
     * @param screen The screen where the mouse click occurred.
     * @param mouseX The x-coordinate of the mouse click.
     * @param mouseY The y-coordinate of the mouse click.
     * @param button The mouse button that was clicked.
     * @return False if the click was handled and should be cancelled, true otherwise.
     */
    private boolean onMouseClick(Screen screen, double mouseX, double mouseY, int button) {
        // We only care about Shift + Left Click on a container screen
        if (!Screen.hasShiftDown() || button != 0
                || !(screen instanceof HandledScreen<?> handledScreen)) {
            return true;
        }

        // Check if the screen has slot bindings
        HandledScreenAccessor accessor = (HandledScreenAccessor) handledScreen;
        Slot hoveredSlot = accessor.getFocusedSlot();

        if (hoveredSlot == null || !SLOT_BINDINGS.containsKey(hoveredSlot.id)) {
            return true;
        }

        List<Integer> targets = SLOT_BINDINGS.get(hoveredSlot.id);

        // Check that there is exactly one binding. Do nothing if there are multiple.
        if (targets == null || targets.size() != 1) {
            Taraton.sendMessage(Text.literal("You can only swap items with a single binding!")
                    .formatted(Formatting.RED));
            return true;
        }

        // Swap logic
        int sourceSlotId = hoveredSlot.id;
        int targetSlotId = targets.get(0);

        // Standard player inventory slot IDs
        int inventorySlotId;
        int hotbarSlotIndex;

        // Determine which slot is in the hotbar and which is in the main inventory.
        if (sourceSlotId >= HOTBAR_START_INDEX && sourceSlotId <= HOTBAR_END_INDEX) {
            hotbarSlotIndex = sourceSlotId - HOTBAR_START_INDEX;
            inventorySlotId = targetSlotId;
        } else if (targetSlotId >= HOTBAR_START_INDEX && targetSlotId <= HOTBAR_END_INDEX) {
            hotbarSlotIndex = targetSlotId - HOTBAR_START_INDEX;
            inventorySlotId = sourceSlotId;
        } else {
            return true;
        }

        // Get objects needed for the interaction.
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        int syncId = handledScreen.getScreenHandler().syncId;

        // Perform the swap
        interactionManager.clickSlot(syncId, inventorySlotId, hotbarSlotIndex, SlotActionType.SWAP,
                client.player);

        return false;
    }

    /**
     * Handles rendering after the screen has been drawn, highlighting the currently bound slots and
     * rendering lines between hovered slots and their bindings.
     * 
     * @param screen The screen being rendered.
     * @param context The draw context for rendering.
     * @param mouseX The x-coordinate of the mouse cursor.
     * @param mouseY The y-coordinate of the mouse cursor.
     * @param delta The delta time since the last frame.
     */
    private void afterRender(Screen screen, DrawContext context, int mouseX, int mouseY,
            float delta) {
        if (!(screen instanceof HandledScreen<?> handledScreen) || SLOT_BINDINGS.isEmpty()) {
            return;
        }

        HandledScreenAccessor accessor = (HandledScreenAccessor) handledScreen;
        int parentX = accessor.getX();
        int parentY = accessor.getY();

        // Highlight slots
        context.getMatrices().push();
        context.getMatrices().translate(parentX, parentY, 0);
        for (Integer slotIndex : SLOT_BINDINGS.keySet()) {
            int color = SLOT_COLORS.getOrDefault(slotIndex, 0x40FFFFFF);
            Slot slot = handledScreen.getScreenHandler().getSlot(slotIndex);
            ScreenUtil.highlightSlot(context, slot, color, 200);
        }
        context.getMatrices().pop();

        // Draw lines from hovered slot
        Slot hoveredSlot = accessor.getFocusedSlot();
        if (hoveredSlot != null && SLOT_BINDINGS.containsKey(hoveredSlot.id)) {
            List<Integer> boundSlots = SLOT_BINDINGS.get(hoveredSlot.id);
            if (boundSlots != null && !boundSlots.isEmpty()) {

                RenderSystem.lineWidth(2.0f);

                context.getMatrices().push();
                context.getMatrices().translate(parentX, parentY, 0);

                int lineColor = 0xFFFFFF00;
                for (Integer targetSlotIndex : boundSlots) {
                    Slot targetSlot = handledScreen.getScreenHandler().getSlot(targetSlotIndex);
                    ScreenUtil.drawLine(context, hoveredSlot, targetSlot, 401, lineColor);
                }

                context.getMatrices().pop();

                RenderSystem.lineWidth(1.0f);
            }
        }
    }

    /**
     * Updates the slot colors based on the current bindings, generating a unique color for each
     * binding pair.
     */
    private void updateSlotColors() {
        SLOT_COLORS.clear();

        for (Map.Entry<Integer, List<Integer>> entry : SLOT_BINDINGS.entrySet()) {
            int slot1 = entry.getKey();
            List<Integer> boundSlots = entry.getValue();

            if (boundSlots.isEmpty()) {
                continue;
            }

            int slot2 = boundSlots.get(0);

            // Create a canonical representation by ordering the pair.
            int small = Math.min(slot1, slot2);
            int large = Math.max(slot1, slot2);

            // Create a unique 64-bit seed from the pair of slot IDs.
            long seed = (long) small << 32 | large;
            java.util.Random random = new java.util.Random(seed);

            // Generate a well-distributed color using the seeded random generator.
            float r = 0.4f + random.nextFloat() * 0.6f;
            float g = 0.4f + random.nextFloat() * 0.6f;
            float b = 0.4f + random.nextFloat() * 0.6f;

            // Convert float components to integer components
            int red = (int) (r * 255);
            int green = (int) (g * 255);
            int blue = (int) (b * 255);

            // Combine into a final ARGB color with 25% alpha
            int color = 0x40000000 | (red << 16) | (green << 8) | blue;

            SLOT_COLORS.put(slot1, color);
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
     * Creates a screen for editing slot bindings.
     * 
     * @param opt The option containing the slot binding configuration.
     * @return A ControllerBuilder for the slot binding configuration.
     */
    private IntegerFieldControllerBuilder createSlotController(Option<Integer> opt) {
        return IntegerFieldControllerBuilder.create(opt).min(INVENTORY_INDEX)
                .max(INVENTORY_INDEX + INVENTORY_SLOTS);
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
                                .keyController("Slot", INSTANCE::createSlotController)
                                .valueController(null,
                                        (subOption) -> KeyValueController.Builder.create(subOption)
                                                .ratio(2.0 / 3.0)
                                                .keyController("Slot",
                                                        INSTANCE::createSlotController)
                                                .valueController("Enabled",
                                                        TickBoxControllerBuilder::create)))
                        .initial(new KeyValuePair<>(INVENTORY_INDEX,
                                new KeyValuePair<>(HOTBAR_START_INDEX, true)))
                        .build())
                .build();
    }

    /**
     * Custom screen for the wardrobe swap functionality.
     */
    private static class BindingScreen extends HandledScreen<ScreenHandler> {
        private final HandledScreen<?> parent;

        private static final Text INSTRUCTIONS = Text
                .literal("Hover over a slot and press B to bind it.").formatted(Formatting.YELLOW);
        private Text tooltip = INSTRUCTIONS.copy();

        private int currentSlot = -1;

        /**
         * Constructor for the BindingScreen.
         * 
         * @param handler The screen handler for the wardrobe.
         * @param inventory The player's inventory.
         * @param title The title of the screen.
         * @param parent The parent screen to return to when closing this screen.
         */
        public BindingScreen(ScreenHandler handler, PlayerInventory inventory, Text title,
                HandledScreen<?> parent) {
            super(handler, inventory, title);
            this.parent = parent;
        }

        /**
         * Confirms the tooltip to be displayed, allowing it to persist for a short time.
         * 
         * @param tooltip The tooltip text to confirm.
         */
        private void confirmTooltip(Text tooltip) {
            this.tooltip = tooltip.copy();
            Taraton.sendMessage(this.tooltip);

            ScheduleUtil.schedule(() -> {
                if (tooltip.getString().equals(tooltip.getString())) {
                    this.tooltip = INSTRUCTIONS.copy();
                }
            }, 60);
        }

        @Override
        protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {}

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            // Suppress the parent's tooltip rendering and render the parent screen
            TooltipSuppressor parentSuppressor = (TooltipSuppressor) this.parent;
            parentSuppressor.setSuppressTooltip(true);
            this.parent.render(context, mouseX, mouseY, delta);
            parentSuppressor.setSuppressTooltip(false);

            // Handle slot highlighting
            if (currentSlot != -1) {
                HandledScreenAccessor parentAccessor = (HandledScreenAccessor) this.parent;
                int parentX = parentAccessor.getX();
                int parentY = parentAccessor.getY();

                // Get the slot to highlight from the parent handler
                Slot slot = this.parent.getScreenHandler().getSlot(currentSlot);

                if (slot != null) {
                    // Manually apply the parent's coordinate system
                    context.getMatrices().push();
                    context.getMatrices().translate(parentX, parentY, 0);

                    // Draw the highlight at the slot's relative position
                    int color = 0x80FFD700; // 50% transparent gold
                    context.fill(RenderLayer.getGuiOverlay(), slot.x, slot.y, slot.x + 16,
                            slot.y + 16, color);

                    // Restore the original matrix to not affect other rendering
                    context.getMatrices().pop();
                }
            }

            INSTANCE.afterRender(this.parent, context, mouseX, mouseY, delta);
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
            } else if (keyCode != 66) { // Not 'B' key
                return super.keyPressed(keyCode, scanCode, modifiers);
            }

            Slot hoveredSlot = ((HandledScreenAccessor) this.parent).getFocusedSlot();
            int slotIndex = hoveredSlot != null ? hoveredSlot.id : -1;
            if (slotIndex < INVENTORY_INDEX || slotIndex >= INVENTORY_INDEX + INVENTORY_SLOTS) {
                // If not in inventory range, do nothing
                return super.keyPressed(keyCode, scanCode, modifiers);
            }

            // Handle slot binding logic
            if (currentSlot == -1) {
                if (!deleteBinding(slotIndex)) {
                    setBind(slotIndex);
                }
            } else if (currentSlot == slotIndex) {
                resetBind();
            } else {
                createBinding(slotIndex);
            }

            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        /**
         * Sets the current bind index to the specified slot.
         * 
         * @param slot The slot index to bind.
         */
        private void setBind(int slot) {
            currentSlot = slot;
            tooltip = Text
                    .literal("Hover over a slot and press B to bind it to slot " + (slot + 1) + ".")
                    .formatted(Formatting.YELLOW);
        }

        /**
         * Resets the current binding state, clearing the current bind index and
         */
        private void resetBind() {
            currentSlot = -1;
            tooltip = INSTRUCTIONS.copy();
        }

        /**
         * Creates a binding for the specified slot.
         * 
         * @param slot The slot index to bind.
         */
        private void createBinding(int slot) {
            // Check that one slot is a hotbar slot and the other is an inventory slot
            if (!(currentSlot >= HOTBAR_START_INDEX && currentSlot <= HOTBAR_END_INDEX)
                    && !(slot >= HOTBAR_START_INDEX && slot <= HOTBAR_END_INDEX)) {
                Text confirmation =
                        Text.literal("You can only bind a hotbar slot to an inventory slot!")
                                .formatted(Formatting.RED);
                confirmTooltip(confirmation);
                return;
            }

            // If binding a different slot, update the current bind
            TaratonList config = SLOT_BINDING_MAP.getInstance();
            List<KeyValuePair<Integer, KeyValuePair<Integer, Boolean>>> typedList =
                    INSTANCE.getTypedList(config, TYPE);

            // Update the hotkey for the selected slot
            typedList.add(new KeyValuePair<>(currentSlot, new KeyValuePair<>(slot, true)));
            config.customConfig = typedList;
            SLOT_BINDING_MAP.getHandler().save();
            INSTANCE.onSave();

            // Show confirmation message
            Text confirmation =
                    Text.literal("Bound slot " + (slot + 1) + " to slot " + (currentSlot + 1) + "!")
                            .formatted(Formatting.GREEN);
            confirmTooltip(confirmation);

            currentSlot = -1;
        }

        /**
         * Deletes the binding for the specified slot.
         * 
         * @param slot The slot index to unbind.
         * @return True if a binding was removed, false otherwise.
         */
        private boolean deleteBinding(int slot) {
            // Delete all bindings for the specified slot
            TaratonList config = SLOT_BINDING_MAP.getInstance();
            List<KeyValuePair<Integer, KeyValuePair<Integer, Boolean>>> typedList =
                    INSTANCE.getTypedList(config, TYPE);

            boolean removed = typedList
                    .removeIf(pair -> pair.getValue().getKey() == slot || pair.getKey() == slot);
            if (removed) {
                config.customConfig = typedList;
                SLOT_BINDING_MAP.getHandler().save();
                INSTANCE.onSave();

                // Show confirmation message
                Text confirmation =
                        Text.literal("Unbound slot " + (slot + 1) + " from all bindings!")
                                .formatted(Formatting.RED);
                confirmTooltip(confirmation);
            }

            return removed;
        }
    }
}
