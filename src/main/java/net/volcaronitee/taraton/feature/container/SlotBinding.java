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
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonJson;
import net.volcaronitee.taraton.config.TaratonList;
import net.volcaronitee.taraton.config.controller.KeyValueController;
import net.volcaronitee.taraton.config.controller.KeyValueController.KeyValuePair;

public class SlotBinding {
    private static final SlotBinding INSTANCE = new SlotBinding();

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

    private static final Map<Integer, Integer> SLOT_BINDINGS = new HashMap<>();

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
                // MinecraftClient.getInstance()
                // .submit(() -> INSTANCE.createScreen((HandledScreen<?>) screen));
            }

            // Register the key press event for slot swapping
            ScreenMouseEvents.allowMouseClick(screen).register(INSTANCE::allowMouseClick);
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

    public int setSlotBinding(CommandContext<FabricClientCommandSource> context) {
        editBindings = true;
        return 1;
    }

    /**
     * Saves the current wardrobe swap hotkeys to the configuration file.
     */
    private void onSave() {
        SLOT_BINDINGS.clear();
        List<KeyValuePair<Integer, KeyValuePair<Integer, Boolean>>> typedList =
                getTypedList(SLOT_BINDING_MAP.getInstance(), TYPE);

        for (KeyValuePair<Integer, KeyValuePair<Integer, Boolean>> hotkey : typedList) {
            if (hotkey.getValue().getValue()) {
                SLOT_BINDINGS.put(hotkey.getValue().getKey(), hotkey.getKey());
            }
        }
    }

    /**
     * Overrides mouse clicks if targeting a slot binding.
     * 
     * @param screen The screen where the mouse click occurred.
     * @param mouseX The x-coordinate of the mouse click.
     * @param mouseY The y-coordinate of the mouse click.
     * @param button The mouse button that was clicked.
     * @return True if the mouse click should be allowed, false otherwise.
     */
    private boolean allowMouseClick(Screen screen, double mouseX, double mouseY, int button) {
        return true;
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
        return IntegerFieldControllerBuilder.create(opt).min(5).max(44);
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
                        .initial(new KeyValuePair<>(1,
                                new KeyValuePair<>(GLFW.GLFW_KEY_UNKNOWN, true)))
                        .build())
                .build();
    }
}
