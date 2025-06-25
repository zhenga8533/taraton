package net.volcaronitee.volcclient.feature.chat;

import java.util.List;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.controller.KeyValueController.KeyValuePair;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.ListUtil;
import net.volcaronitee.volcclient.util.TextUtil;

/**
 * Feature for substituting text when rendering any text in the game,
 */
public class TextSubstitution {
    public static final ListUtil SUBSTITUTION_MAP = new ListUtil("Substitution Map",
    // @formatter:off
            Text.literal(
            "A list of text substitutions to apply in chat messages.\n"
                    + "The following color and format codes are supported:\n\n"
                    + "§00 - Black§r\n"
                    + "§11 - Dark Blue§r\n"
                    + "§22 - Dark Green§r\n"
                    + "§33 - Dark Aqua§r\n"
                    + "§44 - Dark Red§r\n"
                    + "§55 - Dark Purple§r\n"
                    + "§66 - Gold§r\n"
                    + "§77 - Gray§r\n"
                    + "§88 - Dark Gray§r\n"
                    + "§99 - Blue§r\n"
                    + "§aa - Green§r\n"
                    + "§bb - Aqua§r\n"
                    + "§cc - Red§r\n"
                    + "§dd - Light Purple§r\n"
                    + "§ee - Yellow§r\n"
                    + "§ff - White§r\n"
                    + "§kk - Obfuscated§r\n"
                    + "§ll - Bold§r\n"
                    + "§mm - Strikethrough§r\n"
                    + "§nn - Underline§r\n"
                    + "§oo - Italic§r\n"
                    + "§rr - Reset§r\n\n").append(TextUtil.createLink(
                        "htmlcolorcodes.com/minecraft-color-codes/", "https://htmlcolorcodes.com/minecraft-color-codes/")),
            "substitution_map.json", null, null);
    // @formatter:on

    static {
        SUBSTITUTION_MAP.setIsMap(true);
    }

    /**
     * Modifies the given Text by applying substitutions defined in the
     * 
     * @param originalText The original Text to modify.
     * @return A new Text instance with substitutions applied, or an empty Text if the original is
     *         null.
     */
    public static Text modify(Text originalText) {
        String original = originalText.getString();
        String modified = original.replace("Volcaronitee", "§6The Lion§r");

        if (ConfigUtil.getHandler().chat.textSubstitution) {
            List<KeyValuePair<String, String>> map = SUBSTITUTION_MAP.getHandler().map;
            for (KeyValuePair<String, String> entry : map) {
                modified = modified.replace(entry.getKey(), entry.getValue());
            }
        }

        return Text.literal(modified).setStyle(originalText.getStyle());
    }
}
