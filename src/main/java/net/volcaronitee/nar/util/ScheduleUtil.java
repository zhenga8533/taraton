package net.volcaronitee.nar.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.Pair;

/**
 * ScheduleUtil is a utility class for scheduling tasks to run after a specified number of ticks.
 */
public class ScheduleUtil {
    private static final List<Pair<Runnable, Integer>> activeTasks = new ArrayList<>();
    private static final Queue<Pair<Runnable, Integer>> pendingTasks =
            new ConcurrentLinkedQueue<>();

    /**
     * Initializes the ScheduleUtil by registering a server tick event listener.
     */
    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Add all newly scheduled tasks from the thread-safe queue to the active list
            activeTasks.addAll(pendingTasks);
            pendingTasks.clear();

            // Use an iterator to safely remove tasks while iterating
            Iterator<Pair<Runnable, Integer>> iterator = activeTasks.iterator();
            while (iterator.hasNext()) {
                Pair<Runnable, Integer> task = iterator.next();
                task.setRight(task.getRight() - 1);

                if (task.getRight() <= 0) {
                    task.getLeft().run();
                    iterator.remove();
                }
            }
        });
    }

    /**
     * Schedules a runnable to be executed after a specified number of ticks.
     * 
     * @param runnable The runnable to execute.
     * @param ticks The number of ticks to wait before executing the runnable.
     */
    public static void schedule(Runnable runnable, int ticks) {
        pendingTasks.add(new Pair<>(runnable, ticks));
    }

    /**
     * Schedules a command to be executed after a delay.
     * 
     * @param command The command to be executed.
     * @param delay The delay in ticks before executing the command.
     */
    public static void scheduleCommand(String command, int delay) {
        // Send the command to the player network handler
        ScheduleUtil.schedule(() -> {
            ClientPlayNetworkHandler networkHandler =
                    MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler != null) {
                // The command string should not include the slash
                String formattedCommand = command.startsWith("/") ? command.substring(1) : command;
                networkHandler.sendChatCommand(formattedCommand);
            }
        }, delay);
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
