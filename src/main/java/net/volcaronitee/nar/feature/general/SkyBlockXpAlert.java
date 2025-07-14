package net.volcaronitee.nar.feature.general;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.util.ScheduleUtil;
import net.volcaronitee.nar.util.TitleUtil;

/**
 * Feature that alerts the player when they gain SkyBlock XP.
 */
public class SkyBlockXpAlert {
    private static final SkyBlockXpAlert INSTANCE = new SkyBlockXpAlert();

    private static final Pattern SKYBLOCK_XP_PATTERN =
            Pattern.compile("(§b\\+\\d+ SkyBlock XP)( §7\\([^§]*§7\\)§b \\(\\d+\\/100\\))");

    private static final Set<String> XP_TEXTS = new HashSet<>();

    /**
     * Private constructor to prevent instantiation.
     */
    public static void register() {
        ClientReceiveMessageEvents.GAME.register(INSTANCE::parseMessage);
    }

    /**
     * Parses the incoming message to check for SkyBlock XP gain and displays it if applicable.
     * 
     * @param message The message received from the game.
     * @param overlay Whether the message is an overlay message.
     */
    private void parseMessage(Text message, boolean overlay) {
        if (!NarConfig.getHandler().general.skyblockXpAlert || !overlay) {
            return;
        }

        // Check if the message contains SkyBlock XP gain
        String text = message.getString();
        Matcher matcher = SKYBLOCK_XP_PATTERN.matcher(text);
        if (!matcher.find()) {
            return;
        }

        // Use the full matched string for the cooldown to prevent duplicates
        String fullMatch = matcher.group(0);
        if (XP_TEXTS.contains(fullMatch)) {
            return;
        }
        XP_TEXTS.add(fullMatch);

        // Group 1 contains the first part (e.g., "§b+1 SkyBlock XP")
        String title = matcher.group(1);

        // Group 2 contains the second part (e.g., " §7(Attribute Levels)§b (91/100)")
        String subtitle = matcher.group(2);

        // Use the captured groups to send the title and subtitle
        NotARat.sendMessage(Text.of(title + subtitle));
        TitleUtil.createTitle(title, subtitle, 1);

        // Set a cooldown to prevent spamming the alert
        ScheduleUtil.schedule(() -> {
            XP_TEXTS.remove(fullMatch);
        }, 200);
    }
}
