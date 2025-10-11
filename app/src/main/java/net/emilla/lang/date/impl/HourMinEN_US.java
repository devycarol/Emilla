package net.emilla.lang.date.impl;

import android.content.Context;
import android.text.format.DateFormat;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;
import net.emilla.lang.date.HourMin;

import java.util.Calendar;

public record HourMinEN_US(int hour24, int minute) implements HourMin {

    public static final String REGEX = "(?i)([01]?[0-9]|2[0-3])(:?[0-5][0-9])? *([AP]M?)?";
    private static final String RGX_AM = "(?i).*\\d *A.*";
    private static final String RGX_PM = "(?i).*\\d *P.*";

    public static HourMin instance(String timeStr, Context ctx, @StringRes int errorTitle) {
        int meridiem;
        if (timeStr.matches(RGX_AM)) meridiem = Calendar.AM;
        else if (timeStr.matches(RGX_PM)) meridiem = Calendar.PM;
        else meridiem = -1;

        timeStr = timeStr.replaceAll("\\D", "");
        if (!timeStr.matches("\\d{1,4}")) {
            throw new EmillaException(errorTitle, R.string.error_invalid_time);
        }

        int hour;
        int minute;
        int len = timeStr.length();
        if (len > 2) {
            hour = Integer.parseInt(timeStr.substring(0, len - 2));
            minute = Integer.parseInt(timeStr.substring(len - 2));
        } else {
            hour = Integer.parseInt(timeStr);
            minute = 0;
        }

        if (meridiem == -1) { // meridiem wasn't specified
            if (1 <= hour && hour <= 12 && !DateFormat.is24HourFormat(ctx)) {
                // don't change to the soonest occurrence if the device uses 24-hour time.
                if (isFlip(hour, minute)) {
                    hour = (hour + 12) % 24;
                    // flip the meridiem: advance the hour by 12 and wrap back to 0 if it reaches 24.
                }
            } // 24-time hours 0 and 13+ are left unchanged
        } else { // meridiem was specified
            if (hour < 1 || 12 < hour) {
                throw new EmillaException(errorTitle, R.string.error_invalid_time);
            }
            // hours outside of [1, 12] are invalid.
            if (hour == 12) hour = 0;
            // 12 AM is 0, 12 PM is 12.
            if (meridiem == Calendar.PM) hour += 12;
            // advance hour by 12 to convert to PM.
        }

        if (hour > 23 || minute > 59) {
            throw new EmillaException(errorTitle, R.string.error_invalid_time);
        }

        return new HourMinEN_US(hour, minute);
    }

    private static boolean isFlip(int hour, int minute) {
        var cal = Calendar.getInstance();
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        if (currentHour == 0) currentHour = 24;
        // handle 12am as "24pm"
        int currentMinute = cal.get(Calendar.MINUTE);
        boolean flip;
        if (currentHour <= 12) {
            flip = hour < currentHour || hour == currentHour && minute <= currentMinute;
            // beginning 1am until 1pm, we need to flip the meridiem of the given time if
            // it's earlier or equal to the current time.
        } else {
            currentHour -= 12;
            flip = hour > currentHour || hour == currentHour && minute > currentMinute;
            // beginning 1pm until 1am, we need to flip the meridiem of the given time if
            // it's later than the time 12 hours ago.
        }
        return flip;
    }
}
