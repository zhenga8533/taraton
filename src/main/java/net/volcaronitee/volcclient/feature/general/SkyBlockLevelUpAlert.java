package net.volcaronitee.volcclient.feature.general;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.ParseUtil;
import net.volcaronitee.volcclient.util.ScheduleUtil;
import net.volcaronitee.volcclient.util.TextUtil;

/**
 * Feature that alerts the player when they level up in SkyBlock.
 */
public class SkyBlockLevelUpAlert {
    private static final SkyBlockLevelUpAlert INSTANCE = new SkyBlockLevelUpAlert();

    private static final Pattern LEVEL_UP_PATTERN = Pattern.compile("Level \\d+ ➡ \\[\\d+\\]");

    private String levelUpText = "";
    private boolean cooldown = false;

    /**
     * Private constructor to prevent instantiation.
     */
    public static void register() {
        ClientReceiveMessageEvents.GAME.register(SkyBlockLevelUpAlert::parseMessage);
    }

    /**
     * Parses the incoming message to check for SkyBlock level up and displays it if applicable.
     * 
     * @param message The message received from the game.
     * @param overlay Whether the message is an overlay message.
     */
    private static void parseMessage(Text message, boolean overlay) {
        if (!ConfigUtil.getHandler().general.skyblockLevelUpAlert || overlay || INSTANCE.cooldown) {
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
            MinecraftClient client = MinecraftClient.getInstance();
            client.inGameHud.getChatHud().addMessage(TextUtil.MOD_TITLE.copy()
                    .append(Text.literal(" §3§lSKYBLOCK LEVEL UP§r " + INSTANCE.levelUpText)));
            client.inGameHud.setTitle(Text.literal(INSTANCE.levelUpText));
        }, 2);

        // Set a cooldown to prevent spamming the alert
        INSTANCE.cooldown = true;
        ScheduleUtil.schedule(() -> {
            INSTANCE.cooldown = false;
        }, 100);
    }
}
