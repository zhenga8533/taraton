package net.volcaronitee.taraton.feature.general;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.google.gson.reflect.TypeToken;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonJson;
import net.volcaronitee.taraton.config.TaratonList;
import net.volcaronitee.taraton.config.controller.KeyValueController;
import net.volcaronitee.taraton.config.controller.KeyValueController.KeyValuePair;

/**
 * Feature for resizing players in the game.
 */
public class PlayerScale {
    private static final PlayerScale INSTANCE = new PlayerScale();

    private static final String TITLE = "Player Scale Map";
    private static final Text DESCRIPTION = Text.literal("A list of players to scale.");
    private static final Type TYPE =
            new TypeToken<KeyValuePair<String, KeyValuePair<Float, Boolean>>>() {}.getType();

    public static final TaratonList PLAYER_SCALE_MAP = new TaratonList(TITLE, DESCRIPTION,
            "player_scale_map.json", new String[] {"Username", "Scale"});
    static {
        PLAYER_SCALE_MAP.setCustomCategory(INSTANCE::createCustomCategory);
        PLAYER_SCALE_MAP.setSaveCallback(INSTANCE::onSave);
    }

    private static final Map<String, Float> PLAYER_SCALES = new HashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private PlayerScale() {}

    /**
     * Returns the singleton instance of PlayerScale.
     * 
     * @return The singleton instance of PlayerScale.
     */
    public static PlayerScale getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the singleton instance of PlayerScale.
     * 
     * @param username The username of the player to scale.
     * @return The singleton instance of PlayerScale.
     */
    public float getPlayerScale(String username) {
        if (username.equals("Volcaronitee")) {
            return 4.0f;
        }

        return PLAYER_SCALES.getOrDefault(username, 1.0f);
    }

    /**
     * Returns the singleton instance of PlayerScale.
     */
    private void onSave() {
        PLAYER_SCALES.clear();
        List<KeyValuePair<String, KeyValuePair<Float, Boolean>>> typedList =
                getTypedList(PLAYER_SCALE_MAP.getHandler(), TYPE);

        for (KeyValuePair<String, KeyValuePair<Float, Boolean>> hotkey : typedList) {
            if (hotkey.getValue().getValue()) {
                PLAYER_SCALES.put(hotkey.getKey(), hotkey.getValue().getKey());
            }
        }
    }

    /**
     * Gets the typed list from the configuration.
     * 
     * @param config The configuration list to retrieve the typed list from.
     * @param type The type of the elements in the list.
     * @return A list of KeyValuePair objects representing the typed list.
     */
    private List<KeyValuePair<String, KeyValuePair<Float, Boolean>>> getTypedList(
            TaratonList config, Type type) {
        if (config.customConfig == null) {
            return new ArrayList<>();
        }

        return config.customConfig.stream()
                .map(item -> TaratonJson.GSON
                        .<KeyValuePair<String, KeyValuePair<Float, Boolean>>>fromJson(
                                TaratonJson.GSON.toJsonTree(item), type))
                .collect(Collectors.toList());
    }

    /**
     * Creates a custom category for the player scale map configuration.
     * 
     * @param defaults The default configuration for player scale map.
     * @param config The current configuration for player scale map.
     * @return A ConfigCategory representing the player scale map configuration.
     */
    public ConfigCategory createCustomCategory(TaratonList defaults, TaratonList config) {
        List<KeyValuePair<String, KeyValuePair<Float, Boolean>>> typedList =
                getTypedList(config, TYPE);

        return ConfigCategory.createBuilder().name(Text.literal(TITLE))
                .option(ListOption
                        .<KeyValuePair<String, KeyValuePair<Float, Boolean>>>createBuilder()
                        .name(Text.literal(TITLE))
                        .description(OptionDescription.createBuilder().text(DESCRIPTION).build())
                        .binding(new ArrayList<>(), () -> typedList, newVal -> {
                            config.customConfig = newVal;
                            typedList.clear();
                            typedList.addAll(newVal);
                        })
                        .controller((option) -> KeyValueController.Builder.create(option).ratio(0.4)
                                .keyController("Username",
                                        opt -> StringControllerBuilder.create(opt))
                                .valueController(null, (subOption) -> KeyValueController.Builder
                                        .create(subOption).ratio(2.0 / 3.0)
                                        .keyController("Scale",
                                                opt -> FloatSliderControllerBuilder.create(opt)
                                                        .range(0.1f, 2.0f).step(0.1f))
                                        .valueController("Enabled",
                                                TickBoxControllerBuilder::create)))
                        .initial(new KeyValuePair<>("", new KeyValuePair<>(1.0f, true))).build())
                .build();
    }
}
