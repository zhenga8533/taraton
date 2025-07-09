package net.volcaronitee.nar.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import net.volcaronitee.nar.mixin.accessor.InGameHudAccessor;

/**
 * Utility class for managing and displaying titles in the game.
 */
public class TitleUtil {
    private static Title currentTitle = null;

    /**
     * Initializes the TitleUtil by registering a client tick event listener.
     */
    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (currentTitle == null) {
                return;
            }

            currentTitle.aliveTime--;
            if (currentTitle.aliveTime <= 0) {
                clearTitle();
            }
        });
    }

    /**
     * Adds a title to be displayed in the game. If a title with the same text already exists, it
     * will be refreshed.
     * 
     * @param title The title text to display.
     * @param subtitle The subtitle text to display.
     * @param priority The priority of the title, used to determine which title to display if
     *        multiple titles are active.
     * @param fadeIn Time in ticks needed for the title to fade in.
     * @param stay Time in ticks the title will stay on screen.
     * @param fadeOut Time in ticks needed for the title to fade out.
     */
    public static void createTitle(String title, String subtitle, int priority, int fadeIn,
            int stay, int fadeOut) {
        // Check if the title already exists and refresh it if so
        String key = title + subtitle;
        if (currentTitle != null && currentTitle.key.equals(key)) {
            currentTitle.refresh();
            return;
        }

        // Create a new Title object and add it to the map
        currentTitle = new Title(title, subtitle, priority, fadeIn, stay, fadeOut);
        currentTitle.refresh();
    }

    /**
     * Creates a title with default fade-in, stay, and fade-out times.
     * 
     * @param title The title text to display.
     * @param subtitle The subtitle text to display.
     * @param priority The priority of the title, used to determine which title to display if
     */
    public static void createTitle(String title, String subtitle, int priority) {
        createTitle(title, subtitle, priority, 10, 70, 20);
    }

    /**
     * Clears the currently displayed title if it matches the current title.
     */
    public static void clearTitle() {
        InGameHud hud = MinecraftClient.getInstance().inGameHud;
        if (hud != null && ((InGameHudAccessor) hud).getTitle() == currentTitle.title) {
            hud.clearTitle();
        }
        currentTitle = null;
    }

    /**
     * Class representing a title to be displayed in the game.
     */
    private static class Title {
        private Text title;
        private Text subtitle;
        private String key;
        private int aliveTime;

        private int fadeIn;
        private int stay;
        private int fadeOut;
        private int priority;

        /**
         * Constructor for creating a new Title instance.
         * 
         * @param title The title text to display.
         * @param subtitle The subtitle text to display.
         * @param priority The priority of the title, used to determine which title to display if
         *        multiple titles are active.
         * @param fadeIn Time in ticks needed for the title to fade in.
         * @param stay Time in ticks the title will stay on screen.
         * @param fadeOut Time in ticks needed for the title to fade out.
         */
        private Title(String title, String subtitle, int priority, int fadeIn, int stay,
                int fadeOut) {
            this.title = Text.of(title);
            this.subtitle = Text.of(subtitle);
            this.key = title + subtitle;
            this.priority = priority;
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
        }

        /**
         * Refreshes the title display in the game.
         */
        private void refresh() {
            MinecraftClient client = MinecraftClient.getInstance();
            aliveTime = fadeIn + stay + fadeOut;

            if (client != null && client.inGameHud != null
                    && currentTitle.priority <= this.priority) {
                client.inGameHud.setTitleTicks(fadeIn, stay, fadeOut);
                client.inGameHud.setTitle(title);
                client.inGameHud.setSubtitle(subtitle);
            }
        }
    }
}
