package net.volcaronitee.nar.feature.general;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.util.ParseUtil;
import net.volcaronitee.nar.util.RequestUtil;

/**
 * Handles image URL bypassing in chat messages by encrypting image URLs.
 */
public class ImagePreview {
    private static final ImagePreview INSTANCE = new ImagePreview();

    private static final Identifier LAYER = Identifier.of(NotARat.MOD_ID, "image_preview");
    private static final Identifier TEXTURE_ID =
            Identifier.of(NotARat.MOD_ID, "image_preview_texture");
    private static final int SHIFT_AMOUNT = 3;
    private static final String IMAGE_PREFIX = "nar@";
    private static final Pattern BYPASS_PATTERN = Pattern.compile(IMAGE_PREFIX + "[^ ]+");

    private NativeImageBackedTexture dynamicTexture;
    private NativeImage dynamicImage;
    private boolean isLoading = false;
    private boolean hasLoaded = false;

    /**
     * Private constructor to prevent instantiation.
     */
    private ImagePreview() {}

    /**
     * Gets the singleton instance of ImagePreview.
     * 
     * @return The singleton instance of ImagePreview.
     */
    public static ImagePreview getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the image preview feature to handle chat messages.
     */
    public static void register() {
        ClientSendMessageEvents.MODIFY_CHAT.register(INSTANCE::handleImageBypass);
        ClientReceiveMessageEvents.MODIFY_GAME.register(INSTANCE::handleImagePreview);
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer
                .attachLayerAfter(IdentifiedLayer.CHAT, LAYER, INSTANCE::renderImagePreview));
    }

    /**
     * Renders the image preview on the HUD.
     * 
     * @param context The draw context for rendering.
     * @param tickCounter The render tick counter for timing.
     */
    private void renderImagePreview(DrawContext context, RenderTickCounter tickCounter) {
        if (!NarConfig.getHandler().general.imagePreview) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (hasLoaded && dynamicTexture != null && dynamicImage != null) {
            int windowWidth = context.getScaledWindowWidth();
            int windowHeight = context.getScaledWindowHeight();

            // Calculate mouse position relative to the window size
            int mouseX = (int) (client.mouse.getX() * windowWidth / client.getWindow().getWidth());
            int mouseY =
                    (int) (client.mouse.getY() * windowHeight / client.getWindow().getHeight());

            // Calculate the size and position of the image preview
            int originalImageWidth = dynamicImage.getWidth();
            int originalImageHeight = dynamicImage.getHeight();

            int maxWidth = (int) (windowWidth * 0.5);
            int maxHeight = (int) (windowHeight * 0.5);

            double scale = 1.0;
            if (originalImageWidth > maxWidth) {
                scale = (double) maxWidth / originalImageWidth;
            }
            if (originalImageHeight * scale > maxHeight) {
                scale = (double) maxHeight / originalImageHeight;
            }

            int renderedWidth = (int) (originalImageWidth * scale);
            int renderedHeight = (int) (originalImageHeight * scale);

            int x = mouseX;
            int y = mouseY - renderedHeight;

            if (x < 0) {
                x = 0;
            } else if (x + renderedWidth > windowWidth) {
                x = windowWidth - renderedWidth;
            }

            if (y < 0) {
                y = 0;
            } else if (y + renderedHeight > windowHeight) {
                y = windowHeight - renderedHeight;
            }

            // Draw the image preview with a border and shadow
            context.fill(x - 2, y - 2, x + renderedWidth + 2, y + renderedHeight + 2, 0x80000000);
            context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_ID, x, y, 0.0F, 0.0F,
                    renderedWidth, renderedHeight, originalImageWidth, originalImageHeight);
        } else if (isLoading) {
            // Display loading text if the image is still loading
            context.drawText(client.textRenderer, Text.literal("Loading image..."),
                    (int) client.mouse.getX(), (int) client.mouse.getY(),
                    Formatting.YELLOW.getColorValue(), true);
        }
    }

    /**
     * Handles image URL bypassing in chat messages by encrypting image URLs.
     * 
     * @param message The chat message to process.
     * @return The modified chat message with image URLs encrypted.
     */
    private String handleImageBypass(String message) {
        if (!NarConfig.getHandler().general.imagePreview || message == null || message.isEmpty()) {
            return message;
        }

        Matcher matcher = ParseUtil.IMAGE_URL_PATTERN.matcher(message);
        StringBuilder resultBuilder = new StringBuilder();

        while (matcher.find()) {
            String originalUrl = matcher.group();
            if (!ParseUtil.isImage(originalUrl)) {
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
     * @param overlay Whether the message is an overlay (e.g., from a mod or external source).
     * @return The modified chat message with decrypted image URLs.
     */
    private Text handleImagePreview(Text message, boolean overlay) {
        if (!NarConfig.getHandler().general.imagePreview || overlay || message == null) {
            return message;
        }

        Text modifiedText = ParseUtil.modifyText(message.asOrderedText(), segment -> {
            String text = segment.getString();
            Matcher matcher = BYPASS_PATTERN.matcher(text);
            MutableText modifiedSegment = Text.empty();
            int lastIndex = 0;

            // Iterate through all matches and replace them with decrypted URLs
            while (matcher.find()) {
                modifiedSegment.append(Text.literal(text.substring(lastIndex, matcher.start()))
                        .setStyle(segment.getStyle()));
                String originalUrl = matcher.group();
                String decryptedUrl = decryptImageUrl(originalUrl.substring(IMAGE_PREFIX.length()));

                // Add the decrypted URL with a clickable style
                try {
                    ClickEvent clickEvent = NarConfig.getHandler().general.imagePreview
                            ? new ClickEvent.RunCommand("nar showImage " + decryptedUrl)
                            : new ClickEvent.OpenUrl(new URI(decryptedUrl));
                    HoverEvent hoverEvent = NarConfig.getHandler().general.imagePreview
                            ? new HoverEvent.ShowText(Text.literal("Click to show image preview!")
                                    .formatted(Formatting.YELLOW))
                            : new HoverEvent.ShowText(Text.literal("Open image in browser.\n")
                                    .formatted(Formatting.YELLOW)
                                    .append(Text.literal(
                                            " Turn on image preview in config to view image in-game! ")
                                            .formatted(Formatting.DARK_GRAY)));
                    Style imageStyle = Style.EMPTY.withColor(Formatting.BLUE).withUnderline(true)
                            .withClickEvent(clickEvent).withHoverEvent(hoverEvent);

                    Text decryptedText = Text.literal(decryptedUrl).setStyle(imageStyle);
                    modifiedSegment.append(decryptedText);
                } catch (Exception e) {
                    e.printStackTrace();
                    modifiedSegment.append(Text.literal(originalUrl).setStyle(segment.getStyle()));
                }
                lastIndex = matcher.end();
            }

            // Append any remaining text after the last match
            if (lastIndex < text.length()) {
                modifiedSegment.append(
                        Text.literal(text.substring(lastIndex)).setStyle(segment.getStyle()));
            }

            return modifiedSegment;
        });

        return modifiedText;
    }

    /**
     * Handles the "showImage <imageUrl>" command to display an image preview.
     * 
     * @param command The command string to handle, expected to be in the format "showImage
     *        <imageUrl>".
     * @return True if the command was handled successfully, false otherwise.
     */
    public boolean handleCommand(String command) {
        // Check if the command is "showImage <imageUrl>"
        String[] args = command.split(" ", 2);
        if (args.length != 2 || !args[0].equalsIgnoreCase("showImage")) {
            return false;
        }

        // Parse the image URL from the command arguments
        String imageUrl = args[1];
        if (!NarConfig.getHandler().general.imagePreview || imageUrl == null
                || imageUrl.isEmpty()) {
            return false;
        }

        // Request the image from the URL and create a dynamic texture
        CompletableFuture.supplyAsync(() -> {
            try (InputStream is = URI.create(imageUrl).toURL().openStream()) {
                NativeImage image = NativeImage.read(is);
                return image;
            } catch (IOException e) {
                return null;
            }
        }, RequestUtil.EXECUTOR).thenAcceptAsync(image -> {
            if (image != null) {
                MinecraftClient client = MinecraftClient.getInstance();
                client.execute(() -> {
                    if (dynamicTexture != null) {
                        dynamicTexture.close();
                    }
                    dynamicTexture = new NativeImageBackedTexture(
                            () -> NotARat.MOD_ID + ".image_preview", image);
                    client.getTextureManager().registerTexture(TEXTURE_ID, dynamicTexture);
                    dynamicImage = image;
                    hasLoaded = true;
                    isLoading = false;
                });
            } else {
                isLoading = false;
                hasLoaded = false;
            }
        }, MinecraftClient.getInstance());

        return true;
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
