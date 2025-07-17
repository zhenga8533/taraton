package net.volcaronitee.taraton.feature.general;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.Window;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.util.ParseUtil;
import net.volcaronitee.taraton.util.RequestUtil;
import net.volcaronitee.taraton.util.helper.Contract;

/**
 * Handles image URL bypassing in chat messages by encrypting image URLs.
 */
public class ImagePreview {
    private static final ImagePreview INSTANCE = new ImagePreview();

    private static final Identifier LAYER = Identifier.of(Taraton.MOD_ID, "image_preview");
    private static final Identifier TEXTURE_ID =
            Identifier.of(Taraton.MOD_ID, "image_preview_texture");
    private static final int SHIFT_AMOUNT = 3;
    private static final String IMAGE_PREFIX = "tar@";
    private static final String NSFW_PREFIX = "tarx@";
    private static final Pattern BYPASS_PATTERN =
            Pattern.compile("(" + IMAGE_PREFIX + "|" + NSFW_PREFIX + ")([^ ]+)");

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
        ClientSendMessageEvents.MODIFY_COMMAND.register(INSTANCE::handleImageBypass);

        ClientReceiveMessageEvents.MODIFY_GAME.register(INSTANCE::handleImagePreview);
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.remove(screen).register(closedScreen -> INSTANCE.resetImagePreview());
        });
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
        MinecraftClient client = MinecraftClient.getInstance();
        Window window = client.getWindow();
        if (!TaratonConfig.getInstance().general.imagePreview || window.isMinimized()) {
            return;
        }

        // Get the current window dimensions and mouse position
        int windowWidth = context.getScaledWindowWidth();
        int windowHeight = context.getScaledWindowHeight();
        int mouseX = (int) (client.mouse.getX() * windowWidth / window.getWidth());
        int mouseY = (int) (client.mouse.getY() * windowHeight / window.getHeight());

        if (hasLoaded && dynamicTexture != null && dynamicImage != null) {
            // Adjust the image preview size to fit within the window dimensions
            int imageWidth = dynamicImage.getWidth() * windowWidth / window.getWidth();
            int imageHeight = dynamicImage.getHeight() * windowHeight / window.getHeight();

            int renderedWidth = imageWidth;
            int renderedHeight = imageHeight;

            if (imageWidth > windowWidth || imageHeight > windowHeight) {
                double scaleX = (double) windowWidth / imageWidth;
                double scaleY = (double) windowHeight / imageHeight;
                double scale = Math.min(scaleX, scaleY);

                renderedWidth = (int) (imageWidth * scale);
                renderedHeight = (int) (imageHeight * scale);
            }

            // Adjust the position of the image preview to be centered around the mouse cursor
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

            // Draw the image preview
            context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_ID, x, y, 0.0F, 0.0F,
                    renderedWidth, renderedHeight, renderedWidth, renderedHeight);
        } else if (isLoading) {
            // Display loading text if the image is still loading
            context.drawText(client.textRenderer, Text.literal("Loading image..."), mouseX + 5,
                    mouseY - 5, Formatting.YELLOW.getColorValue(), true);
        }
    }

    /**
     * Handles image URL bypassing in chat messages by encrypting image URLs.
     * 
     * @param message The chat message to process.
     * @return The modified chat message with image URLs encrypted.
     */
    private String handleImageBypass(String message) {
        if (!TaratonConfig.getInstance().general.imagePreview || message == null
                || message.isEmpty()) {
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

            String editedUrl = "";
            if (originalUrl.startsWith("_")) {
                originalUrl = originalUrl.substring(1);
                editedUrl = NSFW_PREFIX;
            } else {
                editedUrl = IMAGE_PREFIX;
            }
            editedUrl += encryptImageUrl(originalUrl);
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
        if (!TaratonConfig.getInstance().general.imagePreview || overlay || message == null) {
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

                // Extract the original URL, prefix, and URL part
                String originalUrl = matcher.group(0);
                String prefix = matcher.group(1);
                String urlPart = matcher.group(2);

                // Decrypt the URL part
                boolean blockNsfw = prefix.equals(NSFW_PREFIX) && !Contract.isSigned();
                String decryptedUrl = blockNsfw ? "§4[BLOCKED]" : decryptImageUrl(urlPart);

                // Add the decrypted URL with a clickable style
                boolean showPreview =
                        TaratonConfig.getInstance().general.imagePreview && !blockNsfw;
                try {
                    ClickEvent clickEvent = showPreview
                            ? new ClickEvent.RunCommand("taraton showImage " + decryptedUrl)
                            : new ClickEvent.OpenUrl(
                                    new URI(blockNsfw ? "https://roblox.com" : decryptedUrl));

                    HoverEvent hoverEvent = showPreview
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
        String[] args = command.split(" ", 2);
        if (isLoading || !TaratonConfig.getInstance().general.imagePreview || args.length != 2
                || !args[0].equalsIgnoreCase("showImage")) {
            return false;
        }

        // Parse the image URL from the command
        String imageUrl = args[1];
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }

        // Reset the current image preview if it exists
        resetImagePreview();
        hasLoaded = false;
        isLoading = true;

        // Fetch the image from the URL
        RequestUtil.getImage(imageUrl).thenAcceptAsync(image -> {
            MinecraftClient client = MinecraftClient.getInstance();
            client.execute(() -> {
                resetImagePreview();

                // Check if the client is still in a valid state
                if (client.currentScreen == null) {
                    isLoading = false;
                    hasLoaded = false;
                    return;
                }

                // Check if the image is null or invalid
                if (image != null) {
                    dynamicTexture = new NativeImageBackedTexture(
                            () -> Taraton.MOD_ID + ".image_preview", image);
                    client.getTextureManager().registerTexture(TEXTURE_ID, dynamicTexture);
                    dynamicImage = image;
                    hasLoaded = true;
                } else {
                    hasLoaded = false;
                    Taraton.sendMessage(
                            Text.literal("Failed to load image! Image data was null or invalid.")
                                    .formatted(Formatting.RED));
                }
                isLoading = false;
            });
        }, MinecraftClient.getInstance()::execute).exceptionally(throwable -> {
            throwable.printStackTrace();
            isLoading = false;
            hasLoaded = false;
            Taraton.sendMessage(
                    Text.literal("Failed to load image from URL: " + throwable.getMessage())
                            .formatted(Formatting.RED));
            return null;
        });

        return true;
    }

    /**
     * Resets the image preview by clearing the current texture and image.
     */
    private void resetImagePreview() {
        if (dynamicTexture != null) {
            MinecraftClient.getInstance().getTextureManager().destroyTexture(TEXTURE_ID);
            dynamicTexture.close();
            dynamicTexture = null;
        }
        if (dynamicImage != null) {
            dynamicImage.close();
            dynamicImage = null;
        }
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
