package net.volcaronitee.taraton.feature.garden;

import java.util.List;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.LocationUtil;
import net.volcaronitee.taraton.util.LocationUtil.World;
import net.volcaronitee.taraton.util.ScheduleUtil;
import net.volcaronitee.taraton.util.TablistUtil;

public class PestControl {
    private static final PestControl INSTANCE = new PestControl();

    private String lastTpPlot = "";

    /**
     * Private constructor to prevent instantiation.
     */
    private PestControl() {}

    public static PestControl getInstance() {
        return INSTANCE;
    }

    public static void register() {

    }

    public int pestTpCommand(CommandContext<FabricClientCommandSource> context) {
        if (!FeatureUtil.isEnabled(TaratonConfig.getInstance().farming.pestTeleport)
                || LocationUtil.getWorld() != World.GARDEN) {
            return 0;
        }

        // Fetch the tab list and find the index of the pests
        List<PlayerListEntry> tablist = TablistUtil.getTablist();
        int pestIndex = TablistUtil.findIndex("Pests:");
        if (pestIndex == -1 || pestIndex + 2 >= tablist.size()) {
            Taraton.sendMessage(Text.literal("Could not find pests in the tab list.")
                    .formatted(Formatting.RED));
            return 1;
        }

        try {
            String plotsStr = tablist.get(pestIndex + 2).getDisplayName().getString();
            String[] plots = plotsStr.split(": ")[1].split(", ");

            // Check if there are no pests left
            if (!plotsStr.startsWith("Plots: ") || plots.length == 0) {
                Taraton.sendMessage(
                        Text.literal("All pests exterminated!").formatted(Formatting.GREEN));
                return 1;
            }

            // Iterate through the plots and teleport to the first new plot
            for (String plot : plots) {
                if (!plot.equals(lastTpPlot)) {
                    Taraton.sendMessage(Text.literal("Teleporting to plot: " + plot)
                            .formatted(Formatting.GREEN));
                    ScheduleUtil.scheduleCommand("plottp " + plot, 1);
                    lastTpPlot = plot;
                    return 1;
                }
            }

            // If all plots are already visited, the pest is in current plot
            Taraton.sendMessage(
                    Text.literal("Pest is in your current plot!").formatted(Formatting.YELLOW));

            return 1;
        } catch (Exception e) {
            Taraton.sendMessage(Text.literal("Error parsing pest locations from tab list.")
                    .formatted(Formatting.RED));
            return 0;
        }
    }
}
