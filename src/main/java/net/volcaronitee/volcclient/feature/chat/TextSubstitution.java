package net.volcaronitee.volcclient.feature.chat;

import java.util.List;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.controller.KeyValueController.KeyValuePair;
import net.volcaronitee.volcclient.util.ListUtil;

public class TextSubstitution {
    public static final ListUtil SUBSTITUTION_MAP =
            new ListUtil("Substitution Map", "substitution_map.json");

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
