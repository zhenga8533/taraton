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
     * Formats a number with commas for better readability.
     * 
     * @param number The number to format, as a double.
     * @return A string representation of the number with commas inserted every three digits.
     */
    public static String commafy(double number) {
        String str = String.valueOf((long) number);
        StringBuilder result = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (i > 0 && (length - i) % 3 == 0) {
                result.append(',');
            }
            result.append(str.charAt(i));
        }
        return result.toString();
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

    /**
     * Converts a time in milliseconds to a human-readable string format.
     * 
     * @param time The time in milliseconds to convert.
     * @return A string representation of the time in days, hours, minutes, and seconds.
     */
    public static String timeToString(long time) {
        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        StringBuilder builder = new StringBuilder();
        if (days > 0) {
            builder.append(days).append("d ");
        }
        if (hours % 24 > 0) {
            builder.append(hours % 24).append("h ");
        }
        if (minutes % 60 > 0) {
            builder.append(minutes % 60).append("m ");
        }
        if (seconds % 60 > 0) {
            builder.append(seconds % 60).append("s");
        }

        return builder.toString().trim();
    }
}
