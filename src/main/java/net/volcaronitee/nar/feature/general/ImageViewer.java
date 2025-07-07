package net.volcaronitee.nar.feature.general;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.util.helper.Parser;

/**
 * Handles image URL bypassing in chat messages by encrypting image URLs.
 */
public class ImageViewer {
    private static final ImageViewer INSTANCE = new ImageViewer();

    private static final int SHIFT_AMOUNT = 3;
    private static final String IMAGE_PREFIX = "nar@";
    private static final Pattern BYPASS_PATTERN = Pattern.compile(IMAGE_PREFIX + "[^ ]+");

    /**
     * Private constructor to prevent instantiation.
     */
    private ImageViewer() {
    }

    public static void register() {
        ClientSendMessageEvents.MODIFY_CHAT.register(INSTANCE::handleImageBypass);
        ClientReceiveMessageEvents.MODIFY_GAME.register(INSTANCE::handleImageViewer);
    }

    /**
     * Handles image URL bypassing in chat messages by encrypting image URLs.
     * 
     * @param message The chat message to process.
     * @return The modified chat message with image URLs encrypted.
     */
    private String handleImageBypass(String message) {
        if (!NarConfig.getHandler().general.imageViewer || message == null || message.isEmpty()) {
            return message;
        }

        Matcher matcher = Parser.IMAGE_URL_PATTERN.matcher(message);
        StringBuilder resultBuilder = new StringBuilder();

        while (matcher.find()) {
            String originalUrl = matcher.group();
            if (!Parser.isImage(originalUrl)) {
                matcher.appendReplacement(resultBuilder, originalUrl);
                continue;
            }

            String editedUrl = IMAGE_PREFIX + encryptImageUrl(originalUrl);
            matcher.appendReplacement(resultBuilder, Matcher.quoteReplacement(editedUrl));
        }

        matcher.appendTail(resultBuilder);
        return resultBuilder.toString();
    }

    /**
     * Handles the decryption of image URLs in received chat messages.
     * 
     * @param message The chat message to process.
     * @param overlay Whether the message is an overlay (e.g., from a mod or
     *                external source).
     * @return The modified chat message with decrypted image URLs.
     */
    private Text handleImageViewer(Text message, boolean overlay) {
        if (!NarConfig.getHandler().general.imageViewer || overlay || message == null) {
            return message;
        }

        Text modifiedText = Parser.modifyText(message.asOrderedText(), segment -> {
            String text = segment.getString();
            Matcher matcher = BYPASS_PATTERN.matcher(text);
            MutableText modifiedSegment = Text.empty();
            int lastIndex = 0;

            // Iterate through all matches and replace them with decrypted URLs
            while (matcher.find()) {
                modifiedSegment
                        .append(Text.literal(text.substring(lastIndex, matcher.start())).setStyle(segment.getStyle()));
                String originalUrl = matcher.group();
                String decryptedUrl = decryptImageUrl(originalUrl.substring(IMAGE_PREFIX.length()));

                // Add the decrypted URL with a clickable style
                try {
                    Style imageStyle = Style.EMPTY.withColor(Formatting.BLUE).withUnderline(true)
                            .withClickEvent(new ClickEvent.OpenUrl(new URI(decryptedUrl)));
                    Text decryptedText = Text.literal(decryptedUrl)
                            .setStyle(imageStyle);
                    modifiedSegment.append(decryptedText);
                } catch (Exception e) {
                    e.printStackTrace();
                    modifiedSegment.append(Text.literal(originalUrl).setStyle(segment.getStyle()));
                }
                lastIndex = matcher.end();
            }

            // Append any remaining text after the last match
            if (lastIndex < text.length()) {
                modifiedSegment.append(Text.literal(text.substring(lastIndex)).setStyle(segment.getStyle()));
            }

            return modifiedSegment;
        });

        return modifiedText;
    }

    /**
     * Encrypts the image URL by shifting characters by the shift amount.
     * 
     * @param url The image URL to encrypt.
     * @return The encrypted image URL.
     */
    private String encryptImageUrl(String url) {
        url = url.replace("https://", "");

        StringBuilder encrypted = new StringBuilder();
        for (char c : url.toCharArray()) {
            encrypted.append((char) (c + SHIFT_AMOUNT));
        }
        return encrypted.toString();
    }

    /**
     * Decrypts the image URL by shifting characters back by the shift amount.
     * 
     * @param url The encrypted image URL to decrypt.
     * @return The decrypted image URL.
     */
    public static String decryptImageUrl(String url) {
        StringBuilder decrypted = new StringBuilder();
        for (char c : url.toCharArray()) {
            decrypted.append((char) (c - SHIFT_AMOUNT));
        }
        return "https://" + decrypted.toString();
    }
}
