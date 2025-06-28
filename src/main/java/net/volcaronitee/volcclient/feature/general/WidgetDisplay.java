package net.volcaronitee.volcclient.feature.general;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.volcaronitee.volcclient.config.controller.KeyValueController.KeyValuePair;
import net.volcaronitee.volcclient.util.ListUtil;
import net.volcaronitee.volcclient.util.OverlayUtil;
import net.volcaronitee.volcclient.util.OverlayUtil.LineContent;

/**
 * Feature to display widgets in the overlay based on player list entries.
 */
public class WidgetDisplay {
    private static final WidgetDisplay INSTANCE = new WidgetDisplay();

    private static final Ordering<PlayerListEntry> PLAYER_COMPARATOR =
            Ordering.from(new PlayerComparator());

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
        ClientTickEvents.END_CLIENT_TICK.register(INSTANCE::updateWidgets);
    }

    /**
     * Updates the widget display overlay based on the current configuration.
     * 
     * @param client The Minecraft client instance.
     */
    private void updateWidgets(MinecraftClient client) {
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler == null || networkHandler.getPlayerList() == null) {
            return;
        }

        // Sort the player list entries based on the comparator
        List<PlayerListEntry> tablist = new ArrayList<>(networkHandler.getPlayerList());
        tablist.sort(PLAYER_COMPARATOR);

        // Loop through the player list entries and populate the widgets
        Widget addToWidget = null;
        for (PlayerListEntry entry : tablist) {
            Text displayName = entry.getDisplayName();
            if (displayName == null) {
                continue;
            }

            String name = displayName.getString();
            if (addToWidget == null) {
                // Find the first active widget that matches the name
                for (Widget widget : WIDGETS.values()) {
                    if (widget.active.get() && name.startsWith(widget.name)) {
                        addToWidget = widget;
                        addToWidget.lines.clear();
                        addToWidget.lines.add(new LineContent(displayName));
                        break;
                    }
                }
            } else {
                // If we already have a widget, add widget details to it
                if (name.length() > 1 && name.charAt(0) == ' ' && name.charAt(1) != ' ') {
                    addToWidget.lines.add(new LineContent(displayName));
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
        private final Supplier<Boolean> active;

        /**
         * Constructor for Widget.
         * 
         * @param name The name of the widget to display.
         */
        public Widget(String name) {
            this.name = name;
            this.lines = new ArrayList<>(
                    List.of(new LineContent("§b§l" + name + ":"), new LineContent("Placeholder")));
            this.active = () -> WIDGET_LIST.getHandler().list.stream()
                    .anyMatch(pair -> pair.getKey().equals(name) && pair.getValue());

            OverlayUtil.createOverlay(name, this.active, lines);
        }
    }

    /**
     * Comparator for PlayerListEntry objects to sort players in the tab list.
     */
    private static class PlayerComparator implements Comparator<PlayerListEntry> {
        @Override
        public int compare(PlayerListEntry playerOne, PlayerListEntry playerTwo) {
            // Get player teams
            Team teamOne = playerOne.getScoreboardTeam();
            Team teamTwo = playerTwo.getScoreboardTeam();

            // Compare game modes (spectators usually last)
            boolean playerOneNotSpectator = playerOne.getGameMode() != GameMode.SPECTATOR;
            boolean playerTwoNotSpectator = playerTwo.getGameMode() != GameMode.SPECTATOR;

            return ComparisonChain.start()
                    .compareTrueFirst(playerOneNotSpectator, playerTwoNotSpectator)
                    .compare(teamOne != null ? teamOne.getName() : "",
                            teamTwo != null ? teamTwo.getName() : "")
                    .compare(playerOne.getProfile().getName(), playerTwo.getProfile().getName())
                    .result();
        }
    }
}
