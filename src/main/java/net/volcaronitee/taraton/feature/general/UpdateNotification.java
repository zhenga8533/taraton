package net.volcaronitee.taraton.feature.general;

import java.net.URI;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.util.RequestUtil;
import net.volcaronitee.taraton.util.ScheduleUtil;

/**
 * Handles update notifications for the Taraton mod.
 */
public class UpdateNotification {
    private static final String GITHUB_URL = "zhenga8533/taraton/releases/latest";

    private static String latestVersion = null;
    private static boolean updateAvailable = false;

    /**
     * Registers the update notification feature. This method should be called during mod
     * initialization.
     */
    public static void register() {
        checkForUpdates();
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> notifyPlayer());
    }

    /**
     * Checks for updates by querying the GitHub API. This method is asynchronous and will not block
     * the game thread.
     */
    private static void checkForUpdates() {
        String url = "https://api.github.com/repos/" + GITHUB_URL;
        RequestUtil.get(url).thenAcceptAsync(responseBody -> {
            if (responseBody == null || responseBody.isEmpty()) {
                return;
            }

            // Parse the response to extract the latest version
            String[] parts = responseBody.split("\"tag_name\":\"");
            if (parts.length > 1) {
                String version = parts[1].split("\"")[0];
                if (!version.equals(Taraton.MOD_VERSION)) {
                    latestVersion = version;
                    updateAvailable = true;
                }
            }
        }, RequestUtil.EXECUTOR);
    }

    /**
     * If an update is available, sends a clickable chat message to the player.
     */
    private static void notifyPlayer() {
        if (!TaratonConfig.getHandler().general.updateNotification || !updateAvailable) {
            return;
        }

        // Notify after 5 seconds
        ScheduleUtil.schedule(() -> {
            if (!updateAvailable) {
                return;
            }

            try {
                // Construct the clickable message to notify the player about the update
                Style style = Style.EMPTY.withColor(Formatting.BLUE).withUnderline(true)
                        .withClickEvent(new ClickEvent.OpenUrl(
                                new URI("https://github.com/" + GITHUB_URL)));
                MutableText message = Text.literal("A new version of Taraton is available: ")
                        .formatted(Formatting.YELLOW)
                        .append(Text.literal(latestVersion).setStyle(style));

                if (Taraton.sendMessage(message)) {
                    updateAvailable = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 100);
    }
}
