package net.volcaronitee.nar.feature.chat;

import java.util.regex.Matcher;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.config.NarList;
import net.volcaronitee.nar.util.ParseUtil;
import net.volcaronitee.nar.util.ScheduleUtil;

/**
 * Feature to automatically kick players from the party if they are in the black list.
 */
public class AutoKick {
    private static final AutoKick INSTANCE = new AutoKick();

    public static final NarList BLACK_LIST = new NarList("Black List",
            Text.literal("A list of players to block from using various features."),
            "black_list.json");

    /**
     * Private constructor to prevent instantiation.
     */
    private AutoKick() {}

    /**
     * Registers the AutoKick feature to listen for incoming messages.
     */
    public static void register() {
        ClientReceiveMessageEvents.GAME.register(INSTANCE::handleAutoKick);
    }

    /**
     * Handles the auto kick feature by checking if the message matches the party join pattern.
     * 
     * @param message The incoming message to check.
     * @param overlay Whether the message is an overlay message.
     */
    private void handleAutoKick(Text message, boolean overlay) {
        if (overlay || !NarConfig.getHandler().chat.autoKick) {
            return;
        }

        String text = message.getString();
        Matcher matcher = ParseUtil.PARTY_JOIN_PATTERN.matcher(text);
        if (!matcher.matches()) {
            return;
        }

        String username = matcher.group(1);
        if (BLACK_LIST.list.contains(username)) {
            ScheduleUtil.scheduleCommand("p kick " + username);
        }
    }
}
