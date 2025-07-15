package net.volcaronitee.taraton.feature.general;

import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonList;

public class PlayerEditor {
    private static final PlayerEditor INSTANCE = new PlayerEditor();

    public static final TaratonList PLAYER_EDITOR_LIST = new TaratonList("Player Editor Map",
            Text.literal("A list of players to apply render transformations to."),
            "player_editor_map.json", new String[] {"Username", "Transformations"});

    /**
     * Private constructor to prevent instantiation.
     */
    private PlayerEditor() {}
}
