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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonJson;
import net.volcaronitee.taraton.config.TaratonList;
import net.volcaronitee.taraton.config.controller.KeyBindController;
import net.volcaronitee.taraton.config.controller.KeyValueController;
import net.volcaronitee.taraton.config.controller.KeyValueController.KeyValuePair;
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

    /**
     * Private constructor to prevent instantiation.
     */
    private WardrobeSwap() {}

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
        ScheduleUtil.schedule(() -> {
            MinecraftClient client = MinecraftClient.getInstance();
            Screen currentScreen = client.currentScreen;

            // Check if the base wardrobe screen is open
            if (currentScreen instanceof HandledScreen<?>) {
                HandledScreen<?> parentScreen = (HandledScreen<?>) currentScreen;

                // Create and set the custom wardrobe screen
                WardrobeScreen customScreen = new WardrobeScreen(parentScreen.getScreenHandler(),
                        client.player.getInventory(), Text.literal("Wardrobe Swap"), parentScreen);
                client.setScreen(customScreen);
            }
        }, 1);

        return 1;
    }

    /**
     * Saves the current wardrobe swap hotkeys to the configuration file.
     */
    private void onSave() {
        SWAP_HOTKEYS.clear();
        List<KeyValuePair<Integer, KeyValuePair<Integer, Boolean>>> typedList =
                getTypedList(WARDROBE_SWAP_MAP.getHandler(), TYPE);

        for (KeyValuePair<Integer, KeyValuePair<Integer, Boolean>> hotkey : typedList) {
            if (hotkey.getValue().getValue()) {
                SWAP_HOTKEYS.put(hotkey.getValue().getKey(), hotkey.getKey());
            }
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
                        .build())
                .build();
    }

    /**
     * Custom screen for the wardrobe swap functionality.
     */
    private static class WardrobeScreen extends HandledScreen<ScreenHandler> {
        private final Screen parent;

        /**
         * Constructor for the WardrobeScreen.
         * 
         * @param handler The screen handler for the wardrobe.
         * @param inventory The player's inventory.
         * @param title The title of the screen.
         * @param parent The parent screen to return to when closing this screen.
         */
        public WardrobeScreen(ScreenHandler handler, PlayerInventory inventory, Text title,
                Screen parent) {
            super(handler, inventory, title);
            this.parent = parent;
        }

        @Override
        public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
            context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        }

        @Override
        protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {}

        @Override
        public void close() {
            this.client.setScreen(this.parent);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == 256) { // Escape key
                this.close();
                return true;
            }

            Slot hoveredSlot = this.focusedSlot;
            int slotIndex = hoveredSlot != null ? hoveredSlot.id : -1;

            // If hovered slot is in the wardrobe range, handle the hotkey swap
            if (slotIndex >= WARDROBE_INDEX && slotIndex < WARDROBE_INDEX + WARDROBE_SLOTS) {
                TaratonList config = WARDROBE_SWAP_MAP.getHandler();
                List<KeyValuePair<Integer, KeyValuePair<Integer, Boolean>>> typedList =
                        INSTANCE.getTypedList(config, TYPE);

                // Update the hotkey for the selected slot
                typedList.removeIf(item -> item.getKey() == slotIndex);
                typedList.add(new KeyValuePair<>(slotIndex, new KeyValuePair<>(keyCode, true)));
                config.customConfig = typedList;
                config.onSave(config);
            }

            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }
}
