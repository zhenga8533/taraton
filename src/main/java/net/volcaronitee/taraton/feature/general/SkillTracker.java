package net.volcaronitee.taraton.feature.general;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.OverlayUtil;
import net.volcaronitee.taraton.util.OverlayUtil.LineContent;
import net.volcaronitee.taraton.util.OverlayUtil.Overlay;
import net.volcaronitee.taraton.util.TickUtil;
import net.volcaronitee.taraton.util.TrackerUtil.RateTracker;

/**
 * Feature to track skill progress in the game.
 */
public class SkillTracker {
    private static final SkillTracker INSTANCE = new SkillTracker();

    private static final Map<String, RateTracker> SKILL_TRACKER = new HashMap<>();
    static {
        SKILL_TRACKER.put("None", new RateTracker());
        SKILL_TRACKER.put("Combat", new RateTracker());
        SKILL_TRACKER.put("Mining", new RateTracker());
        SKILL_TRACKER.put("Foraging", new RateTracker());
        SKILL_TRACKER.put("Fishing", new RateTracker());
        SKILL_TRACKER.put("Enchanting", new RateTracker());
        SKILL_TRACKER.put("Alchemy", new RateTracker());
        SKILL_TRACKER.put("Taming", new RateTracker());
        SKILL_TRACKER.put("Carpentry", new RateTracker());
        SKILL_TRACKER.put("Runecrafting", new RateTracker());
        SKILL_TRACKER.put("Social", new RateTracker());
        SKILL_TRACKER.put("Hunting", new RateTracker());
    }

    private String currentSkill = "None";

    private static final List<LineContent> LINES =
            List.of(new LineContent("§3Skill: ", "§fAura", () -> true),
                    new LineContent("§3Gain: ", "§f0", () -> true),
                    new LineContent("§3Time: ", "§cInactive", () -> true),
                    new LineContent("§3Rate: ", "§f0 xp/hr", () -> true),
                    new LineContent("§3Level Up: ", "§aMaxed", () -> true));
    private static final Overlay OVERLAY = OverlayUtil.createOverlay("skill_tracker",
            () -> FeatureUtil.isEnabled(TaratonConfig.getInstance().general.skillTracker != 0),
            LINES);

    /**
     * Registers the skill tracker feature to update periodically and listen for messages.
     */
    public static void register() {
        TickUtil.register(INSTANCE::updateOverlay, 20);
        ClientReceiveMessageEvents.GAME.register(INSTANCE::updateTracker);
    }

    private void updateOverlay(MinecraftClient client) {
        // TODO: Implement logic to update the current skill and its progress.
    }

    private void updateTracker(Text message, boolean overlay) {
        if (!overlay
                || !FeatureUtil.isEnabled(TaratonConfig.getInstance().general.skillTracker != 0)) {
            return;
        }

        // TODO: Implement logic to parse the message and update the skill tracker.
    }
}
