package net.volcaronitee.nar.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

/**
 * Utility class for managing tick-based functions in Minecraft.
 */
public class TickUtil {
    private static final List<TickFunction> TICK_FUNCTIONS = new ArrayList<>();

    /**
     * Initializes the TickUtil by registering a client tick event listener.
     */
    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (TickFunction entry : TICK_FUNCTIONS) {
                entry.tick(client);
            }
        });
    }

    /**
     * Registers a function to be executed on each tick at a specified rate.
     * 
     * @param function The function to execute on each tick.
     * @param tickRate The number of ticks to wait before executing the function again.
     */
    public static void register(Consumer<MinecraftClient> function, int tickRate) {
        TICK_FUNCTIONS.add(new TickFunction(function, tickRate));
    }

    /**
     * A class representing a tick function that can be executed at a specified rate.
     */
    private static class TickFunction {
        final Consumer<MinecraftClient> function;
        final int tickRate;
        int tickCounter;

        /**
         * Constructor for TickFunction.
         * 
         * @param function The function to execute on each tick.
         * @param tickRate The number of ticks to wait before executing the function again.
         */
        TickFunction(Consumer<MinecraftClient> function, int tickRate) {
            this.function = function;
            this.tickRate = Math.max(1, tickRate);
            this.tickCounter = 0;
        }

        /**
         * Attempts to execute the function based on the tick rate.
         * 
         * @param client The Minecraft client instance.
         */
        void tick(MinecraftClient client) {
            tickCounter++;
            if (tickCounter >= tickRate) {
                function.accept(client);
                tickCounter = 0;
            }
        }
    }
}
