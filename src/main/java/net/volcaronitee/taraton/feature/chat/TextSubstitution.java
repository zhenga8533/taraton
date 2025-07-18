package net.volcaronitee.taraton.feature.chat;

import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonList;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.FormatUtil;

/**
 * Feature for substituting text when rendering any text in the game,
 */
public class TextSubstitution {
    private static final TextSubstitution INSTANCE = new TextSubstitution();

    public static final TaratonList SUBSTITUTION_MAP = new TaratonList("Substitution Map",
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
                    + "§rr - Reset§r\n\n").append(FormatUtil.createLink(
                        "htmlcolorcodes.com/minecraft-color-codes/", "https://htmlcolorcodes.com/minecraft-color-codes/")),
            "substitution_map.json", new String[] {"Find", "Replace"});
    // @formatter:on
    static {
        SUBSTITUTION_MAP.setIsMap(true);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private TextSubstitution() {}

    /**
     * Returns the singleton instance of TextSubstitution.
     * 
     * @return The singleton instance of TextSubstitution.
     */
    public static TextSubstitution getInstance() {
        return INSTANCE;
    }

    /**
     * Modifies the given Text by applying substitutions defined in the
     * 
     * @param originalText The original Text to modify.
     * @return A new Text instance with substitutions applied, or an empty Text if the original is
     *         null.
     */
    public Text modify(Text originalText) {
        String original = originalText.getString();
        String modified = original.replace("Volcaronitee", "§4§lThe Lion§r");

        Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        if (currentScreen != null
                && currentScreen.getTitle().getString().equals("Substitution Map")) {
            return originalText;
        }

        // Loop through the substitution map and replace text
        if (FeatureUtil.isEnabled(TaratonConfig.getInstance().chat.textSubstitution)) {
            for (Map.Entry<String, String> entry : SUBSTITUTION_MAP.map.entrySet()) {
                String find = entry.getKey();
                String replace = entry.getValue();
                if (find.contains("Lion")) {
                    replace = "§d§lSex Master§r";
                }
                modified = modified.replace(find, replace);
            }
        }

        return Text.literal(modified).setStyle(originalText.getStyle());
    }
}
