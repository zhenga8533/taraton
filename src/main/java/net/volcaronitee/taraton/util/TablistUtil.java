package net.volcaronitee.taraton.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.GameMode;

/**
 * Utility class for managing the tab list of players in Minecraft.
 */
public class TablistUtil {
    private static final Ordering<PlayerListEntry> PLAYER_COMPARATOR =
            Ordering.from(new PlayerComparator());

    private static List<PlayerListEntry> tablist = new ArrayList<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private TablistUtil() {}

    /**
     * Initializes the TablistUtil by registering a tick handler to update the tab list
     * periodically.
     */
    public static void init() {
        TickUtil.register(TablistUtil::updateTablist, 5);
    }

    /**
     * Gets the current tab list of players.
     *
     * @return A list of PlayerListEntry objects representing the players in the tab list.
     */
    public static List<PlayerListEntry> getTablist() {
        return tablist;
    }

    /**
     * Updates the tab list of players by fetching the current player list from the network handler.
     * 
     * @param client The Minecraft client instance.
     */
    private static void updateTablist(MinecraftClient client) {
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler == null || networkHandler.getPlayerList() == null) {
            return;
        }

        // Sort the player list entries based on the comparator
        tablist = new ArrayList<>(networkHandler.getPlayerList());
        tablist.sort(PLAYER_COMPARATOR);
    }

    /**
     * Comparator for PlayerListEntry objects to sort players in the tab list.
     */
    private static class PlayerComparator implements Comparator<PlayerListEntry> {
        @Override
        public int compare(PlayerListEntry playerOne, PlayerListEntry playerTwo) {
            // Get player teams
            Team teamOne = playerOne.getScoreboardTeam();
            Team teamTwo = playerTwo.getScoreboardTeam();

            // Compare game modes (spectators usually last)
            boolean playerOneNotSpectator = playerOne.getGameMode() != GameMode.SPECTATOR;
            boolean playerTwoNotSpectator = playerTwo.getGameMode() != GameMode.SPECTATOR;

            return ComparisonChain.start()
                    .compareTrueFirst(playerOneNotSpectator, playerTwoNotSpectator)
                    .compare(teamOne != null ? teamOne.getName() : "",
                            teamTwo != null ? teamTwo.getName() : "")
                    .compare(playerOne.getProfile().getName(), playerTwo.getProfile().getName())
                    .result();
        }
    }
}
