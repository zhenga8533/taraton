package net.volcaronitee.volcclient.util;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.Pair;

/**
 * ScheduleUtil is a utility class for scheduling tasks to run after a specified number of ticks.
 */
public class ScheduleUtil {
    private static List<Pair<Runnable, Integer>> tasks = new ArrayList<>();

    /**
     * Initializes the ScheduleUtil by registering a server tick event listener.
     */
    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
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
}
