package net.volcaronitee.nar.util;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.Pair;

/**
 * ScheduleUtil is a utility class for scheduling tasks to run after a specified number of ticks.
 */
public class ScheduleUtil {
    private static List<Pair<Runnable, Integer>> tasks = new ArrayList<>();
    private static int totalDelay = 0;

    /**
     * Initializes the ScheduleUtil by registering a server tick event listener.
     */
    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            List<Pair<Runnable, Integer>> toRemove = new ArrayList<>();
            for (Pair<Runnable, Integer> task : tasks) {
                task.setRight(task.getRight() - 1);
                if (task.getRight() <= 0) {
                    task.getLeft().run();
                    toRemove.add(task);
                }
            }
            tasks.removeAll(toRemove);
        });
    }

    /**
     * Schedules a runnable to be executed after a specified number of ticks.
     * 
     * @param runnable The runnable to execute.
     * @param ticks The number of ticks to wait before executing the runnable.
     */
    public static void schedule(Runnable runnable, int ticks) {
        tasks.add(new Pair<>(runnable, ticks));
    }

    /**
     * Schedules a command to be executed after a delay.
     * 
     * @param command The command to be executed.
     * @param delay The delay in ticks before executing the command.
     */
    public static void scheduleCommand(String command, int delay) {
        // Send the command to the player network handler
        totalDelay += delay;
        ScheduleUtil.schedule(() -> {
            ClientPlayNetworkHandler networkHandler =
                    MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler != null) {
                String formattedCommand = command.startsWith("/") ? command.substring(1) : command;
                networkHandler.sendChatCommand(formattedCommand);
            }
            totalDelay -= delay;
        }, totalDelay);
    }

    /**
     * Schedules a command to be executed after a delay.
     * 
     * @param command The command to be executed.
     */
    public static void scheduleCommand(String command) {
        scheduleCommand(command, 6);
    }
}
