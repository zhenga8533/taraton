package net.volcaronitee.volcclient.util;

import java.net.URI;
import java.net.URISyntaxException;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Utility class for text formatting and constants.
 */
public class TextUtil {
    public static final Text MOD_TITLE = Text.literal("[").formatted(Formatting.DARK_GRAY)
            .append(Text.literal("Volc Client").formatted(Formatting.GOLD))
            .append(Text.literal("]")).formatted(Formatting.DARK_GRAY);

    /**
     * Private constructor to prevent instantiation.
     */
    private TextUtil() {}

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
            System.err.println("Invalid URL syntax for link: " + url + " Error: " + e.getMessage());
            textComponent.append(Text.literal(" (Invalid Link!)").formatted(Formatting.RED));
        }

        return textComponent;
    }
}
