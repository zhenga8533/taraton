package net.volcaronitee.volcclient.feature.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TextSubstitution {
    private static final Map<String, String> SUBSTITUTION_MAP = new HashMap<>();

    static {
        SUBSTITUTION_MAP.put("a", "§c@");
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
    public static Text textRenderer$redirectAsOrderedText(Text originalText) {
        MutableText substitutedText = Text.empty().copy();

        // Visit each part of the original text and apply substitutions
        originalText.visit((style, textPart) -> {
            String currentProcessedPart = textPart;

            for (Map.Entry<String, String> entry : SUBSTITUTION_MAP.entrySet()) {
                String target = entry.getKey();
                String replacement = entry.getValue();

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
