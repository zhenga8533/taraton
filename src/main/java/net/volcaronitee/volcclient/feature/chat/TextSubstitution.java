package net.volcaronitee.volcclient.feature.chat;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class TextSubstitution {
    private static final String TARGET_STRING = "Volcaronitee";
    private static final String REPLACEMENT_STRING = "The Lion";

    /**
     * Substitutes occurrences of a specific string in the provided text with another string.
     * 
     * @param originalText The original text to be modified.
     * @return The modified text with substitutions applied.
     */
    private static Text substituteText(Text originalText) {
        if (!originalText.getString().contains(TARGET_STRING)) {
            return originalText;
        }

        MutableText newText = Text.empty();

        originalText.getSiblings().forEach(sibling -> {
            String content = sibling.getString();
            if (content.contains(TARGET_STRING)) {
                String replacedContent = content.replaceAll(TARGET_STRING, REPLACEMENT_STRING);
                newText.append(Text.literal(replacedContent).setStyle(sibling.getStyle()));
            } else {
                newText.append(Text.literal(content).setStyle(sibling.getStyle()));
            }
        });

        return newText;
    }

    /**
     * Redirects the chat message modification to apply substitutions.
     * 
     * @param originalMessage The original chat message text to be modified.
     * @return The modified chat message text with substitutions applied.
     */
    public static Text chatHud$modifyChatMessage(Text originalMessage) {
        return substituteText(originalMessage);
    }

    /**
     * Redirects the item name rendering to apply substitutions.
     * 
     * @param originalName The original item name text.
     * @return The modified item name text with substitutions applied.
     */
    public static Text itemStack$applyItemNameSubstitutions(Text originalName) {
        return substituteText(originalName);
    }

    /**
     * Redirects the text renderer to apply substitutions for ordered text.
     * 
     * @param originalText The original text to be rendered.
     * @return The modified text with substitutions applied.
     */
    public static Text textRenderer$redirectAsOrderedText(Text originalText) {
        return substituteText(originalText);
    }
}
