package net.volcaronitee.taraton.feature.container;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.util.OverlayUtil;
import net.volcaronitee.taraton.util.OverlayUtil.Overlay;

/**
 * A search bar feature for the Taraton mod.
 */
public class Searchbar {
    private static final Overlay OVERLAY = OverlayUtil.createOverlay("searchbar",
            () -> TaratonConfig.getHandler().container.searchbar, List.of());

    private static TextFieldWidget searchbar;

    /**
     * Registers the search bar overlay to the Taraton mod.
     */
    public static void register() {
        searchbar = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 100, 10,
                Text.literal("Search..."));

        OVERLAY.setSpecialRender((context, delta) -> {
            searchbar.renderWidget(context, OVERLAY.getX(), OVERLAY.getY(), delta);
        });
    }
}
