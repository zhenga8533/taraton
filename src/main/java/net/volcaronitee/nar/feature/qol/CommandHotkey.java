package net.volcaronitee.nar.feature.qol;

import java.util.List;
import org.lwjgl.glfw.GLFW;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.text.Text;
import net.volcaronitee.nar.config.NarList;
import net.volcaronitee.nar.config.controller.KeyBindController;
import net.volcaronitee.nar.config.controller.KeyValueController;
import net.volcaronitee.nar.config.controller.KeyValueController.KeyValuePair;

public class CommandHotkey {
    private static final CommandHotkey INSTANCE = new CommandHotkey();

    private static final String TITLE = "Command Hotkey Map";
    private static final Text DESCRIPTION = Text.literal("A list of command hotkeys.");
    public static final NarList HOTKEY_MAP =
            new NarList(TITLE, Text.literal("A list of command hotkeys."), "hotkey_map.json");
    static {
        HOTKEY_MAP.setSaveCallback(INSTANCE::onSave);
        HOTKEY_MAP.setCustomCategory(INSTANCE::createCustomCategory);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private CommandHotkey() {}

    /**
     * Creates a custom category for the command hotkey map.
     * 
     * @param defaults The default configuration for the command hotkeys.
     * @param config The current configuration for the command hotkeys.
     * @return A ConfigCategory representing the command hotkey map.
     */
    @SuppressWarnings("unchecked")
    public ConfigCategory createCustomCategory(NarList defaults, NarList config) {
        return ConfigCategory.createBuilder().name(Text.literal(TITLE)).option(ListOption
                .<KeyValuePair<Integer, KeyValuePair<String, Boolean>>>createBuilder()
                .name(Text.literal(TITLE))
                .description(OptionDescription.createBuilder().text(DESCRIPTION).build())
                .binding(
                        (List<KeyValuePair<Integer, KeyValuePair<String, Boolean>>>) config.customConfig,
                        () -> (List<KeyValuePair<Integer, KeyValuePair<String, Boolean>>>) config.customConfig,
                        newVal -> config.customConfig = newVal)
                .controller((option) -> KeyValueController.Builder.create(option).ratio(0.4)
                        .keyController("Key", KeyBindController.Builder::create)
                        .valueController(null, (subOption) -> KeyValueController.Builder
                                .create(subOption).ratio(2.0 / 3.0)
                                .keyController("Value", StringControllerBuilder::create)
                                .valueController("Enabled", TickBoxControllerBuilder::create)))
                .initial(new KeyValuePair<>(GLFW.GLFW_KEY_UNKNOWN, new KeyValuePair<>("", true)))
                .build()).build();
    }

    private void onSave() {

    }
}
