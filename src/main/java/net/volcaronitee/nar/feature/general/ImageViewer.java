package net.volcaronitee.nar.feature.general;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.text.Text;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.util.helper.Parser;

/**
 * Handles image URL bypassing in chat messages by encrypting image URLs.
 */
public class ImageViewer {
    private static final ImageViewer INSTANCE = new ImageViewer();

    private static final int SHIFT_AMOUNT = -1;
    private static final String IMAGE_PREFIX = "nar@";
    private static final Pattern BYPASS_PATTERN = Pattern.compile(IMAGE_PREFIX + "[^ ]+");

    /**
     * Private constructor to prevent instantiation.
     */
    private ImageViewer() {}

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

    private Text handleImageViewer(Text message, boolean overlay) {
        if (!NarConfig.getHandler().general.imageViewer || overlay || message == null) {
            return message;
        }

        // TODO

        return message;
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
