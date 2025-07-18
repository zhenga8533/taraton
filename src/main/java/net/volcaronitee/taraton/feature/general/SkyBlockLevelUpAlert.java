package net.volcaronitee.taraton.feature.general;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.ParseUtil;
import net.volcaronitee.taraton.util.ScheduleUtil;
import net.volcaronitee.taraton.util.TitleUtil;

/**
 * Feature that alerts the player when they level up in SkyBlock.
 */
public class SkyBlockLevelUpAlert {
    private static final SkyBlockLevelUpAlert INSTANCE = new SkyBlockLevelUpAlert();

    private static final Pattern LEVEL_UP_PATTERN = Pattern.compile("Level \\d+ ➡ \\[\\d+\\]");

    private String levelUpText = "";

    /**
     * Private constructor to prevent instantiation.
     */
    private SkyBlockLevelUpAlert() {}

    /**
     * Registers the SkyBlock level up alert feature to listen for incoming messages.
     */
    public static void register() {
        ClientReceiveMessageEvents.GAME.register(INSTANCE::parseMessage);
    }

    /**
     * Parses the incoming message to check for SkyBlock level up and displays it if applicable.
     * 
     * @param message The message received from the game.
     * @param overlay Whether the message is an overlay message.
     */
    private void parseMessage(Text message, boolean overlay) {
        if (!FeatureUtil.isEnabled(TaratonConfig.getInstance().general.skyblockLevelUpAlert)
                || overlay) {
            return;
        }

        // Check if the message contains SkyBlock level up
        String text = ParseUtil.removeFormatting(message.getString()).strip();
        Matcher matcher = LEVEL_UP_PATTERN.matcher(text);
        if (!matcher.find()) {
            return;
        }

        // Extract the level up text from the matched group
        text = text.replace("Level ", "§bLevel §8");
        text = text.replace("[", "[§b");
        INSTANCE.levelUpText = text.replace("]", "§8]");

        ScheduleUtil.schedule(() -> {
            String title = "§3§lSKYBLOCK LEVEL UP";
            TitleUtil.createTitle(title, INSTANCE.levelUpText, 2);
        }, 2);
    }
}
