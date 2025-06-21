package net.volcaronitee.volcclient.feature.chat;

import java.util.Optional;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.volcclient.config.controller.KeyValueController;
import net.volcaronitee.volcclient.util.ListUtil;

public class TextSubstitution {
    public static final ListUtil SUBSTITUTION_MAP =
            new ListUtil("Substitution Map", "substitution_map.json");

    static {
        SUBSTITUTION_MAP.setIsMap(true);
    }

    /**
     * Parses legacy color codes (e.g., §c, §l) in the input text and converts them to a Text.
     * 
     * @param text The input text containing legacy color codes.
     * @return A Text object with the appropriate styles applied.
     */
    private static Text parseColorCodes(String text) {
        MutableText result = Text.empty().copy();
        Style currentStyle = Style.EMPTY;

        // Iterate through the text and apply styles based on color codes
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);

            if (c == '§' && i + 1 < text.length()) {
                char code = text.charAt(i + 1);
                Formatting formatting = Formatting.byCode(code);

                // If a valid formatting code is found, update the current style
                if (formatting != null) {
                    if (formatting.isColor()) {
                        currentStyle = currentStyle.withColor(formatting);
                    } else if (formatting.isModifier()) {
                        currentStyle = currentStyle.withExclusiveFormatting(formatting);
                    } else if (formatting == Formatting.RESET) {
                        currentStyle = Style.EMPTY;
                    }
                    i++;
                    continue;
                }
            }

            // Append the character with the current style
            MutableText literalComponent = Text.literal(String.valueOf(c));
            literalComponent.setStyle(currentStyle);
            result.append(literalComponent);
        }

        return result;
    }

    /**
     * Redirect method to substitute text based on the SUBSTITUTION_MAP.
     * 
     * @param originalText The original text to be processed.
     * @return The processed text with substitutions applied.
     */
    public static Text textRenderer$textRendererDrawLayer(Text originalText) {
        MutableText substitutedText = Text.empty().copy();

        // Visit each part of the original text and apply substitutions
        originalText.visit((style, textPart) -> {
            String currentProcessedPart = textPart;

            for (KeyValueController.KeyValuePair<String, String> pair : SUBSTITUTION_MAP.map) {
                String target = pair.getKey();
                String replacement = pair.getValue();

                if (currentProcessedPart.contains(target)) {
                    currentProcessedPart = currentProcessedPart.replace(target, replacement);
                }
            }

            Text parsedWithNewCodes = parseColorCodes(currentProcessedPart);

            MutableText styledComponent = (MutableText) parsedWithNewCodes;
            styledComponent.setStyle(style);
            substitutedText.append(styledComponent);

            return Optional.empty();
        }, originalText.getStyle());

        return substitutedText;
    }
}
