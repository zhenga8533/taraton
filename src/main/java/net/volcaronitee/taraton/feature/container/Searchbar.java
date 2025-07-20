package net.volcaronitee.taraton.feature.container;

import java.util.List;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.mixin.accessor.ScreenAccessor;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.OverlayUtil;
import net.volcaronitee.taraton.util.OverlayUtil.LineContent;
import net.volcaronitee.taraton.util.OverlayUtil.Overlay;

public class Searchbar {
    private static final Searchbar INSTANCE = new Searchbar();

    private static final int SEARCHBAR_WIDTH = 192;
    private static final int SEARCHBAR_HEIGHT = 16;

    private static final Overlay OVERLAY = OverlayUtil.createOverlay("searchbar",
            () -> FeatureUtil.isEnabled(TaratonConfig.getInstance().container.searchbar),
            List.of(new LineContent("Searchbar Placeholder", () -> true)));
    static {
        OVERLAY.setFixedSize(SEARCHBAR_WIDTH, SEARCHBAR_HEIGHT);
        OVERLAY.setSpecialRender((context, delta) -> {
        });
        OVERLAY.setOnContainer(true);
    }

    private static TextFieldWidget searchbar;

    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof GenericContainerScreen) {
                if (searchbar == null) {
                    searchbar = new TextFieldWidget(client.textRenderer, 0, 0, SEARCHBAR_WIDTH,
                            SEARCHBAR_HEIGHT, Text.literal("Search..."));
                }

                searchbar.setX(OVERLAY.getX());
                searchbar.setY(OVERLAY.getY());

                ((ScreenAccessor) screen).invokeAddDrawableChild(searchbar);
            }

            ScreenEvents.remove(screen).register(INSTANCE::onScreenClose);
        });
    }

    public static String getText() {
        return searchbar != null ? searchbar.getText() : "";
    }

    private void onScreenClose(Screen screen) {
        searchbar.setFocused(false);
    }
}
