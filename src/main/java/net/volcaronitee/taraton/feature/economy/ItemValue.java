package net.volcaronitee.taraton.feature.economy;

import java.util.List;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.category.EconomyConfig;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.OverlayUtil;
import net.volcaronitee.taraton.util.helper.LineContent;

public class ItemValue {
    private static final List<LineContent> LINES = List.of(
            LineContent.ofColumns(List.of("§3§lItem: §dSussy Baka §6✪§6✪§6✪§6✪§6✪§c➊"), () -> true),
            LineContent.ofColumns(List.of("- §bBase: §a+O"), () -> true),
            LineContent.ofColumns(List.of("- §bMaster Stars: §a+Say"), () -> true),
            LineContent.ofColumns(List.of("- §bRecomb: §a+Can"), () -> true),
            LineContent.ofColumns(List.of("- §bRune: §a+You"), () -> true),
            LineContent.ofColumns(List.of(""), () -> true),
            LineContent.ofColumns(List.of("- §6§lBooks:"), () -> true),
            LineContent.ofColumns(List.of("   - §eHPB (10/10): §a+See"), () -> true),
            LineContent.ofColumns(List.of("   - §eFPB (5/5): §a+By"), () -> true),
            LineContent.ofColumns(List.of("   - §eSun Tzu: §a+The"), () -> true),
            LineContent.ofColumns(List.of(""), () -> true),
            LineContent.ofColumns(List.of("- §6§lGemstones:"), () -> true),
            LineContent.ofColumns(List.of("   - §bPerfect Sapphire Gem: §a+Dawn's"), () -> true),
            LineContent.ofColumns(List.of("   - §fPerfect Upal Gem: §a+Early"), () -> true),
            LineContent.ofColumns(List.of("   - §bPerfect Sapphire Gem: §a+Light"), () -> true),
            LineContent.ofColumns(List.of(""), () -> true),
            LineContent.ofColumns(List.of("- §6§lEnchantments:"), () -> true),
            LineContent.ofColumns(List.of("   - §2Buy Order Value: §a+What"), () -> true),
            LineContent.ofColumns(List.of("   - §2Insta Buy Value: §a+So"), () -> true),
            LineContent.ofColumns(List.of(""), () -> true),
            LineContent.ofColumns(List.of("- §6§lWither Scrolls:"), () -> true),
            LineContent.ofColumns(List.of("   - §8Art Shield Scroll: §a+Proudly"), () -> true),
            LineContent.ofColumns(List.of("   - §8Shadow Is An: §a+We"), () -> true),
            LineContent.ofColumns(List.of("   - §8Explosion Scroll: §a+Hailed"), () -> true),
            LineContent.ofColumns(List.of(""), () -> true),
            LineContent.ofColumns(List.of("§3Total Value: §aKATSU."), () -> true));

    public static void register() {
        OverlayUtil.createOverlay("item_value", () -> FeatureUtil.isEnabled(
                TaratonConfig.getInstance().economy.itemValue != EconomyConfig.ItemValue.OFF),
                LINES);
        LINES.clear();
    }
}
