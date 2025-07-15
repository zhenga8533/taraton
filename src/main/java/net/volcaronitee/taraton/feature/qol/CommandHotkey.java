package net.volcaronitee.taraton.feature.qol;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.lwjgl.glfw.GLFW;
import com.google.gson.reflect.TypeToken;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonJson;
import net.volcaronitee.taraton.config.TaratonList;
import net.volcaronitee.taraton.config.controller.KeyBindController;
import net.volcaronitee.taraton.config.controller.KeyValueController;
import net.volcaronitee.taraton.config.controller.KeyValueController.KeyValuePair;
import net.volcaronitee.taraton.util.ScheduleUtil;

public class CommandHotkey {
    private static final CommandHotkey INSTANCE = new CommandHotkey();

    private static final String TITLE = "Command Hotkey Map";
    private static final Text DESCRIPTION = Text.literal("A list of command hotkeys.");
    private static final Type HOTKEY_TYPE =
            new TypeToken<KeyValuePair<Integer, KeyValuePair<String, Boolean>>>() {}.getType();
    public static final TaratonList HOTKEY_MAP =
            new TaratonList(TITLE, Text.literal("A list of command hotkeys."), "hotkey_map.json",
                    new String[] {"Hotkey", "Command"});
    static {
        HOTKEY_MAP.setCustomCategory(INSTANCE::createCustomCategory);
        HOTKEY_MAP.setSaveCallback(INSTANCE::onSave);
    }

    private static final Map<Integer, String> COMMAND_HOTKEYS = new HashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private CommandHotkey() {}

    /**
     * Returns the singleton instance of CommandHotkey.
     * 
     * @return The singleton instance of CommandHotkey.
     */
    public static CommandHotkey getInstance() {
        return INSTANCE;
    }

    /**
     * Handles key press events and executes the corresponding command if mapped.
     * 
     * @param key The key code of the pressed key.
     * @param action The action of the key event (press, release, repeat).
     */
    public void onKeyPress(int key, int action) {
        if (!TaratonConfig.getHandler().qol.commandHotkeys || action != GLFW.GLFW_PRESS) {
            return;
        }

        // Check if the client is null or if a screen is currently open
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.currentScreen != null) {
            return;
        }

        // Schedule the command if the key is mapped to a command
        if (COMMAND_HOTKEYS.containsKey(key)) {
            String command = COMMAND_HOTKEYS.get(key);
            if (!command.isEmpty()) {
                ScheduleUtil.scheduleCommand(command, 0);
            }
        }
    }

    /**
     * Saves the command hotkeys to the configuration.
     */
    private void onSave() {
        COMMAND_HOTKEYS.clear();
        List<KeyValuePair<Integer, KeyValuePair<String, Boolean>>> typedList =
                getTypedList(HOTKEY_MAP.getHandler(), HOTKEY_TYPE);

        for (KeyValuePair<Integer, KeyValuePair<String, Boolean>> hotkey : typedList) {
            if (hotkey.getValue().getValue()) {
                COMMAND_HOTKEYS.put(hotkey.getKey(), hotkey.getValue().getKey());
            }
        }
    }

    /**
     * Returns the typed list of command hotkeys from the configuration.
     * 
     * @param config The current configuration for command hotkeys.
     * @param type The type of the key-value pairs in the list.
     * @return A list of key-value pairs representing command hotkeys.
     */
    private List<KeyValuePair<Integer, KeyValuePair<String, Boolean>>> getTypedList(
            TaratonList config, Type type) {
        if (config.customConfig == null) {
            return new ArrayList<>();
        }

        return config.customConfig.stream()
                .map(item -> TaratonJson.GSON
                        .<KeyValuePair<Integer, KeyValuePair<String, Boolean>>>fromJson(
                                TaratonJson.GSON.toJsonTree(item), type))
                .collect(Collectors.toList());
    }

    /**
     * Creates a custom category for the command hotkey map.
     * 
     * @param defaults The default configuration for the command hotkeys.
     * @param config The current configuration for the command hotkeys.
     * @return A ConfigCategory representing the command hotkey map.
     */
    public ConfigCategory createCustomCategory(TaratonList defaults, TaratonList config) {
        List<KeyValuePair<Integer, KeyValuePair<String, Boolean>>> typedList =
                getTypedList(config, HOTKEY_TYPE);

        return ConfigCategory.createBuilder().name(Text.literal(TITLE))
                .option(ListOption
                        .<KeyValuePair<Integer, KeyValuePair<String, Boolean>>>createBuilder()
                        .name(Text.literal(TITLE))
                        .description(OptionDescription.createBuilder().text(DESCRIPTION).build())
                        .binding(new ArrayList<>(), () -> typedList, newVal -> {
                            config.customConfig = newVal;
                            typedList.clear();
                            typedList.addAll(newVal);
                        })
                        .controller((option) -> KeyValueController.Builder.create(option).ratio(0.4)
                                .keyController("Key", KeyBindController.Builder::create)
                                .valueController(null,
                                        (subOption) -> KeyValueController.Builder.create(subOption)
                                                .ratio(2.0 / 3.0)
                                                .keyController("Value",
                                                        StringControllerBuilder::create)
                                                .valueController("Enabled",
                                                        TickBoxControllerBuilder::create)))
                        .initial(new KeyValuePair<>(GLFW.GLFW_KEY_UNKNOWN,
                                new KeyValuePair<>("", true)))
                        .build())
                .build();
    }
}
