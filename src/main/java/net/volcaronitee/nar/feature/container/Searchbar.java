package net.volcaronitee.nar.feature.container;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.util.OverlayUtil;
import net.volcaronitee.nar.util.OverlayUtil.Overlay;

/**
 * A search bar feature for the NotARat mod.
 */
public class Searchbar {
    private static final Overlay OVERLAY = OverlayUtil.createOverlay("searchbar",
            () -> NarConfig.getHandler().container.searchbar, List.of());

    private static TextFieldWidget searchbar;

    /**
     * Registers the search bar overlay to the NotARat mod.
     */
    public static void register() {
        searchbar = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 100, 10,
                Text.literal("Search..."));

        OVERLAY.setSpecialRender((context, delta) -> {
            searchbar.renderWidget(context, OVERLAY.getX(), OVERLAY.getY(), delta);
        });
    }
}
