package net.volcaronitee.taraton.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.Text;

/**
 * Utility class for managing and updating the in-game scoreboard.
 */
public class ScoreboardUtil {
    private static List<Text> scoreboard = new ArrayList<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private ScoreboardUtil() {}

    /**
     * Initializes the scoreboard utility by registering the update method to be called every 5
     * ticks.
     */
    public static void init() {
        TickUtil.register(ScoreboardUtil::updateScoreboard, 1);
    }

    /**
     * Gets the current scoreboard lines.
     * 
     * @return The list of scoreboard lines.
     */
    public static List<Text> getScoreboard() {
        return scoreboard;
    }

    /**
     * Updates the scoreboard by fetching the latest data from the Minecraft client.
     * 
     * @param client The Minecraft client instance.
     */
    private static void updateScoreboard(MinecraftClient client) {
        if (client == null || client.world == null) {
            scoreboard = Collections.emptyList();
            return;
        }

        // Fetch the current scoreboard and sidebar objective
        Scoreboard clientScoreboard = client.world.getScoreboard();
        ScoreboardObjective sidebarObjective =
                clientScoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);

        if (sidebarObjective == null) {
            scoreboard = Collections.emptyList();
            return;
        }

        // Retrieve and order the scoreboard entries
        Collection<ScoreboardEntry> scores =
                clientScoreboard.getScoreboardEntries(sidebarObjective);
        List<ScoreboardEntry> orderedScores = new ArrayList<>(scores);
        Collections.reverse(orderedScores);

        List<Text> newLines = new ArrayList<>();
        newLines.add(sidebarObjective.getDisplayName());

        // Process each scoreboard entry
        for (ScoreboardEntry entry : orderedScores) {
            if (entry.owner() == null || entry.owner().startsWith("#")) {
                continue;
            }

            // Decorate the owner's name with team formatting
            Team team = clientScoreboard.getScoreHolderTeam(entry.owner());
            Text ownerText = Team.decorateName(team, Text.literal(entry.owner()));

            if (entry.owner().contains("§")) {
                // If the owner's name contains formatting codes, display it as is
                newLines.add(ownerText);
            } else {
                // Format the score value
                Text scoreText;
                NumberFormat format =
                        entry.numberFormatOverride() != null ? entry.numberFormatOverride()
                                : sidebarObjective.getNumberFormat();

                if (entry.display() != null) {
                    // Use custom display text if available
                    scoreText = entry.display();
                } else if (format != null) {
                    // Format the score using the specified number format
                    scoreText = format.format(entry.value());
                } else {
                    // Default to plain text representation of the score value
                    scoreText = Text.literal(String.valueOf(entry.value()));
                }

                newLines.add(Text.empty().append(ownerText).append(" ").append(scoreText));
            }
        }

        scoreboard = newLines;
    }
}
