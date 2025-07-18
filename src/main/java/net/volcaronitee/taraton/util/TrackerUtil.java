package net.volcaronitee.taraton.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for tracking rates of change for various stats in the game.
 */
public class TrackerUtil {
    private static final List<RateTracker> trackers = new ArrayList<>();

    private static boolean paused = false;

    /**
     * Creates a new RateTracker instance and adds it to the list of trackers.
     * 
     * @return A new RateTracker instance that can be used to track rates of change for a specific
     *         stat.
     */
    public static RateTracker createRateTracker() {
        RateTracker tracker = new RateTracker();
        trackers.add(tracker);
        return tracker;
    }

    /**
     * Checks if the tracker is currently paused.
     * 
     * @return True if the tracker is paused, false otherwise.
     */
    public static boolean isPaused() {
        return paused;
    }

    /**
     * Toggles the paused state of all trackers. When paused, the elapsed time and rate calculations
     */
    public static void pause() {
        paused = !paused;
        if (paused) {
            long currentTime = System.currentTimeMillis();
            for (RateTracker tracker : trackers) {
                tracker.pauseTime = currentTime;
            }
        } else {
            long pauseDuration = System.currentTimeMillis() - trackers.get(0).pauseTime;
            for (RateTracker tracker : trackers) {
                tracker.totalPauseTime += pauseDuration;
            }
        }
    }

    /**
     * A class to track the rate of change of a specific stat over time.
     */
    public static class RateTracker {
        private long startTime;
        private long lastUpdateTime;
        private double startValue;
        private double currentValue;
        private double nextValue;

        // Fields to handle pausing
        private long pauseTime;
        private long totalPauseTime;

        /**
         * Constructs a new RateTracker, initially inactive.
         */
        public RateTracker() {
            this.startTime = 0;
            this.lastUpdateTime = 0;
            this.startValue = 0;
            this.currentValue = 0;
            this.nextValue = 0;

            this.pauseTime = 0;
            this.totalPauseTime = 0;
        }

        /**
         * Updates the current value of the stat being tracked. This should be called whenever the
         * value changes.
         *
         * @param newValue The new value of the stat.
         */
        public void update(double newValue) {
            if (nextValue != 0 && newValue < startValue) {

            }
        }

        /**
         * Checks if the tracker is currently active.
         * 
         * @param timeThreshold The time threshold in milliseconds to consider the tracker active.
         * @return True if the tracker is active, false otherwise.
         */
        public boolean isActive(int timeThreshold) {
            if (this.startTime == 0) {
                return false;
            }

            long sinceUpdate = System.currentTimeMillis() - this.lastUpdateTime;
            return sinceUpdate < timeThreshold;
        }

        /**
         * Gets the total amount gained or lost since the tracker started.
         *
         * @return The difference between the current value and the start value.
         */
        public double getTotalGained() {
            return this.currentValue - this.startValue;
        }

        /**
         * Gets the elapsed time since the tracker started, excluding any paused time.
         * 
         * @return The elapsed time in milliseconds since the tracker started, minus any paused
         *         time.
         */
        public long getElapsedTime() {
            if (this.startTime == 0) {
                return 0;
            }

            long currentTime = System.currentTimeMillis();
            return currentTime - this.startTime - this.totalPauseTime;
        }

        /**
         * Calculates the rate of change per second.
         *
         * @return The amount gained or lost per second.
         */
        public double getRatePerSecond() {
            long elapsedTime = getElapsedTime();
            if (elapsedTime <= 0) {
                return 0.0;
            }
            return getTotalGained() / (elapsedTime / 1000.0);
        }

        /**
         * Calculates the rate of change per minute.
         *
         * @return The amount gained or lost per minute.
         */
        public double getRatePerMinute() {
            return getRatePerSecond() * 60;
        }

        /**
         * Calculates the rate of change per hour.
         *
         * @return The amount gained or lost per hour.
         */
        public double getRatePerHour() {
            return getRatePerMinute() * 60;
        }
    }
}
