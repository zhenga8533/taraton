package net.volcaronitee.volcclient.feature.chat;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class TextSubstitution {

    private static final String TARGET_STRING = "Volcaronitee";
    private static final String REPLACEMENT_STRING = "The Lion";

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

    public static Text chatHud$modifyChatMessage(Text originalMessage) {
        return substituteText(originalMessage);
    }

    public static Text itemStack$applyItemNameSubstitutions(Text originalName) {
        return substituteText(originalName);
    }

    public static Text textRenderer$redirectAsOrderedText(Text originalText) {
        return substituteText(originalText);
    }
}
