package net.volcaronitee.taraton.feature.general;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.core.util.CronExpression;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonList;
import net.volcaronitee.taraton.util.TickUtil;
import net.volcaronitee.taraton.util.TitleUtil;

/**
 * Feature that provides a reminder timer system, allowing players to set reminders based on time
 * intervals or cron expressions.
 */
public class ReminderTimer {
    private static final ReminderTimer INSTANCE = new ReminderTimer();

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)([hms])");

    private static final TaratonList REMINDER_MAP = new TaratonList("Reminder Map",
            Text.literal("A list of reminders for the player."), "reminder_map.json");
    static {
        REMINDER_MAP.setIsMap(true);
        REMINDER_MAP.setSaveCallback(INSTANCE::onSave);
    }

    private static final Map<String, Long> TIME_REMINDERS = new HashMap<>();
    private static final Map<String, CronExpression> CRON_EXPRESSIONS = new HashMap<>();
    private static final Map<String, ZonedDateTime> LAST_TRIGGERS = new HashMap<>();

    private long elapsedSeconds = 0;

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
     * Callback method that is called every tick to check for reminders and display them.
     * 
     * @param client The Minecraft client instance.
     */
    private void onTick(MinecraftClient client) {
        if (!TaratonConfig.getHandler().general.reminderTimer || client == null
                || client.world == null) {
            return;
        }

        elapsedSeconds++;
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());

        for (Map.Entry<String, Long> entry : TIME_REMINDERS.entrySet()) {
            String reminder = entry.getKey();
            long initialDurationSeconds = entry.getValue();

            if (elapsedSeconds > 0 && elapsedSeconds % initialDurationSeconds == 0) {
                TitleUtil.createTitle(reminder, "", 9);
            }
        }

        for (Map.Entry<String, CronExpression> entry : CRON_EXPRESSIONS.entrySet()) {
            String reminderName = entry.getKey();
            CronExpression cronExpr = entry.getValue();

            ZonedDateTime lastTrigger = LAST_TRIGGERS.getOrDefault(reminderName,
                    ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));

            Date nextExpectedDate =
                    cronExpr.getNextValidTimeAfter(Date.from(lastTrigger.toInstant()));

            if (nextExpectedDate != null) {
                ZonedDateTime nextExpectedZoned =
                        nextExpectedDate.toInstant().atZone(ZoneId.systemDefault());

                if ((now.isEqual(nextExpectedZoned) || now.isAfter(nextExpectedZoned))
                        && !now.isEqual(lastTrigger)) {
                    TitleUtil.createTitle(reminderName, "", 9);
                    LAST_TRIGGERS.put(reminderName, now);
                }
            }
        }
    }

    /**
     * Parses a time duration string into seconds.
     * 
     * @param value The time duration string to parse, e.g., "1h 30m 15s".
     * @return The total duration in seconds, or -1 if the string is invalid.
     */
    private long parseTime(String value) {
        String cleanedString = value.trim().toLowerCase().replaceAll("\\s", "");
        Matcher matcher = TIME_PATTERN.matcher(cleanedString);
        long seconds = 0;
        int lastMatchEnd = 0;
        boolean foundAnyUnit = false;

        while (matcher.find()) {
            if (matcher.start() > lastMatchEnd) {
                return -1;
            }
            foundAnyUnit = true;
            long timeValue = Long.parseLong(matcher.group(1));
            String timeUnit = matcher.group(2);

            switch (timeUnit) {
                case "h":
                    seconds += timeValue * 3600;
                    break;
                case "m":
                    seconds += timeValue * 60;
                    break;
                case "s":
                    seconds += timeValue;
                    break;
            }
            lastMatchEnd = matcher.end();
        }

        if (lastMatchEnd < cleanedString.length()
                || (seconds == 0 && !foundAnyUnit && !cleanedString.isEmpty())) {
            return -1;
        }

        return seconds;
    }

    /**
     * Parses a cron expression from a string.
     * 
     * @param cronString The cron expression string to parse.
     * @return A CronExpression object if valid, null otherwise.
     */
    private CronExpression parseCron(String cronString) {
        try {
            return new CronExpression(cronString);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Callback to save the reminders when the map is modified.
     */
    private void onSave() {
        TIME_REMINDERS.clear();
        CRON_EXPRESSIONS.clear();
        LAST_TRIGGERS.clear();
        elapsedSeconds = 0;

        // Iterate through the reminder map and parse each entry
        for (Map.Entry<String, String> entry : REMINDER_MAP.map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Try parsing the value as a time duration
            long seconds = parseTime(value);
            if (seconds > 0) {
                TIME_REMINDERS.put(key, seconds);
                continue;
            }

            // If parsing time failed, try parsing as a cron expression
            CronExpression cronExpr = parseCron(value);
            if (cronExpr != null) {
                CRON_EXPRESSIONS.put(key, cronExpr);
                LAST_TRIGGERS.put(key, ZonedDateTime.now(ZoneId.systemDefault()).minusDays(1));
            }
        }
    }
}
