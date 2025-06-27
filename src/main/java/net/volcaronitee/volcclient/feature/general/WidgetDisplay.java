package net.volcaronitee.volcclient.feature.general;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.ListUtil;
import net.volcaronitee.volcclient.util.OverlayUtil;
import net.volcaronitee.volcclient.util.OverlayUtil.LineContent;

public class WidgetDisplay {
    private static final WidgetDisplay INSTANCE = new WidgetDisplay();

    public static final ListUtil WIDGET_LIST = new ListUtil("Widget List",
            Text.literal("A list of widgets to display in the overlay."), "widget_list.json", null,
            null);

    private static final Set<Widget> WIDGETS = new HashSet<>();

    static {
        for (String widget : WIDGET_LIST.getHandler().list) {
            WIDGETS.add(new Widget(widget));
        }
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private WidgetDisplay() {}

    /**
     * Registers the widget display feature to listen for client tick events.
     */
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(INSTANCE::updateWidgets);
    }

    /**
     * Updates the widget display overlay based on the current configuration.
     * 
     * @param client The Minecraft client instance.
     */
    private void updateWidgets(MinecraftClient client) {
        PlayerListEntry[] tablist =
                client.getNetworkHandler().getPlayerList().toArray(new PlayerListEntry[0]);

        Widget addToWidget = null;

        for (PlayerListEntry tab : tablist) {
            tab.getDisplayName();
            String text = tab.getDisplayName().getString();
            System.out.println(text);

            for (Widget widget : WIDGETS) {
                // TODO: Implement logic
            }

            if (addToWidget != null) {
                addToWidget.lines.add(new LineContent(text));
            }
        }
    }

    /**
     * Widget is a class that represents a single widget in the overlay.
     */
    private static class Widget {
        private static List<LineContent> lines;

        /**
         * Constructor for Widget.
         * 
         * @param name The name of the widget to display.
         */
        public Widget(String name) {
            Supplier<Boolean> isEnabled = () -> WIDGET_LIST.getHandler().list.contains(name);
            lines = List.of(new LineContent("§b§l" + name + ":", isEnabled),
                    new LineContent(" Placeholder", isEnabled));

            OverlayUtil.createOverlay(name, () -> WIDGET_LIST.getHandler().list.contains(name),
                    lines);
        }
    }
}
