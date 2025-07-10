package net.volcaronitee.nar.feature.crimson_isle;

import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.config.NarList;
import net.volcaronitee.nar.util.ScheduleUtil;

/**
 * Feature that handles vanquisher warping in the Crimson Isle.
 */
public class VanquisherWarp {
    private static final VanquisherWarp INSTANCE = new VanquisherWarp();

    public static final NarList VANQ_LIST = new NarList("Vanq Warp List",
            Text.literal("A list of players to warp into lobby when a vanquisher spawns."),
            "vanq_list.json");

    private static final Pattern SPAWN_PATTERN =
            Pattern.compile("A Vanquisher is spawning nearby!");
    private static final Pattern[] JOIN_PATTERNS =
            {Pattern.compile("Couldn't find a player with that name!"),
                    Pattern.compile("You cannot invite that player since they're not online."),
                    Pattern.compile(".* has joined the party!"),
                    Pattern.compile("The party invite to .* has expired")};
    private static final Pattern FAIL_PATTERN = Pattern.compile("You have joined .* party!");

    private int partyCount = 0;

    /**
     * Private constructor to prevent instantiation.
     */
    private VanquisherWarp() {}

    /**
     * Registers the vanquisher warp handler to listen for incoming messages.
     */
    public static void register() {
        ClientReceiveMessageEvents.GAME.register(INSTANCE::handleVanqWarp);
    }

    /**
     * Handles incoming chat messages related to vanquisher warping.
     * 
     * @param message The chat message to check for vanquisher warp commands.
     * @param overlay Whether the message is an overlay message.
     */
    private void handleVanqWarp(Text message, boolean overlay) {
        if (overlay || !NarConfig.getHandler().crimsonIsle.vanquisherWarp
                || VANQ_LIST.list.isEmpty()) {
            return;
        }

        String text = message.getString();
        handleVanqSpawn(text);
        handlePlayerJoin(text);
    }

    /**
     * Handles vanquisher spawn messages to initiate the warp command.
     * 
     * @param text The chat message text to check for vanquisher spawn.
     */
    private void handleVanqSpawn(String text) {
        if (!SPAWN_PATTERN.matcher(text).find()) {
            return;
        }

        String players = String.join(" ", VANQ_LIST.list);
        ScheduleUtil.scheduleCommand("p " + players);
        partyCount = VANQ_LIST.list.size();
    }

    /**
     * Handles player join messages to update the party count and warp if necessary.
     * 
     * @param text The chat message text to check for player joins.
     */
    private void handlePlayerJoin(String text) {
        if (partyCount <= 0) {
            return;
        }

        // Check if any of the join patterns match the text
        for (Pattern pattern : JOIN_PATTERNS) {
            if (pattern.matcher(text).find()) {
                partyCount--;
                if (partyCount == 0) {
                    ScheduleUtil.scheduleCommand("p warp");
                }
                return;
            }
        }

        // If the fail pattern is found, reset the party count
        if (FAIL_PATTERN.matcher(text).find()) {
            partyCount = 0;
        }
    }
}
