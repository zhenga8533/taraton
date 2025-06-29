package net.volcaronitee.volcclient.feature.container;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.OverlayUtil;
import net.volcaronitee.volcclient.util.OverlayUtil.Overlay;

/**
 * A search bar feature for the VolcClient mod.
 */
public class Searchbar {
    private static final Overlay OVERLAY = OverlayUtil.createOverlay("searchbar",
            () -> ConfigUtil.getHandler().container.searchbar, List.of());

    private static TextFieldWidget searchbar;

    /**
     * Registers the search bar overlay to the VolcClient mod.
     */
    public static void register() {
        searchbar = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 100, 10,
                Text.literal("Search..."));

        OVERLAY.setSpecialRender(context -> {
            searchbar.renderWidget(context, OVERLAY.getX(), OVERLAY.getY(), 1);
        });
    }
}
