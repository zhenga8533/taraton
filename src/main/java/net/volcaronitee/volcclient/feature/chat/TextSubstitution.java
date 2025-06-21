package net.volcaronitee.volcclient.feature.chat;

import java.util.List;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.controller.KeyValueController.KeyValuePair;
import net.volcaronitee.volcclient.util.ListUtil;

public class TextSubstitution {
    public static final ListUtil SUBSTITUTION_MAP = new ListUtil("Substitution Map",
    // @formatter:off
            "A list of text substitutions to apply in chat messages. The following color and format codes are supported:\n\n"
                    + "§0&0 - Black\n"
                    + "§1&1 - Dark Blue\n"
                    + "§2&2 - Dark Green\n"
                    + "§3&3 - Dark Aqua\n"
                    + "§4&4 - Dark Red\n"
                    + "§5&5 - Dark Purple\n"
                    + "§6&6 - Gold\n"
                    + "§7&7 - Gray\n"
                    + "§8&8 - Dark Gray\n"
                    + "§9&9 - Blue\n"
                    + "§a&a - Green\n"
                    + "§b&b - Aqua\n"
                    + "§c&c - Red\n"
                    + "§d&d - Light Purple\n"
                    + "§e&e - Yellow\n"
                    + "§f&f - White\n"
                    + "§k&k - Obfuscated\n"
                    + "§l&l - Bold\n"
                    + "§m&m - Strikethrough\n"
                    + "§n&n - Underline\n"
                    + "§o&o - Italic\n"
                    + "§r&r - Reset\n",
            "substitution_map.json");
    // @formatter:on

    static {
        SUBSTITUTION_MAP.setIsMap(true);
    }

    public static Text modify(Text originalText) {
        if (originalText == null)
            return Text.empty();

        String original = originalText.getString();
        String modified = original.replace("Volcaronitee", "The Lion");

        List<KeyValuePair<String, String>> map = SUBSTITUTION_MAP.getHandler().map;
        for (KeyValuePair<String, String> entry : map) {
            modified = modified.replace(entry.getKey(), entry.getValue());
        }

        return Text.literal(modified).setStyle(originalText.getStyle());
    }
}
