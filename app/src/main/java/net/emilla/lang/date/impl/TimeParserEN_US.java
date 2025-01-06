package net.emilla.lang.date.impl;

import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.lang.date.TimeParser;

import java.util.Calendar;

public record TimeParserEN_US(int hour24, int minute) implements TimeParser {

    private static final String
            RGX_AM = "(?i).*\\d *A.*",
            RGX_PM = "(?i).*\\d *P.*";

    public static TimeParser instance(String timeStr) {
        int meridiem;
        if (timeStr.matches(RGX_AM)) meridiem = Calendar.AM;
        else if (timeStr.matches(RGX_PM)) meridiem = Calendar.PM;
        else meridiem = -1;

        timeStr = timeStr.replaceAll("\\D", "");
        if (!timeStr.matches("\\d{1,4}")) {
            throw new EmlaBadCommandException(R.string.error, R.string.error_invalid_time);
        }

        int hour, minute;
        int len = timeStr.length();
        if (len > 2) {
            hour = Integer.parseInt(timeStr.substring(0, len - 2));
            minute = Integer.parseInt(timeStr.substring(len - 2));
        } else {
            hour = Integer.parseInt(timeStr);
            minute = 0;
        }

        if (meridiem == -1) {
            if (1 <= hour && hour <= 12) {
                Calendar cal = Calendar.getInstance();
                int currentHour = cal.get(Calendar.HOUR_OF_DAY);
                if (currentHour == 0) currentHour = 24;
                // handle 12am as "24pm"
                int currentMinute = cal.get(Calendar.MINUTE);
                boolean flip;
                if (currentHour < 13) {
                    flip = hour < currentHour || hour == currentHour && minute <= currentMinute;
                    // beginning 1am until 1pm, we need to flip the meridiem of the given time if
                    // it's earlier or equal to the current time.
                } else {
                    currentHour -= 12;
                    flip = hour > currentHour || hour == currentHour && minute > currentMinute;
                    // beginning 1pm until 1am, we need to flip the meridiem of the given time if
                    // it's later than the time 12 hours ago.
                }

                if (flip) hour = (hour + 12) % 24;
                // flip the meridiem: advance the hour by 12 and wrap back to 0 if it reaches 24.
                // Todo lang: don't do this if the user wants 24-hour time. Have a setting for
                //  how to handle times given without AM/PM: follow system, soonest occurrence,
                //  and 24-hour.
            } // 24-hour times (0 and 13+) are left unchanged
        } else {
            if (hour < 1 || 12 < hour) throw new EmlaBadCommandException(R.string.error, R.string.error_invalid_time);
            if (hour == 12) hour = 0;
            if (meridiem == Calendar.PM) hour += 12;
        }

        if (hour > 23 || minute > 59) throw new EmlaBadCommandException(R.string.error, R.string.error_invalid_time);

        return new TimeParserEN_US(hour, minute);
    }
}
