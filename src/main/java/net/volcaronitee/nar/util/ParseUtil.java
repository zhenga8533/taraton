package net.volcaronitee.nar.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Pattern;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

/**
 * Utility class for parsing and cleaning text by removing formatting characters.
 */
public class ParseUtil {
    public static final String PLAYER_PATTERN =
            "(?:\\[\\d+\\] )?(?:\\[[^\\]]*\\+?\\] )?(\\w+)(?: \\[[^\\]]+\\])?";

    public static final Pattern IMAGE_URL_PATTERN =
            Pattern.compile("\\b(?:https?|ftp):\\/\\/[\\w\\d\\-_.~%&?#/=+,]*[\\w\\d\\-_~%&?#/=+]",
                    Pattern.CASE_INSENSITIVE);

    public static boolean isImage(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        String lowerCaseUrl = url.toLowerCase();
        return lowerCaseUrl.endsWith(".jpg") || lowerCaseUrl.endsWith(".jpeg")
                || lowerCaseUrl.endsWith(".png") || lowerCaseUrl.endsWith(".gif")
                || lowerCaseUrl.endsWith(".bmp") || lowerCaseUrl.endsWith(".webp")
                || lowerCaseUrl.endsWith(".tiff") || lowerCaseUrl.endsWith(".ico")
                || lowerCaseUrl.endsWith(".svg");
    }

    /**
     * Checks if the given string is a valid numeric string.
     * 
     * @param str The string to check.
     * @return True if the string is numeric, false otherwise.
     */
    public static boolean isNumeric(String str) {
        return str.matches("[-+]?\\d*\\.?\\d+") || str.matches("[-+]?\\d+\\.\\d*");
    }

    /**
     * Parses an integer from the given string.
     *
     * @param str The string to parse.
     * @return The parsed integer, or 0 if parsing fails.
     */
    public static int parseInt(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Removes formatting characters from the given text.
     * 
     * @param text The text to clean.
     * @return The cleaned text without formatting characters.
     */
    public static String removeFormatting(String text) {
        if (text == null) {
            return "";
        }

        StringBuilder cleanedText = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);

            if (currentChar == '§') {
                if (i + 1 < text.length()) {
                    i++;
                }
            } else {
                cleanedText.append(currentChar);
            }
        }

        return cleanedText.toString();
    }

    /**
     * Modifies the given Text by applying a replacer function to segments of the text that have
     * consistent styles.
     * 
     * @param text The text to modify.
     * @param replacer The function to apply to each segment of text.
     * @return The modified Text.
     */
    public static Text modifyText(OrderedText text, Function<Text, Text> replacer) {
        MutableText reconstructed = Text.empty();
        StringBuilder textBuilder = new StringBuilder();
        AtomicReference<Style> currentStyle = new AtomicReference<>();

        // Use the OrderedText API to iterate through the text segments
        text.accept((index, style, codePoint) -> {
            if (currentStyle.get() == null) {
                currentStyle.set(style);
            } else if (!style.equals(currentStyle.get())) {
                if (!textBuilder.isEmpty()) {
                    Text segmentToModify =
                            Text.literal(textBuilder.toString()).setStyle(currentStyle.get());
                    Text modified = replacer.apply(segmentToModify);
                    reconstructed.append(modified);
                }
                textBuilder.setLength(0);
                currentStyle.set(style);
            }

            textBuilder.append(Character.toChars(codePoint));
            return true;
        });

        // Handle the last segment if it exists
        if (!textBuilder.isEmpty()) {
            Text segmentToModify =
                    Text.literal(textBuilder.toString()).setStyle(currentStyle.get());
            Text modified = replacer.apply(segmentToModify);
            reconstructed.append(modified);
        }

        return reconstructed;
    }
}
