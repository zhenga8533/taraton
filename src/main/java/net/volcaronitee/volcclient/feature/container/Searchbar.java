package net.volcaronitee.volcclient.feature.container;

import java.util.List;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.OverlayUtil;
import net.volcaronitee.volcclient.util.OverlayUtil.Overlay;
import net.volcaronitee.volcclient.util.OverlayUtil.SpecialRender;

public class Searchbar {
    private TextFieldWidget searchbar = null;
    private static final SpecialRender render = context -> {

    };
    private static final Overlay overlay = OverlayUtil.createOverlay("searchbar",
            () -> ConfigUtil.getHandler().container.searchbar, List.of());

    static {
        overlay.setSpecialRender(render);
    }

    public static void register() {

    }
}
