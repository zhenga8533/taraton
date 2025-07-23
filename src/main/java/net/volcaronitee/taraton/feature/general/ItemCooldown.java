package net.volcaronitee.taraton.feature.general;

import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonList;

public class ItemCooldown {
    private static final ItemCooldown INSTANCE = new ItemCooldown();

    public static final TaratonList ITEM_COOLDOWN_MAP = new TaratonList("item_cooldown_map",
            Text.literal("A list of item cooldowns for the player."), "item_cooldown_map.json",
            new String[] {"UUID", "Cooldown"});

    /**
     * Private constructor to prevent instantiation.
     */
    private ItemCooldown() {}

    public static void register() {

    }
}
