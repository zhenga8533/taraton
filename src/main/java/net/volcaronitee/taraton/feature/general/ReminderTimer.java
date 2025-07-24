package net.volcaronitee.taraton.feature.general;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.util.CronExpression;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonList;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.ParseUtil;
import net.volcaronitee.taraton.util.TickUtil;
import net.volcaronitee.taraton.util.TitleUtil;

/**
 * Feature that provides a reminder timer system, allowing players to set reminders based on time
 * intervals or cron expressions.
 */
public class ReminderTimer {
    private static final ReminderTimer INSTANCE = new ReminderTimer();

    public static final TaratonList REMINDER_MAP =
            new TaratonList("Reminder Map", Text.literal("A list of reminders for the player."),
                    "reminder_map.json", new String[] {"Message", "Time"});
    static {
        REMINDER_MAP.setIsMap(true);
        REMINDER_MAP.setSaveCallback(INSTANCE::onSave);
    }

    private static final Map<String, Long> TIME_DURATIONS = new HashMap<>();
    private static final Map<String, Long> TIME_TRIGGERS = new HashMap<>();
    private static final Map<String, CronExpression> CRON_EXPRESSIONS = new HashMap<>();
    private static final Map<String, ZonedDateTime> LAST_TRIGGERS = new HashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private ReminderTimer() {}

    /**
     * Registers the reminder timer feature to run on a regular tick interval.
     */
    public static void register() {
        TickUtil.register(INSTANCE::onTick, 20);
    }

    /**
     * Callback method that is called every tick to check for reminders.
     * 
     * @param client The Minecraft client instance.
     */
    private void onTick(MinecraftClient client) {
        if (!FeatureUtil.isEnabled(TaratonConfig.getInstance().general.reminderTimer)
                || client == null || client.world == null) {
            return;
        }

        checkTimeReminders();
        checkCronReminders();
    }

    /**
     * Checks for time-based reminders and triggers them if the current time matches the reminder's
     * trigger time.
     */
    private void checkTimeReminders() {
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : new HashMap<>(TIME_TRIGGERS).entrySet()) {
            String reminder = entry.getKey();
            long nextTriggerTime = entry.getValue();

            if (currentTime >= nextTriggerTime) {
                TitleUtil.createTitle(reminder, "", 9);
                long duration = TIME_DURATIONS.get(reminder);
                TIME_TRIGGERS.put(reminder, nextTriggerTime + duration);
            }
        }
    }

    /**
     * Checks for cron-based reminders and triggers them if the current time matches the next
     * expected time for the cron expression.
     */
    private void checkCronReminders() {
        ZonedDateTime nowZoned = ZonedDateTime.now(ZoneId.systemDefault());
        for (Map.Entry<String, CronExpression> entry : CRON_EXPRESSIONS.entrySet()) {
            String reminderName = entry.getKey();
            CronExpression cronExpr = entry.getValue();

            // Get the last trigger time for this reminder and the next expected time
            ZonedDateTime lastTrigger = LAST_TRIGGERS.getOrDefault(reminderName,
                    ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));

            Date nextExpectedDate =
                    cronExpr.getNextValidTimeAfter(Date.from(lastTrigger.toInstant()));

            // Check if the next expected date is null, which means the cron expression is invalid
            if (nextExpectedDate != null) {
                ZonedDateTime nextExpectedZoned =
                        nextExpectedDate.toInstant().atZone(ZoneId.systemDefault());

                // Check if the current time is equal to or after the next expected time
                if ((nowZoned.isEqual(nextExpectedZoned) || nowZoned.isAfter(nextExpectedZoned))
                        && !nowZoned.isEqual(lastTrigger)) {
                    TitleUtil.createTitle(reminderName, "", 9);
                    LAST_TRIGGERS.put(reminderName, nowZoned);
                }
            }
        }
    }

    /**
     * Callback to save the reminders when the map is modified.
     */
    private void onSave() {
        TIME_DURATIONS.clear();
        TIME_TRIGGERS.clear();
        CRON_EXPRESSIONS.clear();
        LAST_TRIGGERS.clear();

        // Iterate through the reminder map and parse each entry
        for (Map.Entry<String, String> entry : REMINDER_MAP.map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            long seconds = ParseUtil.parseTime(value);
            if (seconds > 0) {
                long durationMillis = seconds * 1000;
                TIME_DURATIONS.put(key, durationMillis);
                // Set the initial trigger time
                TIME_TRIGGERS.put(key, System.currentTimeMillis() + durationMillis);
                continue;
            }

            CronExpression cronExpr = ParseUtil.parseCron(value);
            if (cronExpr != null) {
                CRON_EXPRESSIONS.put(key, cronExpr);
                LAST_TRIGGERS.put(key, ZonedDateTime.now(ZoneId.systemDefault()).minusDays(1));
            }
        }
    }
}
