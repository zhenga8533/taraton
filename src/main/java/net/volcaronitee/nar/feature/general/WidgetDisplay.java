package net.volcaronitee.nar.feature.general;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.volcaronitee.nar.config.controller.KeyValueController.KeyValuePair;
import net.volcaronitee.nar.util.ListUtil;
import net.volcaronitee.nar.util.OverlayUtil;
import net.volcaronitee.nar.util.OverlayUtil.LineContent;
import net.volcaronitee.nar.util.TablistUtil;
import net.volcaronitee.nar.util.TickUtil;

/**
 * Feature to display widgets in the overlay based on player list entries.
 */
public class WidgetDisplay {
    private static final WidgetDisplay INSTANCE = new WidgetDisplay();

    public static final ListUtil WIDGET_LIST = new ListUtil("Widget List",
            Text.literal("A list of widgets to display in the overlay."), "widget_list.json");

    private static final Map<String, Widget> WIDGETS = new java.util.HashMap<>();

    static {
        WIDGET_LIST.setSaveCallback(INSTANCE::onSave);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private WidgetDisplay() {}

    /**
     * Registers the widget display feature to listen for client tick events.
     */
    public static void register() {
        TickUtil.register(INSTANCE::updateWidgets, 10);
    }

    /**
     * Updates the widget display overlay based on the current configuration.
     * 
     * @param client The Minecraft client instance.
     */
    private void updateWidgets(MinecraftClient client) {
        // Loop through the player list entries and populate the widgets
        Widget addToWidget = null;
        for (PlayerListEntry entry : TablistUtil.getInstance().getTablist()) {
            Text displayName = entry.getDisplayName();
            if (displayName == null) {
                continue;
            }

            String name = displayName.getString();
            if (addToWidget == null) {
                // Find the first active widget that matches the name
                for (Widget widget : WIDGETS.values()) {
                    if (name.startsWith(widget.name)) {
                        addToWidget = widget;
                        addToWidget.lines.clear();
                        addToWidget.lines.add(new LineContent(displayName, () -> true));
                        break;
                    }
                }
            } else {
                // If we already have a widget, add widget details to it
                if (name.length() > 1 && name.charAt(0) == ' ' && name.charAt(1) != ' ') {
                    addToWidget.lines.add(new LineContent(displayName, () -> true));
                } else {
                    addToWidget = null;
                }
            }
        }

    }

    /**
     * Callback to save the spam patterns when the list is modified.
     */
    private void onSave() {
        for (KeyValuePair<String, Boolean> widget : WIDGET_LIST.getHandler().list) {
            // Skip widgets that are disabled
            String key = widget.getKey();
            if (!widget.getValue() || key.isEmpty() || WIDGETS.containsKey(key)) {
                continue;
            }

            WIDGETS.put(key, new Widget(key));
        }
    }

    /**
     * Widget is a class that represents a single widget in the overlay.
     */
    private static class Widget {
        private List<LineContent> lines;
        private final String name;
        private static final Supplier<Boolean> active = () -> true;

        /**
         * Constructor for Widget.
         * 
         * @param name The name of the widget to display.
         */
        public Widget(String name) {
            this.name = name;
            this.lines = new ArrayList<>(List.of(new LineContent("§e§l" + name + ":", active),
                    new LineContent(" Tall: §c❁100", active),
                    new LineContent(" Handsome: §9☣100", active)));

            OverlayUtil.createOverlay(name, active, lines);
        }
    }
}
