package net.volcaronitee.taraton.util;

import java.net.URI;
import java.net.URISyntaxException;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Utility class for text formatting and constants.
 */
public class FormatUtil {
    /**
     * Private constructor to prevent instantiation.
     */
    private FormatUtil() {}

    /**
     * Creates a clickable link text component.
     * 
     * @param text The display text for the link.
     * @param url The URL to link to.
     * @return A Text component that represents a clickable link.
     */
    public static Text createLink(String text, String url) {
        MutableText textComponent = Text.literal(text);
        textComponent.formatted(Formatting.BLUE, Formatting.UNDERLINE);

        // Attempt to create a URI from the URL string
        try {
            URI uri = new URI(url);

            textComponent
                    .setStyle(textComponent.getStyle().withClickEvent(new ClickEvent.OpenUrl(uri)));
        } catch (URISyntaxException e) {
            textComponent.append(Text.literal(" (Invalid Link!)").formatted(Formatting.RED));
        }

        return textComponent;
    }

    /**
     * Converts an OrderedText to a String by iterating through its components.
     * 
     * @param orderedText
     * @return
     */
    public static String orderedTextToString(OrderedText orderedText) {
        StringBuilder builder = new StringBuilder();
        orderedText.accept((index, style, codePoint) -> {
            builder.append(Character.toChars(codePoint));
            return true;
        });
        return builder.toString();
    }
}
