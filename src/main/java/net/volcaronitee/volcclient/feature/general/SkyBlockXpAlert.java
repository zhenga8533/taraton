package net.volcaronitee.volcclient.feature.general;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.ScheduleUtil;
import net.volcaronitee.volcclient.util.TextUtil;

/**
 * Feature that alerts the player when they gain SkyBlock XP.
 */
public class SkyBlockXpAlert {
    private static final Pattern SKYBLOCK_XP_PATTERN =
            Pattern.compile("(§b\\+\\d+ SkyBlock XP §7\\([^§]*§7\\)§b \\(\\d+\\/100\\))");

    private static final Set<String> XP_TEXTS = new HashSet<>();

    /**
     * Private constructor to prevent instantiation.
     */
    public static void register() {
        ClientReceiveMessageEvents.GAME.register(SkyBlockXpAlert::parseMessage);
    }

    /**
     * Parses the incoming message to check for SkyBlock XP gain and displays it if applicable.
     * 
     * @param message The message received from the game.
     * @param overlay Whether the message is an overlay message.
     */
    private static void parseMessage(Text message, boolean overlay) {
        if (!ConfigUtil.getHandler().general.skyblockXpAlert || !overlay) {
            return;
        }

        // Check if the message contains SkyBlock XP gain
        String text = message.getString();
        Matcher matcher = SKYBLOCK_XP_PATTERN.matcher(text);
        if (!matcher.find()) {
            return;
        }

        // Extract the XP text from the matched group
        String xpText = matcher.group(1);
        if (XP_TEXTS.contains(xpText)) {
            return;
        }
        XP_TEXTS.add(xpText);

        MinecraftClient client = MinecraftClient.getInstance();
        client.inGameHud.getChatHud()
                .addMessage(TextUtil.MOD_TITLE.copy().append(Text.literal(" " + xpText)));
        client.inGameHud.setTitle(Text.literal(xpText));

        // Set a cooldown to prevent spamming the alert
        ScheduleUtil.schedule(() -> {
            XP_TEXTS.remove(xpText);
        }, 100);
    }
}
