package net.volcaronitee.taraton.feature.economy;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.FormatUtil;
import net.volcaronitee.taraton.util.OverlayUtil;
import net.volcaronitee.taraton.util.OverlayUtil.Overlay;
import net.volcaronitee.taraton.util.ParseUtil;
import net.volcaronitee.taraton.util.ScoreboardUtil;
import net.volcaronitee.taraton.util.TickUtil;
import net.volcaronitee.taraton.util.TrackerUtil;
import net.volcaronitee.taraton.util.TrackerUtil.RateTracker;
import net.volcaronitee.taraton.util.helper.LineContent;

/**
 * Feature to track coin gain and rate in the game.
 */
public class CoinTracker {
    private static final CoinTracker INSTANCE = new CoinTracker();

    private static final RateTracker COIN_TRACKER = TrackerUtil.createRateTracker();

    private static final List<LineContent> LINES =
            List.of(LineContent.ofColumns(List.of("§6Gain:", "§f0"), () -> true),
                    LineContent.ofColumns(List.of("§6Time:", "§cInactive"), () -> true),
                    LineContent.ofColumns(List.of("§6Rate:", "§f0 §e¢/hr"), () -> true));
    private static final Overlay OVERLAY = OverlayUtil.createOverlay("coin_tracker",
            () -> FeatureUtil.isEnabled(TaratonConfig.getInstance().economy.coinTracker != 0),
            LINES);

    /**
     * Registers the coin tracker feature to update periodically and listen for messages.
     */
    public static void register() {

        TickUtil.register(INSTANCE::updateOverlay, 20);
    }

    /**
     * Updates the overlay with the current coin tracker values.
     * 
     * @param client The Minecraft client instance.
     */
    private void updateOverlay(MinecraftClient client) {
        updateTracker();
        LINES.get(0).setColumn("§f" + FormatUtil.commafy(COIN_TRACKER.getTotalGained()), 1);
        String timeText = COIN_TRACKER.getElapsedTime() == 0 ? "§cInactive"
                : "§f" + FormatUtil.timeToString(COIN_TRACKER.getElapsedTime());
        LINES.get(1).setColumn(timeText, 1);
        LINES.get(2).setColumn("§f" + FormatUtil.commafy(COIN_TRACKER.getRatePerHour()) + " §e¢/hr",
                1);
        OVERLAY.setChanged();
    }

    /**
     * Updates the coin tracker by extracting the purse value from the scoreboard.
     */
    private void updateTracker() {
        if (!FeatureUtil.isEnabled(TaratonConfig.getInstance().economy.coinTracker != 0)) {
            return;
        }

        // Extract the purse value from the scoreboard
        List<Text> lines = ScoreboardUtil.getScoreboard();
        String purse = lines.stream().filter(line -> line.getString().startsWith("Purse"))
                .findFirst().map(Text::getString).orElse(null);
        if (purse == null) {
            return;
        }
        int coins = ParseUtil.parseInt(purse.split(" ")[1]);

        // Update the coin tracker with the new value
        COIN_TRACKER.update(coins);
    }
}
